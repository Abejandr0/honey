const crypto = require("node:crypto");
const admin = require("firebase-admin");
const { onCall, HttpsError } = require("firebase-functions/v2/https");
const { setGlobalOptions } = require("firebase-functions/v2");

admin.initializeApp();
setGlobalOptions({ region: "us-central1", maxInstances: 5 });

const db = admin.firestore();
const alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
const codeLifetimeMs = 24 * 60 * 60 * 1000;

function requireUser(request) {
  if (!request.auth?.uid) {
    throw new HttpsError("unauthenticated", "Sign in before using pairing.");
  }
  return request.auth.uid;
}

function newPairingCode() {
  let suffix = "";
  for (let index = 0; index < 10; index += 1) {
    suffix += alphabet[crypto.randomInt(0, alphabet.length)];
  }
  return `HNY-${suffix}`;
}

function parseAnniversary(value) {
  if (value === undefined || value === null || value === "") return null;
  if (typeof value !== "string" || !/^\d{4}-\d{2}-\d{2}$/.test(value)) {
    throw new HttpsError("invalid-argument", "Anniversary must use YYYY-MM-DD.");
  }
  const date = new Date(`${value}T00:00:00.000Z`);
  if (Number.isNaN(date.getTime()) || date.toISOString().slice(0, 10) !== value) {
    throw new HttpsError("invalid-argument", "Anniversary is not a calendar date.");
  }
  const today = new Date().toISOString().slice(0, 10);
  if (value > today) {
    throw new HttpsError("invalid-argument", "Anniversary cannot be in the future.");
  }
  return value;
}

exports.createPairingCode = onCall(async (request) => {
  const uid = requireUser(request);
  const userRef = db.collection("users").doc(uid);

  for (let attempt = 0; attempt < 5; attempt += 1) {
    const code = newPairingCode();
    const codeRef = db.collection("pairingCodes").doc(code);
    const created = await db.runTransaction(async (transaction) => {
      const [userSnapshot, codeSnapshot] = await Promise.all([
        transaction.get(userRef),
        transaction.get(codeRef),
      ]);
      if (!userSnapshot.exists) {
        throw new HttpsError("failed-precondition", "Complete registration first.");
      }
      if (userSnapshot.get("coupleId")) {
        throw new HttpsError("failed-precondition", "This account is already linked.");
      }
      if (codeSnapshot.exists) return false;

      const previousCode = userSnapshot.get("pairingCode");
      if (previousCode) {
        transaction.delete(db.collection("pairingCodes").doc(previousCode));
      }
      transaction.set(codeRef, {
        ownerUid: uid,
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        expiresAt: admin.firestore.Timestamp.fromMillis(Date.now() + codeLifetimeMs),
      });
      transaction.update(userRef, { pairingCode: code });
      return true;
    });

    if (created) return { code };
  }

  throw new HttpsError("resource-exhausted", "Could not create a unique pairing code.");
});

exports.pairWithCode = onCall(async (request) => {
  const uid = requireUser(request);
  const code = typeof request.data?.code === "string" ? request.data.code.trim().toUpperCase() : "";
  if (!/^HNY-[A-HJ-NP-Z2-9]{10}$/.test(code)) {
    throw new HttpsError("invalid-argument", "Enter a valid Honey pairing code.");
  }
  const anniversaryDate = parseAnniversary(request.data?.anniversaryDate);

  const codeRef = db.collection("pairingCodes").doc(code);
  const requesterRef = db.collection("users").doc(uid);
  const coupleRef = db.collection("couples").doc();

  await db.runTransaction(async (transaction) => {
    const codeSnapshot = await transaction.get(codeRef);
    if (!codeSnapshot.exists) {
      throw new HttpsError("not-found", "Pairing code was not found or has expired.");
    }

    const codeData = codeSnapshot.data();
    if (!codeData?.expiresAt || codeData.expiresAt.toMillis() <= Date.now()) {
      throw new HttpsError("deadline-exceeded", "Pairing code has expired.");
    }
    const partnerUid = codeData.ownerUid;
    if (!partnerUid || partnerUid === uid) {
      throw new HttpsError("failed-precondition", "You cannot pair with your own code.");
    }

    const partnerRef = db.collection("users").doc(partnerUid);
    const [requesterSnapshot, partnerSnapshot] = await Promise.all([
      transaction.get(requesterRef),
      transaction.get(partnerRef),
    ]);
    if (!requesterSnapshot.exists || !partnerSnapshot.exists) {
      throw new HttpsError("not-found", "One of these accounts no longer exists.");
    }
    if (requesterSnapshot.get("coupleId") || partnerSnapshot.get("coupleId")) {
      throw new HttpsError("failed-precondition", "An account is already linked to a partner.");
    }

    const requesterCode = requesterSnapshot.get("pairingCode");
    transaction.set(coupleRef, {
      memberIds: [uid, partnerUid],
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      anniversaryDate,
    });
    transaction.update(requesterRef, {
      coupleId: coupleRef.id,
      partnerId: partnerUid,
      pairingCode: null,
      anniversaryDate,
    });
    transaction.update(partnerRef, {
      coupleId: coupleRef.id,
      partnerId: uid,
      pairingCode: null,
      anniversaryDate,
    });
    transaction.delete(codeRef);
    if (requesterCode) {
      transaction.delete(db.collection("pairingCodes").doc(requesterCode));
    }
  });

  return { paired: true };
});
