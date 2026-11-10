#!/usr/bin/env python3
"""Verify that a distribution contains the complete selected Core runtime"""
import argparse
import json
from pathlib import Path
from zipfile import ZipFile


def verify(jar):
    root = Path(__file__).resolve().parent.parent
    inventory = json.loads((root / 'docs/embedded-core-inventory.json').read_text())
    assert not list((root / 'libs').glob('AsmodeusCore*.jar')), 'Leftover Core dependency jar'
    assert 'implementation files("libs/AsmodeusCore' not in (root / 'build.gradle').read_text()
    with ZipFile(jar) as archive:
        entries = archive.namelist()
        names = set(entries)
        assert len(entries) == len(names), 'Duplicate entries in distribution'
        required = {name + '.class' for name in inventory['classes']}
        assert required <= names, f'Missing Core classes: {required - names}'
        excluded = {name + '.class' for name in inventory['excludedClasses']}
        assert not excluded & names, f'Unused Core classes packaged: {excluded & names}'
        resources = set(inventory['resources'])
        assert resources <= names, f'Missing resources: {resources - names}'
        assert not any(name.endswith('.jar') for name in names), 'Nested dependency jar'
        for path in ['mixins.asmodeuscore.json', 'mixins.galaxyspace.json']:
            config = json.loads(archive.read(path))
            assert config['refmap'] in names, f'Missing refmap for {path}'
            for group in ['mixins', 'client', 'server']:
                for mixin in config[group]:
                    name = (config['package'] + '.' + mixin).replace('.', '/') + '.class'
                    assert name in names, f'Missing mixin class: {name}'
        metadata = {item['modid']: item for item in json.loads(archive.read('mcmod.info'))}
        assert {'galaxyspace', 'asmodeuscore'} <= metadata.keys()
        assert metadata['asmodeuscore']['version'] == '1.0.4'
        manifest = archive.read('META-INF/MANIFEST.MF').decode()
        assert 'mixins.asmodeuscore.json,mixins.galaxyspace.json' in manifest
        assert 'META-INF/gcgsac_at.cfg' in names
    print(f'Passed: {len(required)} Core classes, {len(resources)} resources, both mixin configs, metadata, and no external Core jar')


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('jar', type=Path)
    verify(parser.parse_args().jar)
