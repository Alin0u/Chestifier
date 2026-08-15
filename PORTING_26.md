# Porting notes: Minecraft 26.x

Status: build configuration done, source migration not started.
This branch does not compile yet.

## Why this port is different

Mojang removed obfuscation from Java Edition, so 26.x jars ship real names
and no mapping files. Yarn stopped at 1.21.11 and has no 26.x branch, so the
source has to move from Yarn names to Mojang names.

More importantly, GUI rendering was rearchitected. Screens no longer draw
directly. They describe what to draw into a `GuiGraphicsExtractor`, and
rendering happens later from that extracted state.

## Build configuration (done)

- Loom `1.17-SNAPSHOT`, Gradle 9.5.1, Java 25
- No `mappings` line and no `yarn_mappings` property
- `modImplementation` / `modCompileOnly` become `implementation` / `compileOnly`
- No mixin refmap, removed from `mixins.chestifier.json` and `build.gradle`

## Source migration (not started)

31 Minecraft imports across 10 files, roughly 1100 lines.

Class renames:

| Yarn | Mojang |
| --- | --- |
| `HandledScreen` | `AbstractContainerScreen` |
| `HandledScreens` | `MenuScreens` |
| `TextFieldWidget` | `EditBox` |
| `MinecraftClient` | `Minecraft` |
| `KeyBinding` | `KeyMapping` |
| `Text` | `Component` |
| `ScreenHandler` | `AbstractContainerMenu` |
| `GenericContainerScreenHandler` | `ChestMenu` |
| `ShulkerBoxScreenHandler` | `ShulkerBoxMenu` |
| `PlayerScreenHandler` | `InventoryMenu` |
| `ScreenHandlerType` | `MenuType` |
| `PlayerInventory` | `Inventory` |
| `DataComponentTypes` | `DataComponents` |
| `ItemEnchantmentsComponent` | `ItemEnchantments` |
| `Identifier` | `Identifier` (moved to `net.minecraft.resources`) |

Field renames on the screen: `x`/`y` are `leftPos`/`topPos`, `handler` is `menu`,
`backgroundWidth`/`backgroundHeight` are `imageWidth`/`imageHeight`.

## The blocker

The mod's drawing layer targets hooks that no longer exist:

| Current injection | 26.x |
| --- | --- |
| `render(DrawContext, int, int, float)` | gone, see `extractRenderState(GuiGraphicsExtractor, int, int, float)` |
| `drawSlot(DrawContext, Slot)` | gone, see `extractSlot(GuiGraphicsExtractor, Slot, int, int)` |
| `DrawContext` | does not exist |
| `context.drawTexture(...)` | not how drawing works anymore |

Input also changed: `mouseClicked(MouseButtonEvent, boolean)`,
`keyPressed(KeyEvent)`, and `slotClicked` takes a `ContainerInput`.

So the button drawing, frozen-slot markers, search highlighting and slot-index
overlay all need rewriting against the new model, not just renaming.

## Suggested next step

Read `GuiGraphicsExtractor` and how a vanilla screen uses it before changing
any mod code. Worth revisiting once other mods have published 26.x ports,
since there are no Yarn docs and few examples yet.
