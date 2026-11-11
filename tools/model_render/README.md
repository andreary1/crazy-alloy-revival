# Model renders (outside the game)

Transparent PNGs of the mob models, built from the model code and the entity textures, without launching the client.

1. `./gradlew compileJava`, then get the main compile classpath plus `build/classes/java/main` (for example with a small init script).
2. `javac -cp "$CP" -d classes tools/model_render/ExportMeshes.java && java -cp "$CP:classes" ExportMeshes meshes`
3. `python3 tools/model_render/render.py meshes/<mob>.json src/main/resources/assets/crazyalloy_revival/textures/entity/<mob>.png out.png yaw=215 pitch=22 size=1024`

`yaw=215 pitch=22` is the 3/4 view from above with the head toward the front right. Needs numpy and Pillow.
Only the base model and texture are drawn: no render layers (glow, held items) and no in-game lighting.
