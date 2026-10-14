#!/usr/bin/env python3
"""Static checks that do not need Minecraft: every registered block/item has its assets,
every texture/model reference resolves, every crafting reference names something that exists,
and both languages cover the same keys.
Registered ids are read straight from the Java registry classes."""
import json, os, re, sys, glob

NS = "crazyalloy_revival"
ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..")
RES = os.path.join(ROOT, "src", "main", "resources")
A = os.path.join(RES, "assets", NS)
D = os.path.join(RES, "data", NS)
JAVA = os.path.join(ROOT, "src", "main", "java", "com", "crazyalloy", "revival", "registry")
errors = []

def ids(file, pattern):
    return set(re.findall(pattern, open(os.path.join(JAVA, file), encoding="utf-8").read()))

blocks = ids("ModBlocks.java", r'register(?:Simple)?Block\("([a-z_]+)"')
items_simple = ids("ModItems.java", r'(?:simple|food|armor|registerItem|registerSimpleItem)\("([a-z_]+)"')
items_simple |= {f"{e}_spawn_egg" for e in ids("ModItems.java", r'egg\("([a-z_]+)"')}
# Blocks without an item form (liquids are placed with buckets).
NO_ITEM = {"melted_chocolate", "bubbaloo", "ice_cream_portal"}
# Blocks that drop nothing and have no loot table (noLootTable in ModBlocks).
NO_LOOT = {"ice_cream_portal"}
# Items whose name comes from the block key (DoubleHighBlockItem with useBlockDescriptionPrefix).
BLOCK_NAMED = {"sweetwood_door"}
items = (blocks - NO_ITEM) | items_simple
entities = ids("ModEntities.java", r'registerEntityType\(\s*"([a-z_]+)"')
# Thrown items: drawn by ThrownItemRenderer from their item, and they drop nothing.
PROJECTILES = {"brown_sugar_brick", "gumdrop_shot", "jelly_snake_shot", "bullet"}
print(f"{len(blocks)} blocks, {len(items)} items, {len(entities)} entities")

def exists(rel):
    return os.path.isfile(os.path.join(RES, rel))

def tex_path(ref):
    ns, path = ref.split(":") if ":" in ref else ("minecraft", ref)
    return f"assets/{ns}/textures/{path}.png"

for b in blocks:
    if not exists(f"assets/{NS}/blockstates/{b}.json"):
        errors.append(f"missing blockstate {b}")
    if b not in NO_LOOT and not exists(f"data/{NS}/loot_table/blocks/{b}.json"):
        errors.append(f"missing block loot table {b}")
for i in items:
    if not exists(f"assets/{NS}/items/{i}.json"):
        errors.append(f"missing item definition {i}")
for e in entities - PROJECTILES:
    # Variant creatures (Ice Cream Zombie, Living Ice Cream) have one texture per flavour: <id>_<flavour>.png.
    if not exists(f"assets/{NS}/textures/entity/{e}.png") and not glob.glob(os.path.join(RES, f"assets/{NS}/textures/entity/{e}_*.png")):
        errors.append(f"missing entity texture {e}")
    if not exists(f"data/{NS}/loot_table/entities/{e}.json"):
        errors.append(f"missing entity loot table {e}")

models = {os.path.relpath(p, os.path.join(A, "models")).replace(os.sep, "/")[:-5] for p in glob.glob(f"{A}/models/**/*.json", recursive=True)}
def model_ok(ref):
    ns, path = ref.split(":")
    return ns == "minecraft" or path in models
for p in glob.glob(f"{A}/models/**/*.json", recursive=True):
    m = json.load(open(p))
    for t in m.get("textures", {}).values():
        if t.startswith("#"):
            continue
        if t.startswith(NS) and not exists(tex_path(t)):
            errors.append(f"{os.path.basename(p)}: missing texture {t}")
    parent = m.get("parent")
    if parent and parent.startswith(NS) and not model_ok(parent):
        errors.append(f"{os.path.basename(p)}: missing parent {parent}")
for p in glob.glob(f"{A}/blockstates/*.json") + glob.glob(f"{A}/items/*.json"):
    for ref in re.findall(r'"model": "([^"]+)"', open(p).read()):
        if not model_ok(ref):
            errors.append(f"{os.path.basename(p)}: missing model {ref}")

