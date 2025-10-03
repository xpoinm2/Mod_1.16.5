# Quest Screen Source Files

The refactored quest screens that now share the `AbstractQuestScreen` base class live under
`src/main/java/com/example/examplemod/client/screen`.

| File | Description |
| --- | --- |
| `AbstractQuestScreen.java` | Shared base class that draws the pinned title, action buttons, and column headers. |
| `ScrollArea.java` | Lightweight helper for rendering scrollable viewports backed by `AbstractQuestScreen`. |
| `BranchQuestScreen.java` | Branch quest implementation wired to the shared layout. |
| `BrushwoodQuestScreen.java` | Brushwood quest implementation wired to the shared layout. |
| `BoneToolsQuestScreen.java` | Bone tools quest implementation wired to the shared layout. |
| `BigBoneQuestScreen.java` | Big Bone quest implementation wired to the shared layout. |
| `CobbleSlabQuestScreen.java` | Cobble Slab quest implementation wired to the shared layout. |
| `CombsQuestScreen.java` | Combs quest implementation wired to the shared layout. |
| `FirepitQuestScreen.java` | Firepit quest implementation with blueprint hover tooltips and the shared layout. |

Each file can be copied directly if you need to migrate the implementation into another project or branch.