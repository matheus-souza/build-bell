#!/usr/bin/env python3
"""
Updates build.gradle.kts and README.md when a new Android Studio version is detected.

Usage:
    python3 scripts/update_as_version.py <platform_major> <platform_version> <codename> [canary_major]

Arguments:
    platform_major    First component of platformBuild for the latest Release
                      (e.g. 271 from 271.12345.678)
    platform_version  platformVersion from the XML feed (e.g. 2026.2.1)
    codename          Release codename                  (e.g. Roadrunner)
    canary_major      (optional) platformBuild major of the latest Canary build.
                      When provided and higher than platform_major, used for untilBuild
                      so the plugin stays compatible with Canary without running the
                      plugin verifier against it.

Examples:
    python3 scripts/update_as_version.py 271 2026.2.1 Roadrunner
    python3 scripts/update_as_version.py 271 2026.2.1 Roadrunner 273
"""

import re
import sys
import urllib.error
import urllib.request
from pathlib import Path

ROOT = Path(__file__).parent.parent


def _short_version(full: str) -> str:
    """Return the first two version components: '2026.1.1' → '2026.1'."""
    parts = full.split(".")
    return ".".join(parts[:2])


def update_gradle(platform_major: str, platform_version: str, canary_major: str = "") -> None:
    path = ROOT / "build.gradle.kts"
    content = path.read_text()

    # Use canary_major for untilBuild if it extends the ceiling beyond the stable release
    until_major = platform_major
    if canary_major and int(canary_major) > int(platform_major):
        until_major = canary_major

    current_match = re.search(r'untilBuild\.set\("([0-9]+)\.\*"\)', content)
    if current_match and int(until_major) > int(current_match.group(1)):
        content = re.sub(
            r'untilBuild\.set\("[0-9]+\.\*"\)',
            f'untilBuild.set("{until_major}.*")',
            content,
        )
        source = f"canary {canary_major}" if until_major == canary_major else f"release {platform_major}"
        print(f"[gradle] untilBuild → {until_major}.* (from {source})")
    else:
        current = current_match.group(1) if current_match else "?"
        print(f"[gradle] untilBuild kept at {current}.* (until_major={until_major} is not higher)")

    # ideVersions tracks only stable Release versions — no Canary.
    # Android Studio versions (e.g. 2026.1.x) do not always have a matching IntelliJ
    # Community tarball; skip silently when the CDN returns 404.
    ic_version = f"IC-{platform_version}"
    if f'"{ic_version}"' not in content:
        cdn_url = f"https://download-cdn.jetbrains.com/idea/ideaIC-{platform_version}.tar.gz"
        try:
            req = urllib.request.Request(cdn_url, method="HEAD")
            urllib.request.urlopen(req, timeout=15)
            tarball_exists = True
        except (urllib.error.HTTPError, urllib.error.URLError, OSError):
            tarball_exists = False

        if tarball_exists:
            content = re.sub(
                r'(ideVersions\.set\(listOf\([^)]+)"(\))',
                rf'\1", "{ic_version}"\2',
                content,
            )
            print(f"[gradle] added {ic_version} to ideVersions")
        else:
            print(f"[gradle] skipped {ic_version}: ideaIC-{platform_version}.tar.gz not on CDN")

    path.write_text(content)


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
    if len(sys.argv) not in (4, 5):
        print(__doc__)
        sys.exit(1)

    platform_major = sys.argv[1]
    platform_version = sys.argv[2]
    codename = sys.argv[3]
    canary_major = sys.argv[4] if len(sys.argv) == 5 else ""

    update_gradle(platform_major, platform_version, canary_major)
    update_readme(platform_version, codename)
    print("Done.")


if __name__ == "__main__":
    main()
