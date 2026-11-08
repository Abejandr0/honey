#!/usr/bin/env python3
"""
Validates Android resource XML string files for AAPT2 compatibility:
- Unescaped apostrophes (') must be escaped with (\')
- Valid XML parsing
"""
import glob
import re
import sys
import xml.etree.ElementTree as ET

def validate_string_files():
    has_errors = False
    files = glob.glob("app/src/main/res/values*/strings.xml")
    if not files:
        print("Warning: No strings.xml files found")
        return 0

    for filepath in files:
        print(f"Checking {filepath}...")
        try:
            tree = ET.parse(filepath)
            root = tree.getroot()
        except Exception as e:
            print(f"Error parsing XML in {filepath}: {e}")
            has_errors = True
            continue

        with open(filepath, "r", encoding="utf-8") as f:
            lines = f.readlines()

        for idx, line in enumerate(lines, start=1):
            # Check if line defines a string element
            if "<string " in line and "</string>" in line:
                # Extract text between tags
                match = re.search(r'>([^<]*)<', line)
                if match:
                    content = match.group(1)
                    # Look for unescaped apostrophes
                    for i, char in enumerate(content):
                        if char == "'":
                            if i == 0 or content[i-1] != "\\":
                                print(f"Error at {filepath}:{idx} - Unescaped apostrophe in: {line.strip()}")
                                has_errors = True

    if has_errors:
        print("\n❌ String validation failed. Please escape apostrophes with \\'.")
        return 1
    else:
        print("\n✅ All Android string resources are valid and AAPT2 compliant.")
        return 0

if __name__ == "__main__":
    sys.exit(validate_string_files())
