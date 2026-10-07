const T={es:{touch:"Cariño",msgs:"Mensajes",loc:"Ubicación",mood:"Ánimo",set:"Ajustes",lang:"Idioma",mode:"Modo",theme:"Tema",dnd:"No molestar",vib:"Vibración",days:"días juntos",away:"de ti",noshare:"No comparte su ubicación",share:"Compartir mi ubicación",unshare:"Dejar de compartir",sent:"Enviado ✓",dndon:"🌙 Tiene No molestar, se entrega después",pend:"mensajes mientras dormía",mym:"Su ánimo",n0:"Toque",n1:"Abrazo",n2:"Beso",t0:"Te mandó un toque",t1:"Te mandó un abrazo",t2:"Te mandó un beso",mood2:"Cambió su ánimo",locn:"Ahora ves su ubicación",m0:"Te quiero",m1:"Voy en camino",m2:"Ya llegué",m3:"Pensando en ti",m4:"Llámame",m5:"Buenas noches 🌙"},
en:{touch:"Touch",msgs:"Messages",loc:"Location",mood:"Mood",set:"Settings",lang:"Language",mode:"Mode",theme:"Theme",dnd:"Do not disturb",vib:"Vibration",days:"days together",away:"away",noshare:"Not sharing location",share:"Share my location",unshare:"Stop sharing",sent:"Sent ✓",dndon:"🌙 Do not disturb on, delivered later",pend:"messages while asleep",mym:"Their mood",n0:"Tap",n1:"Hug",n2:"Kiss",t0:"Sent you a tap",t1:"Sent you a hug",t2:"Sent you a kiss",mood2:"Updated their mood",locn:"You can now see their location",m0:"Love you",m1:"On my way",m2:"I'm here",m3:"Thinking of you",m4:"Call me",m5:"Good night 🌙"}};
const TH=['#3d7bff','#ff5c9a','#8b6cff','#18c29c','#ff9a3d'],MO=['😊','😴','🥰','😔','😤','🍕'];
const ICO={t0:'💗',t1:'🤗',t2:'😘',locn:'📍'};for(let i=0;i<6;i++)ICO['m'+i]='💬';
const init=()=>({a:{lang:'es',dark:1,th:0,dnd:0,vib:1,share:0,mood:'😊',q:[],pos:[45,95]},b:{lang:'es',dark:0,th:1,dnd:0,vib:1,share:0,mood:'🥰',q:[],pos:[150,45]}});
const S=init(),O={a:'b',b:'a'},$=i=>document.getElementById(i),t=(i,k)=>T[S[i].lang][k];
function build(id,lab){
 const hd=k=>`<div class=hd><button class=bk data-go=home>‹</button><span data-i=${k}></span></div>`;
 const el=document.createElement('div');el.id='W'+id;
 el.innerHTML=`<div class=lab>${lab}</div><div class=case><div class="strap t"></div><div class="strap b"></div><div class=watch id=w${id}>
 <div class=dn id=dn${id}>🌙</div>
 <div class="v on" data-v=home><div class=top><b id=tm${id}></b><button class=gear data-go=set>⚙️</button></div><div class=sub id=sb${id}></div>
  <div class=grid>
  <button class=tile data-go=touch><i>💗</i><span data-i=touch></span></button>
  <button class=tile data-go=msgs><i>💬</i><span data-i=msgs></span></button>
  <button class=tile data-go=loc><i>📍</i><span data-i=loc></span></button>
  <button class=tile data-go=mood><i>🙂</i><span data-i=mood></span></button></div></div>
 <div class=v data-v=touch>${hd('touch')}<div class=three>${[0,1,2].map(n=>`<div><button class=circ data-t=${n}>${['💗','🤗','😘'][n]}</button><span data-i=n${n}></span></div>`).join('')}</div></div>
 <div class=v data-v=msgs>${hd('msgs')}<div class=list>${[0,1,2,3,4,5].map(n=>`<button class=pill data-msg=${n}></button>`).join('')}</div></div>
 <div class=v data-v=mood>${hd('mood')}<div class=moods>${MO.map(m=>`<button data-mood=${m}>${m}</button>`).join('')}</div></div>
 <div class=v data-v=loc>${hd('loc')}<svg viewBox="0 0 200 130"><path d="M0 35H200M0 92H200M60 0V130M140 0V130" stroke-width="7" fill="none"/><circle id=me${id} r=8 stroke="#fff" stroke-width=2 /><circle id=ot${id} r=8 stroke="#fff" stroke-width=2 /></svg>
  <div class=dist id=d${id}></div><button class=btn id=sh${id} data-act=share></button></div>
 <div class=v data-v=set>${hd('set')}
  <div class=r2><span data-i=lang></span><button id=lg${id} data-act=lang></button></div>
  <div class=r2><span data-i=mode></span><button id=md${id} data-act=mode></button></div>
  <div class=r2><span data-i=theme></span><div class=dots>${TH.map((c,i)=>`<button style="background:${c}" data-th=${i}></button>`).join('')}</div></div>
  <div class=r2><span data-i=dnd></span><button class=sw data-act=dnd data-sw=dnd></button></div>
  <div class=r2><span data-i=vib></span><button class=sw data-act=vib data-sw=vib></button></div></div>
 <div class=ok id=ok${id}></div>
 <div class=notif id=n${id}><div class=e></div><div class=m></div></div></div></div>`;
 $('row').appendChild(el);
 $('w'+id).addEventListener('click',e=>{const x=e.target.closest('button,.notif');if(!x)return;
  const d=x.dataset;
  if(x.classList.contains('notif'))x.classList.remove('on');
  else if(d.go)go(id,d.go);
  else if(d.t!==undefined)send(id,'t'+d.t);
  else if(d.msg!==undefined)send(id,'m'+d.msg);
  else if(d.mood){S[id].mood=d.mood;send(id,'mood2');all()}
  else if(d.th!==undefined){S[id].th=+d.th;all()}
  else if(d.act)act(id,d.act)});
}
const go=(i,v)=>$('w'+i).querySelectorAll('.v').forEach(e=>e.classList.toggle('on',e.dataset.v==v));
function toast(i,k){const e=$('ok'+i);e.textContent=t(i,k);clearTimeout(e._t);e._t=setTimeout(()=>e.textContent='',2000)}
function notify(to,ico,txt){const n=$('n'+to),w=$('w'+to);n.querySelector('.e').textContent=ico;n.querySelector('.m').textContent=txt;n.classList.add('on');
 if(S[to].vib){w.classList.remove('buzz');void w.offsetWidth;w.classList.add('buzz');navigator.vibrate&&navigator.vibrate([80,40,80])}
 clearTimeout(n._t);n._t=setTimeout(()=>n.classList.remove('on'),3500)}
