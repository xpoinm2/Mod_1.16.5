# Heaven skybox asset placement

To use the custom cube-map style sky in the Heaven dimension, place the six textures listed below into
`src/main/resources/assets/examplemod/textures/environment/` inside your mod project:

- `skybox_front.png`
- `skybox_back.png`
- `skybox_left.png`
- `skybox_right.png`
- `skybox_top.png`
- `skybox_bottom.png`

Each texture corresponds to one face of the rendered skybox:

| Face orientation | Texture file |
| ---------------- | ------------ |
| +Z (South / front) | `skybox_front.png` |
| -Z (North / back) | `skybox_back.png` |
| +X (East / right) | `skybox_right.png` |
| -X (West / left) | `skybox_left.png` |
| +Y (Top) | `skybox_top.png` |
| -Y (Bottom) | `skybox_bottom.png` |

The renderer automatically loads these textures at runtime, so updating the image files is enough to
change the appearance of the Heaven sky.