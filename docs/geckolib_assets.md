# GeckoLib Asset Layout

GeckoLib expects its model, texture, and animation assets to live on the resource
path (`src/main/resources/assets/<modid>/`). In this project the beaver entity is
configured to look up resources under the `examplemod` namespace from
`BeaverModelGeo`.

| Asset type | Expected location | Current file |
|------------|-------------------|--------------|
| Model geometry (`*.geo.json`) | `assets/examplemod/geo/` | `assets/examplemod/geo/beaver.geo.json` |
| Animation (`*.animation.json`) | `assets/examplemod/animations/` | `assets/examplemod/animations/beaver.animation.json` |
| Texture (`*.png`) | `assets/examplemod/textures/entity/<entity>/` | `assets/examplemod/textures/entity/beaver/beaver.png` |

Ensure that new GeckoLib models follow the same pattern; files placed in the Java
source tree will be ignored at runtime.