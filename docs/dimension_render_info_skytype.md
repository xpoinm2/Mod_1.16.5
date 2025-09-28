# DimensionRenderInfo SkyType in Forge 1.16.5

The Forge MDK for Minecraft 1.16.5 ships with Mojang's official mappings enabled by default. In these
mappings the sky rendering options are represented by the nested `enum` `SkyType` inside
`net.minecraft.client.world.DimensionRenderInfo`. The example mod included in this repository already
imports and uses this type when constructing the custom `HeavenDimensionRenderInfo`.

If you are working in an IDE that is configured with different mappings (for example a community fork or
custom Yarn build) the nested enum may appear under a different name. In that case you can still refer to
it with the fully-qualified binary name `DimensionRenderInfo$SkyType`, or switch your project back to the
Mojang mappings configured in `build.gradle`.

For reference, the Heaven dimension render info registers with
`registerDimensionRenderInfo` by passing `SkyType.NONE`, which disables vanilla skyboxes while allowing the
custom renderer to draw its own cube map sky. You can find the exact usage in
`src/main/java/com/example/examplemod/client/render/HeavenDimensionRenderInfo.java`.