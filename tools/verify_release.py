#!/usr/bin/env python3
"""Verify the unified Foundation distribution before copying it to Downloads"""
import argparse
import json
from zipfile import ZipFile
from verify_embedded_core import verify


def verify_release(path):
    verify(path)
    with ZipFile(path) as archive:
        names = set(archive.namelist())
        required = {
            'micdoodle8/mods/galacticraft/core/GalacticraftCore.class',
            'micdoodle8/mods/galacticraft/planets/GalacticraftPlanets.class',
            'micdoodle8/mods/miccore/MicdoodlePlugin.class',
            'foundation/FoundationLoadingPlugin.class',
            'galaxyspace/GalaxySpace.class',
            'asmodeuscore/AsmodeusCore.class',
            'kotlin/jvm/internal/Intrinsics.class',
            'org/spongepowered/asm/launch/MixinTweaker.class',
            'META-INF/foundation_at.cfg',
        }
        assert required <= names, f'Missing release components: {required - names}'
        mod_entries = json.loads(archive.read('mcmod.info'))
        primary = mod_entries[0]
        assert primary['modid'] == 'galaxyspace', 'The existing primary registry ID must remain compatible'
        assert primary['name'] == 'Foundation', 'The primary jar display name must be Foundation'
        assert primary['version'] == '2.0.0', 'The primary metadata must use the Foundation release version'
        metadata = {entry['modid'] for entry in mod_entries}
        assert {'galacticraftcore', 'galacticraftplanets', 'galaxyspace', 'asmodeuscore'} <= metadata
        manifest = archive.read('META-INF/MANIFEST.MF').decode()
        assert 'FMLCorePlugin: foundation.FoundationLoadingPlugin' in manifest
        assert 'TweakClass:' not in manifest, 'A TweakClass would prevent Forge from loading the core plugin'
        assert 'FMLAT: foundation_at.cfg' in manifest
        for config_name in ['mixins.asmodeuscore.json', 'mixins.galaxyspace.json']:
            config = json.loads(archive.read(config_name))
            for group in ['mixins', 'client', 'server']:
                for mixin in config.get(group, []):
                    internal_name = (config['package'] + '.' + mixin).replace('.', '/')
                    assert (internal_name + '$Companion').encode() not in archive.read(internal_name + '.class'), f'Mixin owns an unsupported Kotlin companion: {mixin}'
        for name in names:
            if name.endswith('.class'):
                assert int.from_bytes(archive.read(name)[6:8], 'big') <= 52, f'Not Java 8 bytecode: {name}'
    print('Passed: one Foundation jar with all components, embedded runtimes, and Java 8 bytecode')


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('jar')
    verify_release(parser.parse_args().jar)
