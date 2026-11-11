#!/usr/bin/env python3
"""Verify a Foundation Forge 26.3 distribution"""
import argparse
import json
import re
import tomllib
from pathlib import Path
from zipfile import ZipFile


MOD_CLASSES = {
    'galacticraftcore': 'micdoodle8/mods/galacticraft/core/GalacticraftCore.class',
    'galacticraftplanets': 'micdoodle8/mods/galacticraft/planets/GalacticraftPlanets.class',
    'galaxyspace': 'galaxyspace/GalaxySpace.class',
    'asmodeuscore': 'asmodeuscore/AsmodeusCore.class',
}
# Legacy registry callback was replaced by data-driven biomes and an optional pack
REPLACED_CORE_CLASSES = {
    'asmodeuscore/AsmodeusCore$RegistrationHandler': {
        'foundation/biome/FoundationBiomeKeys.class',
        'foundation/biome/FoundationBiomeTags.class',
        'data/asmodeuscore/worldgen/biome/world_engine.json',
        'data/asmodeuscore/worldgen/biome/world_engine_2.json',
        'biome-types/enabled/pack.mcmeta',
        'biome-types/disabled/pack.mcmeta',
        *{f'biome-types/enabled/data/c/tags/worldgen/biome/{tag}.json'
          for tag in ('is_cold', 'is_dry', 'is_dead')},
    },
}
LEGACY_REFERENCES = (
    b'net/minecraft/launchwrapper/',
    b'net/minecraftforge/fml/relauncher/',
    b'net/minecraftforge/fml/common/event/FMLPreInitializationEvent',
    b'net/minecraftforge/fml/common/event/FMLInitializationEvent',
    b'net/minecraftforge/common/DimensionManager',
)


def require(condition, message):
    if not condition:
        raise ValueError(message)


