#!/usr/bin/env python3
"""
Updates build.gradle.kts and README.md when a new Android Studio version is detected.

Usage:
    python3 scripts/update_as_version.py <platform_major> <platform_version> <codename>

Arguments:
    platform_major    First component of platformBuild  (e.g. 271 from 271.12345.678)
    platform_version  platformVersion from the XML feed (e.g. 2026.2.1)
    codename          Release codename                  (e.g. Roadrunner)

Examples:
    python3 scripts/update_as_version.py 271 2026.2.1 Roadrunner
    python3 scripts/update_as_version.py 999 2099.9.1 Test
"""

import re
import sys
from pathlib import Path

ROOT = Path(__file__).parent.parent


def _short_version(full: str) -> str:
    """Return the first two version components: '2026.1.1' → '2026.1'."""
    parts = full.split(".")
    return ".".join(parts[:2])


def update_gradle(platform_major: str, platform_version: str) -> None:
    path = ROOT / "build.gradle.kts"
    content = path.read_text()

    content = re.sub(
        r'untilBuild\.set\("[0-9]+\.\*"\)',
        f'untilBuild.set("{platform_major}.*")',
        content,
    )

    ic_version = f"IC-{_short_version(platform_version)}"
    if ic_version not in content:
        content = re.sub(
            r'(ideVersions\.set\(listOf\([^)]+)"(\))',
            rf'\1", "{ic_version}"\2',
            content,
        )

    path.write_text(content)
    print(f"[gradle] untilBuild → {platform_major}.* | added {ic_version}")


def update_readme(platform_version: str, codename: str) -> None:
    path = ROOT / "README.md"
    content = path.read_text()

    short_ver = _short_version(platform_version)

    # Badge URL (URL-encoded arrow: →%20)
    content = re.sub(
        r"(→%20)\w+%20\d{4}\.\d+(?:\.\d+)*",
        rf"\g<1>{codename}%20{short_ver}",
        content,
    )

    # Alt text (plain "to Codename YYYY.X")
    content = re.sub(
        r"(to )\w+ \d{4}\.\d+(?:\.\d+)*",
        rf"\g<1>{codename} {short_ver}",
        content,
    )

    path.write_text(content)
    print(f"[readme] badge updated → {codename} {short_ver}")


def main() -> None:
    if len(sys.argv) != 4:
        print(__doc__)
        sys.exit(1)

    platform_major, platform_version, codename = sys.argv[1], sys.argv[2], sys.argv[3]
    update_gradle(platform_major, platform_version)
    update_readme(platform_version, codename)
    print("Done.")


if __name__ == "__main__":
    main()
