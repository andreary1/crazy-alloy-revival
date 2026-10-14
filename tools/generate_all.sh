#!/bin/sh
# Regenerates all generated resources. Requires Python 3 with Pillow and nbtlib.
set -e
cd "$(dirname "$0")/.."
python3 tools/gen_resources.py
python3 tools/gen_textures.py
python3 tools/gen_structure.py
python3 tools/gen_tower.py
python3 tools/gen_stage4_structures.py
python3 tools/gen_stage6_structures.py
python3 tools/gen_stage7_structures.py