function send(from,k){const to=O[from];
 if(S[to].dnd){S[to].q.push(k);toast(from,'dndon');return}
 notify(to,k=='mood2'?S[from].mood:ICO[k],t(to,k));toast(from,'sent')}
function act(i,a){const s=S[i];
 if(a=='lang')s.lang=s.lang=='es'?'en':'es';
 else if(a=='mode')s.dark=!s.dark;
 else if(a=='vib')s.vib=!s.vib;
 else if(a=='dnd'){s.dnd=!s.dnd;if(!s.dnd&&s.q.length){const n=s.q.length;s.q=[];all();notify(i,'🌙',n+' '+t(i,'pend'));return}}
 else if(a=='share'){s.share=!s.share;if(s.share)send(i,'locn')}
 all()}
function sty(i){const s=S[i];$('W'+i).style.setProperty('--ac',TH[s.th]);const w=$('w'+i);w.classList.toggle('d',!!s.dark);w.classList.toggle('l',!s.dark);
 w.querySelectorAll('[data-i]').forEach(e=>e.textContent=t(i,e.dataset.i));
 w.querySelectorAll('[data-msg]').forEach(e=>e.textContent=t(i,'m'+e.dataset.msg));
 w.querySelectorAll('[data-sw]').forEach(e=>e.classList.toggle('on',!!s[e.dataset.sw]));
 w.querySelectorAll('[data-th]').forEach(e=>e.classList.toggle('sel',+e.dataset.th==s.th));
 $('lg'+i).textContent=s.lang.toUpperCase();$('md'+i).textContent=s.dark?'🌙':'☀️';
 $('sh'+i).textContent=t(i,s.share?'unshare':'share');$('dn'+i).style.display=s.dnd?'':'none';
 const days=Math.floor((Date.now()-new Date('2024-02-14'))/864e5);
 $('sb'+i).textContent=`💞 ${days} ${t(i,'days')} · ${t(i,'mym')} ${S[O[i]].mood}`;
 const o=O[i],m=$('me'+i),p=$('ot'+i);
 m.setAttribute('cx',s.pos[0]);m.setAttribute('cy',s.pos[1]);m.setAttribute('fill',TH[s.th]);
 p.style.display=S[o].share?'':'none';p.setAttribute('cx',S[o].pos[0]);p.setAttribute('cy',S[o].pos[1]);p.setAttribute('fill',TH[S[o].th]);
 const d=Math.round(Math.hypot(s.pos[0]-S[o].pos[0],s.pos[1]-S[o].pos[1])*6);
 $('d'+i).textContent=S[o].share?`${d} m ${t(i,'away')}`:t(i,'noshare')}
const all=()=>{sty('a');sty('b')};
build('a','Tu reloj');build('b','Reloj de tu pareja');all();
const cl=(v,m)=>Math.max(12,Math.min(v,m));
setInterval(()=>{['a','b'].forEach(k=>S[k].pos=[cl(S[k].pos[0]+Math.random()*8-4,188),cl(S[k].pos[1]+Math.random()*6-3,118)]);all()},2000);
const clk=()=>{const d=new Date(),x=d.getHours()+':'+String(d.getMinutes()).padStart(2,'0');$('tma').textContent=$('tmb').textContent=x};clk();setInterval(clk,10000);
let tm=[];
const STEPS=[
[0,()=>{go('a','touch')},'1. Envía cariño con un toque'],
[1500,()=>send('a','t1')],
[3200,()=>go('a','msgs'),'2. Mensajes rápidos, sin chat'],
[4200,()=>send('a','m1')],
[6500,()=>{go('b','set')},'3. Ella activa No molestar'],
[7500,()=>act('b','dnd')],
[8800,()=>send('a','m5'),'Tú escribes y se guarda en silencio'],
[10800,()=>{act('b','dnd');go('b','home')},'Al volver, recibe un resumen'],
[13500,()=>{go('b','loc');act('b','share');go('a','loc')},'4. Ubicación compartida'],
[16500,()=>{go('a','set');act('a','lang');act('a','mode')},'5. Idioma, modo claro/oscuro y temas'],
[18500,()=>{S.a.th=2;all()}],
[20500,()=>reset(),'Eso es Pulso 💗']];
function reset(){const n=init();Object.assign(S.a,n.a);Object.assign(S.b,n.b);go('a','home');go('b','home');all()}
$('go').onclick=()=>{tm.forEach(clearTimeout);tm=[];reset();$('cap').textContent='';
 STEPS.forEach(([d,f,c])=>tm.push(setTimeout(()=>{f();if(c)$('cap').textContent=c},d)))};
