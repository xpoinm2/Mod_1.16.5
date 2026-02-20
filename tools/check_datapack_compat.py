#!/usr/bin/env python3
"""Validate data pack JSON files for common Forge 1.16.5 worldgen issues.

Focuses on errors that often lead to world-creation hangs, such as:
- malformed JSON;
- JSON values that are not objects where objects are expected;
- obvious 1.18+ worldgen structure inside 1.16.5 packs.
"""

from __future__ import annotations

import argparse
import json
from pathlib import Path
from typing import Iterable, List, Tuple

WORLDGEN_PATH_FRAGMENT = Path("data")


Issue = Tuple[Path, str]


def iter_json_files(root: Path) -> Iterable[Path]:
    for path in root.rglob("*.json"):
        if path.is_file():
            yield path


def is_probably_worldgen(path: Path) -> bool:
    parts = set(path.parts)
    return "worldgen" in parts or "dimension" in parts or "noise_settings" in parts


def check_json_file(path: Path) -> List[Issue]:
    issues: List[Issue] = []
    try:
        text = path.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        issues.append((path, "cannot read as UTF-8"))
        return issues

    try:
        data = json.loads(text)
    except json.JSONDecodeError as exc:
        issues.append((path, f"invalid JSON: {exc.msg} at line {exc.lineno}, col {exc.colno}"))
        return issues

    if is_probably_worldgen(path) and not isinstance(data, dict):
        issues.append((path, "worldgen JSON root is not an object"))

    # Heuristic for the exact symptom: a worldgen list containing bare strings.
    if isinstance(data, list):
        for idx, item in enumerate(data):
            if isinstance(item, str) and item.startswith("minecraft:"):
                issues.append(
                    (
                        path,
                        f"list entry #{idx} is a bare registry id '{item}' (1.16.5 usually expects object entries here)",
                    )
                )
                break

    # Heuristic for 1.18+ style files in old packs.
    if isinstance(data, dict):
        if "biomes" in data and "spawn_target" in data:
            issues.append((path, "looks like 1.18+ noise settings schema, likely incompatible with 1.16.5"))
        if "type" in data and data.get("type") == "minecraft:overworld" and "generator" in data:
            # 1.16 has dimension JSON too, but if large new fields appear, flag soft warning.
            if isinstance(data.get("generator"), dict) and "settings" in data["generator"] and isinstance(data["generator"]["settings"], str):
                issues.append((path, "dimension generator references settings by string id; verify pack targets exactly 1.16.5"))

    return issues


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Check data pack folder for JSON compatibility issues with Forge 1.16.5"
    )
    parser.add_argument(
        "path",
        nargs="?",
        default=".",
        help="Path to a data pack folder, world folder, or any directory to scan",
    )
    args = parser.parse_args()

    root = Path(args.path).resolve()
    if not root.exists():
        print(f"ERROR: path does not exist: {root}")
        return 2

    files = list(iter_json_files(root))
    if not files:
        print(f"No JSON files found under: {root}")
        return 0

    all_issues: List[Issue] = []
    for path in files:
        all_issues.extend(check_json_file(path))

    if not all_issues:
        print(f"OK: checked {len(files)} JSON files, no obvious 1.16.5 compatibility issues found.")
        return 0

    print(f"Found {len(all_issues)} potential issue(s) in {len(files)} JSON file(s):")
    for path, msg in all_issues:
        rel = path.relative_to(root) if path.is_relative_to(root) else path
        print(f"- {rel}: {msg}")

    print("\nTip: temporarily remove suspicious packs, then start the world with only this mod + Forge 1.16.5.")
    return 1


if __name__ == "__main__":
    raise SystemExit(main())
