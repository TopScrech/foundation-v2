#!/usr/bin/env bash
set -euo pipefail
project_root="$(cd "$(dirname "$0")/.." && pwd)"
cache_root="${FOUNDATION_CACHE_ROOT:-${XDG_CACHE_HOME:-$HOME/.cache}/foundation-v2/galaxyspace}"
mkdir -p "$cache_root"
cache_root="$(cd "$cache_root" && pwd)"
case "$cache_root/" in
    "$project_root/"*) echo 'Build cache must be outside the project folder' >&2; exit 1 ;;
esac
workspace="$cache_root/workspace"
mkdir -p "$workspace"
rsync -a --delete --exclude '.git/' --exclude '.gradle/' --exclude 'build/' "$project_root/" "$workspace/"
if [[ -z "${JAVA_HOME:-}" && -x /usr/libexec/java_home ]]; then
    export JAVA_HOME="$(/usr/libexec/java_home -v 1.8)"
fi
if [[ $# -eq 0 ]]; then
    set -- clean build gsJar deobfGSJar
fi
cd "$workspace"
bash gradlew --project-cache-dir "$cache_root/project-cache" --console=plain "$@"
