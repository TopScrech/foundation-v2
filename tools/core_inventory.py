#!/usr/bin/env python3
"""Trace the conservative Asmodeus Core closure, including reflective entry points"""
import argparse
import hashlib
import json
import re
import struct
from pathlib import Path
from zipfile import ZipFile


def utf8_constants(data):
    count = struct.unpack_from('>H', data, 8)[0]
    offset, index, result = 10, 1, []
    sizes = {3: 4, 4: 4, 5: 8, 6: 8, 7: 2, 8: 2, 9: 4, 10: 4,
             11: 4, 12: 4, 15: 3, 16: 2, 17: 4, 18: 4, 19: 2, 20: 2}
    while index < count:
        tag = data[offset]
        offset += 1
        if tag == 1:
            size = struct.unpack_from('>H', data, offset)[0]
            offset += 2
            result.append(data[offset:offset + size].decode('utf-8', errors='replace'))
            offset += size
        else:
            offset += sizes[tag]
            if tag in (5, 6):
                index += 1
        index += 1
    return result


def inventory(jar, project):
    with ZipFile(jar) as archive:
        classes = {name[:-6]: archive.read(name) for name in archive.namelist()
                   if name.startswith('asmodeuscore/') and name.endswith('.class')}
        names = sorted(classes, key=len, reverse=True)
        pattern = re.compile(r'(' + '|'.join(map(re.escape, names)) + r')(?![\w$])')

        def references(text):
            return set(pattern.findall(text.replace('.', '/')))

        roots = {'asmodeuscore/AsmodeusCore', 'asmodeuscore/core/proxy/ClientProxy',
                 'asmodeuscore/AsmodeusCore$RegistrationHandler'}
        for source in (project / 'src/main').rglob('*'):
            if source.is_file() and source.suffix in ('.java', '.kt', '.json', '.info'):
                roots.update(references(source.read_text(errors='replace')))
        mixins = json.loads(archive.read('mixins.asmodeuscore.json'))
        for key in ('mixins', 'client', 'server'):
            roots.update((mixins['package'] + '.' + name).replace('.', '/') for name in mixins[key])
        edges = {name: references('\n'.join(utf8_constants(data))) - {name}
                 for name, data in classes.items()}
        selected, pending = set(), list(roots)
        while pending:
            name = pending.pop()
            if name not in selected:
                selected.add(name)
                pending.extend(edges[name] - selected)
                pending.extend(other for other in classes
                               if other.split('$')[0] == name.split('$')[0] and other not in selected)
        sources = sorted({name.split('$')[0] + '.java' for name in selected})
        return {
            'archive': Path(jar).name,
            'sha256': hashlib.sha256(Path(jar).read_bytes()).hexdigest(),
            'roots': sorted(roots),
            'classes': sorted(selected),
            'sources': sources,
            'excludedClasses': sorted(set(classes) - selected),
            'recoveredSources': [name for name in sources if name not in archive.namelist()],
            'note': 'Conservative class closure, including lifecycle, client proxy, nested types, and mixin entry points; resources require separate dynamic-path review',
        }


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('jar', type=Path)
    parser.add_argument('--project', type=Path, default=Path(__file__).resolve().parent.parent)
    args = parser.parse_args()
    print(json.dumps(inventory(args.jar, args.project), indent=2))