def verify_release(path):
    root = Path(__file__).resolve().parent.parent
    with ZipFile(path) as archive:
        entries = archive.namelist()
        names = set(entries)
        require(len(entries) == len(names), 'Duplicate archive entries')
        require(not any(name.startswith('foundation/verification/') for name in names),
                'Packaged isolated SDK verification helpers')
        required = set(MOD_CLASSES.values()) | {
            'META-INF/mods.toml', 'pack.mcmeta', 'LICENSE.txt',
            'kotlin/jvm/internal/Intrinsics.class',
            'foundation/item/FoundationItemComponents.class',
            'foundation/item/FoundationItemVariants.class',
            'assets/galacticraftcore/lang/en_us.json',
            'assets/galacticraftcore/items/item_basic_moon.json',
            'foundation/effect/FoundationEffectCures.class',
            'foundation/mixin/MilkEffectCuresMixin.class',
            'foundation/mixin/PlanetClockLayersMixin.class',
            'foundation/level/FoundationPlanetClocks.class',
            'micdoodle8/mods/galacticraft/core/energy/item/ItemElectricBase.class',
            'mixins.foundation.json',
        }
        for namespace, body in [('galacticraftcore', 'moon'), ('galacticraftplanets', 'mars'),
                                ('galacticraftplanets', 'venus'), ('galacticraftplanets', 'asteroids')]:
            required.add(f'data/{namespace}/world_clock/{body}.json')
            required.add(f'data/{namespace}/dimension_type/{body}.json')
        for namespace, item in [('galacticraftcore', 'battery'), ('galacticraftcore', 'infinite_battery'),
                                ('galacticraftplanets', 'atomic_battery'),
                                *[('galaxyspace', name) for name in ('advanced_battery', 'modern_battery', 'extra_battery', 'ultimate_battery')]]:
            required.add(f'assets/{namespace}/items/{item}.json')
        require(required <= names, f'Missing release components: {required - names}')
        metadata = tomllib.loads(archive.read('META-INF/mods.toml').decode())
        require(metadata['modLoader'] == 'javafml', 'Expected modern Forge loader')
        require(metadata['loaderVersion'] == '[66,)', 'Expected Forge 66 loader')
        mods = {mod['modId']: mod for mod in metadata['mods']}
        require(set(mods) == set(MOD_CLASSES), 'Expected all four integrated mod containers')
        require(mods['galaxyspace']['version'] == '2.0.0', 'Incorrect Foundation version')
        for mod_id in MOD_CLASSES:
            deps = {dep['modId']: dep for dep in metadata['dependencies'][mod_id]}
            for dep_id, version_range in [('forge', '[66.1.5,67)'), ('minecraft', '[26.3,26.4)')]:
                require(deps[dep_id]['versionRange'] == version_range, f'Incorrect {mod_id} dependency: {dep_id}')
                require(deps[dep_id]['mandatory'], f'Optional platform dependency: {mod_id}')
            require(b'Lnet/minecraftforge/fml/common/Mod;' in archive.read(MOD_CLASSES[mod_id]),
                    f'Missing mod annotation: {mod_id}')
        inventory = json.loads((root / 'docs/galaxyspace/embedded-core-inventory.json').read_text())
        core_classes = {name + '.class' for name in inventory['classes']
                        if not re.search(r'\$\d+(?:\$|$)', name) and name not in REPLACED_CORE_CLASSES}
        for replacements in REPLACED_CORE_CLASSES.values():
            core_classes.update(replacements)
        require(core_classes <= names, f'Missing embedded Core classes: {core_classes - names}')
        pack = json.loads(archive.read('pack.mcmeta'))['pack']
        require(tuple(pack['min_format']) <= (97, 1) <= tuple(pack['max_format']),
                'Unsupported resource format 97.1')
        require(tuple(pack['min_format']) <= (121, 0) <= tuple(pack['max_format']),
                'Unsupported data format 121.0')
        for tag in ('is_cold', 'is_dry', 'is_dead'):
            name = f'biome-types/enabled/data/c/tags/worldgen/biome/{tag}.json'
            require(json.loads(archive.read(name))['values'] == ['asmodeuscore:world_engine'],
                    f'Incorrect technical biome classification: {tag}')
        require(not any(name.startswith('biome-types/disabled/data/') for name in names),
                'Disabled biome classification pack contains data')
        require('mcmod.info' not in names, 'Packaged legacy metadata')
        require('foundation/FoundationLoadingPlugin.class' not in names, 'Packaged LaunchWrapper coremod')
        require(not any(name.startswith('micdoodle8/mods/miccore/') for name in names), 'Packaged old MicdoodleCore')
        manifest = archive.read('META-INF/MANIFEST.MF').decode()
        require('MixinConfigs: mixins.foundation.json' in manifest,
                'Missing native Foundation mixin registration')
        mixins = json.loads(archive.read('mixins.foundation.json'))
        require(mixins['required'] and mixins['mixins'] == ['MilkEffectCuresMixin', 'PlanetClockLayersMixin']
                and mixins['package'] == 'foundation.mixin'
                and mixins['injectors']['defaultRequire'] == 1,
                'Missing required native Foundation hooks')
        for attribute in ['FMLCorePlugin:', 'TweakClass:', 'FMLAT:']:
            require(attribute not in manifest, f'Legacy manifest attribute: {attribute}')
        for name in sorted(names):
            if name.endswith('.class'):
                bytecode = archive.read(name)
                require(int.from_bytes(bytecode[6:8], 'big') <= 69, f'Bytecode exceeds Java 25: {name}')
                if name.startswith(('foundation/', 'micdoodle8/', 'galaxyspace/', 'asmodeuscore/')):
                    for legacy in LEGACY_REFERENCES:
                        require(legacy not in bytecode, f'Legacy API {legacy.decode()} in {name}')
    print('Passed: unified Foundation Forge 26.3 archive and static compatibility checks')


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('jar', type=Path)
    verify_release(parser.parse_args().jar)
