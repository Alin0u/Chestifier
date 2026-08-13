package dev.alinou.chestifier;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.alinou.chestifier.interfaces.SlotClicker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/*
 * Warning - this code should extend ContainerScreen54 AND ShulkerBoxScreen,
 * which it can't. So we extend the superclass, and implement the few methods
 * that are in those classes (and are identical ...) ourselves. Doh.
 */

@SuppressWarnings({"unchecked", "rawtypes"})
public class ExtendedGuiChest extends HandledScreen {
    private final int inventoryRows;
    public static final Identifier ICONS = Identifier.of(Chestifier.MODID, "textures/icons.png");
    private final Identifier background;
    private final boolean separateBlits;

    public ExtendedGuiChest(GenericContainerScreenHandler container, PlayerInventory lowerInv, Text title, int rows) {
        super(container, lowerInv, title);
        this.inventoryRows = rows;
        backgroundHeight = 114 + rows * 18;
        background = Identifier.of("minecraft", "textures/gui/container/generic_54.png");
        separateBlits = true;
    }

    public ExtendedGuiChest(ShulkerBoxScreenHandler container, PlayerInventory lowerInv, Text title) {
        super(container, lowerInv, title);
        inventoryRows = 3;
        background = Identifier.of("minecraft", "textures/gui/container/shulker_box.png");
        separateBlits = false;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        renderInGameBackground(context);
        super.render(context, mouseX, mouseY, partialTicks);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, 8, 6, 4210752, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, 8, (this.backgroundHeight - 96 + 2), 4210752, false);
    }

    @Override
    protected void drawBackground(DrawContext context, float partialTicks, int mouseX, int mouseY) {
        if (separateBlits) {
            context.drawTexture(background, x, y, 0, 0, this.backgroundWidth, this.inventoryRows * 18 + 17, 256, 256);
            context.drawTexture(background, x, y + this.inventoryRows * 18 + 17, 0, 126, this.backgroundWidth, 96, 256, 256);
        } else {
            context.drawTexture(background, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
        }
    }

    public static void drawChestInventoryBroom(DrawContext context, HandledScreen<?> screen, int x, int y, int mouseX, int mouseY) {
        drawTexturedModalRectWithMouseHighlight(screen, context, x, y, 11 * 18, 0 * 18, 18, 18, mouseX, mouseY);
        myTooltip(context, x, y, 18, 18, mouseX, mouseY, Text.translatable("chestifier.sortchest"));
    }

    public static void drawChestInventoryAllDown(DrawContext context, HandledScreen<?> screen, int x, int y, int mouseX, int mouseY) {
        drawTexturedModalRectWithMouseHighlight(screen, context, x, y, 0 * 18, 2 * 18, 18, 18, mouseX, mouseY);
        myTooltip(context, x, y, 18, 18, mouseX, mouseY, Text.translatable("chestifier.matchdown"));
    }

    public static void drawPlayerInventoryBroom(DrawContext context, HandledScreen<?> screen, int x, int y, int mouseX, int mouseY) {
        drawTexturedModalRectWithMouseHighlight(screen, context, x, y, 11 * 18, 0 * 18, 18, 18, mouseX, mouseY);
        myTooltip(context, x, y, 18, 18, mouseX, mouseY, Text.translatable("chestifier.sortplayer"));
    }

    public static void drawPlayerInventoryAllUp(DrawContext context, HandledScreen<?> screen, int x, int y, int mouseX, int mouseY) {
        drawTexturedModalRectWithMouseHighlight(screen, context, x, y, 8 * 18, 2 * 18, 18, 18, mouseX, mouseY);
        myTooltip(context, x, y, 18, 18, mouseX, mouseY, Text.translatable("chestifier.matchup"));
    }

    public static void drawTexturedModalRectWithMouseHighlight(HandledScreen<?> screen, DrawContext context, int screenx, int screeny, int textx, int texty, int sizex, int sizey, int mousex, int mousey) {
        boolean hovered = mousex >= screenx && mousex < screenx + sizex && mousey >= screeny && mousey < screeny + sizey;
        if (hovered) {
            context.drawTexture(ICONS, screenx, screeny, textx, texty, sizex, sizey, 256, 256);
        } else if (ConfigurationHandler.halfSizeButtons()) {
            MatrixStack matrices = context.getMatrices();
            matrices.push();
            matrices.translate(screenx + sizex / 4.0f, screeny + sizey / 4.0f, 0);
            matrices.scale(0.5f, 0.5f, 1.0f);
            matrices.translate(-(screenx + sizex / 4.0f), -(screeny + sizey / 4.0f), 0);
            if (ConfigurationHandler.toneDownButtons()) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.3f);
                context.drawTexture(ICONS, screenx, screeny, textx, texty, sizex, sizey, 256, 256);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            } else {
                context.drawTexture(ICONS, screenx, screeny, textx, texty, sizex, sizey, 256, 256);
            }
            matrices.pop();
        } else if (ConfigurationHandler.toneDownButtons()) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.3f);
            context.drawTexture(ICONS, screenx, screeny, textx, texty, sizex, sizey, 256, 256);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            context.drawTexture(ICONS, screenx, screeny, textx, texty, sizex, sizey, 256, 256);
        }
    }

    private static void myTooltip(DrawContext context, int screenx, int screeny, int sizex, int sizey, int mousex, int mousey, Text tooltip) {
        if (tooltip != null && mousex >= screenx && mousex <= screenx + sizex && mousey >= screeny && mousey <= screeny + sizey) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, tooltip, mousex, mousey);
        }
    }

    public static void sortInventory(SlotClicker screen, boolean isChest, Inventory inv) {
        int size = isChest ? inv.size() : 36;
        if (size > 9 * 6 && !ConfigurationHandler.allowExtraLargeChests())
            size = 9 * 6;
        for (int toSlot = 0; toSlot < size; toSlot++) {
            ItemStack toStack = inv.getStack(toSlot);
            String targetItemName = toStack.getItem().getTranslationKey();
            if (toStack.getItem() == Items.AIR) {
                if (!isChest && toSlot < 9)
                    continue;
                targetItemName = "§§§";
            }

            if (isChest || toSlot >= 9 && FrozenSlotDatabase.isSlotActionable(toSlot)) {
                for (int fromSlot = toSlot + 1; fromSlot < size; fromSlot++) {
                    if (!isChest && !FrozenSlotDatabase.isSlotActionable(fromSlot))
                        continue;
                    ItemStack slotStack = inv.getStack(fromSlot);
                    if (slotStack.getItem() == Items.AIR)
                        continue;
                    String slotItem = inv.getStack(fromSlot).getItem().getTranslationKey();
                    if (slotItem.compareToIgnoreCase(targetItemName) < 0) {
                        targetItemName = slotItem;
                    }
                }
            } else {
                if (toStack.getCount() >= toStack.getMaxCount()) {
                    continue;
                }
            }

            for (int fromSlot = toSlot + 1; fromSlot < size; fromSlot++) {
                if (!isChest && !FrozenSlotDatabase.isSlotActionable(fromSlot)) {
                    continue;
                }
                toStack = inv.getStack(toSlot);
                ItemStack fromStack = inv.getStack(fromSlot);
                if (fromStack.getItem().getTranslationKey().equals(targetItemName)
                        && (!toStack.getItem().getTranslationKey().equals(targetItemName)
                        || stackShouldGoBefore(fromStack, toStack))) {
                    screen.Chestifier$onMouseClick(null, isChest ? fromSlot : screen.Chestifier$slotIndexfromPlayerInventoryIndex(fromSlot), 0, SlotActionType.PICKUP);
                    screen.Chestifier$onMouseClick(null, isChest ? toSlot : screen.Chestifier$slotIndexfromPlayerInventoryIndex(toSlot), 0, SlotActionType.PICKUP);
                    screen.Chestifier$onMouseClick(null, isChest ? fromSlot : screen.Chestifier$slotIndexfromPlayerInventoryIndex(fromSlot), 0, SlotActionType.PICKUP);
                }
            }
        }
    }

    private static boolean stackShouldGoBefore(ItemStack replacement, ItemStack original) {
        String replacementName = replacement.getName().getString();
        String originalName    = original.getName().getString();

        if (replacementName.compareToIgnoreCase(originalName) > 0) {
            return false;
        }
        if (replacement.isDamageable() && original.isDamageable()
                && replacement.getDamage() > original.getDamage()) {
            return false;
        }

        // In 1.21.11, enchantments are stored as ItemEnchantmentsComponent
        // Enchanted books use DataComponentTypes.STORED_ENCHANTMENTS
        ItemEnchantmentsComponent originalEnch = original.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS,
                original.getEnchantments());
        ItemEnchantmentsComponent replacementEnch = replacement.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS,
                replacement.getEnchantments());

        if (replacementEnch.isEmpty()) {
            if (originalEnch.isEmpty()) {
                return original.getCount() != original.getMaxCount();
            }
            return true;
        }
        if (originalEnch.isEmpty()) {
            return false;
        }
        return enchantedReplacementShouldGoBefore(replacementEnch, originalEnch);
    }

    /** Both stacks are enchanted: fewer enchantments sort first, then compare by sorted id/level. */
    private static boolean enchantedReplacementShouldGoBefore(ItemEnchantmentsComponent replacementEnch, ItemEnchantmentsComponent originalEnch) {
        int replSize = replacementEnch.getSize();
        int origSize = originalEnch.getSize();
        if (replSize < origSize) {
            return true;
        }
        if (replSize > origSize) {
            return false;
        }
        // Compare enchantment IDs by sorted order (RegistryEntry has getId)
        var replEntries = replacementEnch.getEnchantments().stream()
                .map(e -> e.getIdAsString()).sorted().toList();
        var origEntries = originalEnch.getEnchantments().stream()
                .map(e -> e.getIdAsString()).sorted().toList();
        for (int i = 0; i < replSize; i++) {
            String origId = origEntries.get(i);
            String replId = replEntries.get(i);
            int cmp = origId.compareTo(replId);
            if (cmp < 0) return false;
            if (cmp > 0) return true;
            int origLevel = originalEnch.getLevel(originalEnch.getEnchantments().stream()
                    .filter(e -> e.getIdAsString().equals(origId)).findFirst().orElseThrow().value());
            int replLevel = replacementEnch.getLevel(replacementEnch.getEnchantments().stream()
                    .filter(e -> e.getIdAsString().equals(replId)).findFirst().orElseThrow().value());
            if (origLevel != replLevel) return replLevel < origLevel;
        }
        return false;
    }

    public static void moveMatchingItems(HandledScreen<?> screen, boolean isChestToPlayer) {
        Inventory from, to;
        int fromSize, toSize;
        MinecraftClient minecraft = MinecraftClient.getInstance();
        Inventory containerInventory = screen.getScreenHandler().getSlot(0).inventory;

        if (isChestToPlayer) {
            from = containerInventory;               fromSize = from.size();
            to   = minecraft.player.getInventory(); toSize   = 36;
        } else {
            from = minecraft.player.getInventory(); fromSize = 36;
            to   = containerInventory;               toSize   = to.size();
        }
        if (!ConfigurationHandler.allowExtraLargeChests()) {
            if (fromSize > 9 * 6) fromSize = 9 * 6;
            if (toSize   > 9 * 6) toSize   = 9 * 6;
        }
        for (int i = 0; i < fromSize; i++) {
            if (!isChestToPlayer && !FrozenSlotDatabase.isSlotActionable(i))
                continue;
            ItemStack fromStack = from.getStack(i);
            int slot;
            if (isChestToPlayer) {
                slot = i;
            } else {
                slot = ((SlotClicker) screen).Chestifier$slotIndexfromPlayerInventoryIndex(i);
            }
            for (int j = 0; j < toSize; j++) {
                ItemStack toStack = to.getStack(j);
                if (ItemStack.areItemsAndComponentsEqual(fromStack, toStack)) {
                    ((SlotClicker) screen).Chestifier$onMouseClick(null, slot, 0, SlotActionType.QUICK_MOVE);
                }
            }
        }
    }
}