def check_ref(where, ref):
    if ref.startswith("#"):
        return
    ns, path = ref.split(":") if ":" in ref else ("minecraft", ref)
    if ns == NS and path not in items:
        errors.append(f"{where}: unknown item {ref}")
for p in glob.glob(f"{D}/recipe/**/*.json", recursive=True) + glob.glob(f"{D}/loot_table/**/*.json", recursive=True) + glob.glob(f"{D}/advancement/*.json"):
    text = re.sub(r'"parent": "[^"]+"', "", open(p).read())
    for ref in re.findall(rf'"({NS}:[a-z_/]+)"', text):
        path = ref.split(":")[1]
        if "/" in path or path in entities or path in ("sweet_forest", "jelly_bean_fields", "candy_cave", "cookie_hut", "gingerbread_tower", "gingerbread_fortress", "ice_cream_truck", "chocolate_factory", "ice_cream_machine", "ice_cream", "ice_cream_nest", "ice_cream_plains", "bank"):
            continue
        check_ref(os.path.relpath(p, D), ref)

for p in glob.glob(f"{A}/equipment/*.json"):
    for layer, entries in json.load(open(p))["layers"].items():
        for e in entries:
            ns, path = e["texture"].split(":")
            if not exists(f"assets/{ns}/textures/entity/equipment/{layer}/{path}.png"):
                errors.append(f"equipment {p}: missing {layer}/{path}")

en = json.load(open(f"{A}/lang/en_us.json")); pt = json.load(open(f"{A}/lang/pt_br.json"))
if set(en) != set(pt):
    errors.append(f"lang key mismatch: {sorted(set(en) ^ set(pt))}")
for b in blocks:
    if f"block.{NS}.{b}" not in en:
        errors.append(f"missing name for block {b}")
for i in items_simple - BLOCK_NAMED:
    if f"item.{NS}.{i}" not in en:
        errors.append(f"missing name for item {i}")
for e in entities:
    if f"entity.{NS}.{e}" not in en:
        errors.append(f"missing name for entity {e}")
java_keys = set()
for p in glob.glob(os.path.join(ROOT, "src/main/java/**/*.java"), recursive=True):
    java_keys |= set(re.findall(r'"((?:gui|container|itemGroup|crazyalloy_revival\.configuration)\.[A-Za-z0-9_.]+)"', open(p).read()))
for k in java_keys:
    if k.endswith("."):
        continue  # a prefix the code completes with a number (help.1, help.2, ...)
    if k not in en:
        errors.append(f"translation used in Java but missing: {k}")
sounds = json.load(open(f"{A}/sounds.json"))
sound_src = open(os.path.join(JAVA, "ModSounds.java")).read()
sound_ids = re.findall(r'register\("([a-z_.]+)"\)', sound_src)
sound_ids += [f"entity.{m}.{k}" for m in re.findall(r'mob\("([a-z_]+)"\)', sound_src) for k in ("ambient", "hurt", "death")]
for s in sound_ids:
    if s not in sounds:
        errors.append(f"sound event without sounds.json entry: {s}")
    elif sounds[s].get("subtitle") not in en:
        errors.append(f"missing subtitle translation for {s}")

# Vanilla sound files referenced from sounds.json must exist. Checked against the asset index that
# ModDevGradle downloads for the run tasks; skipped when no index has been downloaded yet.
indexes = sorted(glob.glob(os.path.expanduser("~/.gradle/caches/neoformruntime/assets/indexes/*.json")), key=os.path.getmtime)
if indexes:
    vanilla = json.load(open(indexes[-1]))["objects"]
    for ev, body in sounds.items():
        for snd in body.get("sounds", []):
            name = snd if isinstance(snd, str) else snd["name"]
            if name.startswith("minecraft:") and f"minecraft/sounds/{name[10:]}.ogg" not in vanilla:
                errors.append(f"sound event {ev} uses missing vanilla sound {name}")
else:
    print("note: no Minecraft asset index found, vanilla sound paths not checked")

for p in glob.glob(f"{RES}/**/*.json", recursive=True):
    try:
        json.load(open(p, encoding="utf-8"))
    except Exception as ex:
        errors.append(f"invalid JSON {p}: {ex}")

if errors:
    print("\n".join(errors))
    sys.exit(1)
print("resources OK")
