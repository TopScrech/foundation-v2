#!/usr/bin/env python3
"""Reproduce the vanilla metadata facts from the target sources and original Blocks class"""
import argparse
import re
import subprocess
from pathlib import Path
from zipfile import ZipFile


def closing_parenthesis(source, opening):
    depth = 1
    quote = None
    escaped = False
    index = opening + 1
    while index < len(source):
        char = source[index]
        if quote:
            if escaped:
                escaped = False
            elif char == '\\':
                escaped = True
            elif char == quote:
                quote = None
        elif source.startswith('//', index):
            index = source.index('\n', index)
            continue
        elif source.startswith('/*', index):
            index = source.index('*/', index) + 2
            continue
        elif char in ('"', "'"):
            quote = char
        elif char == '(':
            depth += 1
        elif char == ')':
            depth -= 1
            if depth == 0:
                return index
        index += 1
    raise ValueError('Unclosed native registration call')


def snapshots(forge_sources, legacy_forge):
    with ZipFile(forge_sources) as archive:
        source = archive.read('net/minecraft/util/datafix/fixes/BlockStateData.java').decode()
    identifiers = set()
    by_name = {}
    for match in re.finditer(r'\bregister\(\s*(\d+)\s*,', source):
        identifier = int(match[1])
        identifiers.add(identifier)
        opening = source.index('(', match.start())
        closing = closing_parenthesis(source, opening)
        names = re.findall(r'\bcreate\(\s*"([^"]+)"', source[opening:closing + 1])
        for name in names[1:]:
            existing = by_name.setdefault(name, identifier >> 4)
            if existing != identifier >> 4:
                raise ValueError(f'Ambiguous old block name: {name}')
    if not identifiers or 0 not in identifiers or max(identifiers) > 4095:
        raise ValueError('Unexpected vanilla state table')
    result = subprocess.run(
        ['javap', '-classpath', str(legacy_forge), '-c', 'net.minecraft.init.Blocks'],
        check=True, capture_output=True, text=True,
    )
    fields = {}
    name = None
    lookup = False
    for line in result.stdout.splitlines():
        match = re.search(r'// String ([a-z0-9_:]+)', line)
        if match:
            name = match[1]
        if 'Method getRegisteredBlock:' in line:
            lookup = True
        match = re.search(r'// Field ([A-Z][A-Z0-9_]+):', line)
        if match and lookup:
            fields[match[1]] = by_name['minecraft:' + name.removeprefix('minecraft:')]
            lookup = False
    if fields.get('STONE') != 1 or fields.get('STONE_SLAB') != 44 or fields.get('SNOW') != 80:
        raise ValueError('Unexpected original Blocks declarations')
    ids = sorted(identifiers)
    state_source = (
        'package foundation.block\n\n'
        '/** Canonical entries declared by the target Minecraft BlockStateData migration table */\n'
        'internal object LegacyVanillaStateIds {\n    val values = intArrayOf(\n'
        + '\n'.join('        ' + ', '.join(map(str, ids[i:i + 24])) + ',' for i in range(0, len(ids), 24))
        + '\n    )\n}\n'
    )
    field_source = (
        'package foundation.block\n\n'
        '/** Original vanilla field IDs, matched to the cached 1.12.2 Blocks declarations */\n'
        'object LegacyVanillaBlockIds {\n'
        + ''.join(f'    const val {key}: Int = {value}\n' for key, value in fields.items())
        + '}\n'
    )
    return {'LegacyVanillaStateIds.kt': state_source, 'LegacyVanillaBlockIds.kt': field_source}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--forge-sources', required=True, type=Path)
    parser.add_argument('--legacy-forge', required=True, type=Path)
    parser.add_argument('--check', action='store_true')
    args = parser.parse_args()
    destination = Path(__file__).resolve().parent.parent / 'src/main/kotlin/foundation/block'
    for name, source in snapshots(args.forge_sources, args.legacy_forge).items():
        path = destination / name
        if args.check:
            if not path.exists() or path.read_text() != source:
                raise ValueError(f'Metadata snapshot differs: {path}')
        else:
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(source)
    print('Passed native metadata snapshot check' if args.check else 'Updated native metadata snapshots')


if __name__ == '__main__':
    main()
