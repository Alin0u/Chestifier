package dev.alinou.chestifier;

import dev.alinou.chestifier.interfaces.SlotClicker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.joml.Matrix3x2fStack;

/*
 * Warning - this code should extend ContainerScreen54 AND ShulkerBoxScreen,
 * which it can't. So we extend the superclass, and implement the few methods
 * that are in those classes (and are identical ...) ourselves. Doh.
 */

@SuppressWarnings({"unchecked", "rawtypes"})
public class ExtendedGuiChest extends AbstractContainerScreen {
    private final int inventoryRows;
    public static final Identifier ICONS = Identifier.fromNamespaceAndPath(Chestifier.MODID, "textures/icons.png");
    private final Identifier background;
    private final boolean separateBlits;

    public ExtendedGuiChest(ChestMenu container, Inventory lowerInv, Component title, int rows) {
        super(container, lowerInv, title, DEFAULT_IMAGE_WIDTH, 114 + rows * 18);
        this.inventoryRows = rows;
        background = Identifier.fromNamespaceAndPath("minecraft", "textures/gui/container/generic_54.png");
        separateBlits = true;
    }

    public ExtendedGuiChest(ShulkerBoxMenu container, Inventory lowerInv, Component title) {
        super(container, lowerInv, title);
        inventoryRows = 3;
        background = Identifier.fromNamespaceAndPath("minecraft", "textures/gui/container/shulker_box.png");
        separateBlits = false;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTicks) {
        extractTransparentBackground(extractor);
        if (separateBlits) {
            extractor.blit(RenderPipelines.GUI_TEXTURED, background, leftPos, topPos, 0, 0, this.imageWidth, this.inventoryRows * 18 + 17, 256, 256);
            extractor.blit(RenderPipelines.GUI_TEXTURED, background, leftPos, topPos + this.inventoryRows * 18 + 17, 0, 126, this.imageWidth, 96, 256, 256);
        } else {
            extractor.blit(RenderPipelines.GUI_TEXTURED, background, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        extractor.text(this.font, this.title, 8, 6, 4210752, false);
        extractor.text(this.font, this.playerInventoryTitle, 8, (this.imageHeight - 96 + 2), 4210752, false);
    }

    public static void drawChestInventoryBroom(GuiGraphicsExtractor extractor, AbstractContainerScreen<?> screen, int x, int y, int mouseX, int mouseY) {
        drawTexturedModalRectWithMouseHighlight(screen, extractor, x, y, 11 * 18, 0 * 18, 18, 18, mouseX, mouseY);
        myTooltip(extractor, x, y, 18, 18, mouseX, mouseY, Component.translatable("chestifier.sortchest"));
    }

    public static void drawChestInventoryAllDown(GuiGraphicsExtractor extractor, AbstractContainerScreen<?> screen, int x, int y, int mouseX, int mouseY) {
        drawTexturedModalRectWithMouseHighlight(screen, extractor, x, y, 0 * 18, 2 * 18, 18, 18, mouseX, mouseY);
        myTooltip(extractor, x, y, 18, 18, mouseX, mouseY, Component.translatable("chestifier.matchdown"));
    }

    public static void drawPlayerInventoryBroom(GuiGraphicsExtractor extractor, AbstractContainerScreen<?> screen, int x, int y, int mouseX, int mouseY) {
        drawTexturedModalRectWithMouseHighlight(screen, extractor, x, y, 11 * 18, 0 * 18, 18, 18, mouseX, mouseY);
        myTooltip(extractor, x, y, 18, 18, mouseX, mouseY, Component.translatable("chestifier.sortplayer"));
    }

    public static void drawPlayerInventoryAllUp(GuiGraphicsExtractor extractor, AbstractContainerScreen<?> screen, int x, int y, int mouseX, int mouseY) {
        drawTexturedModalRectWithMouseHighlight(screen, extractor, x, y, 8 * 18, 2 * 18, 18, 18, mouseX, mouseY);
        myTooltip(extractor, x, y, 18, 18, mouseX, mouseY, Component.translatable("chestifier.matchup"));
    }

    public static void drawTexturedModalRectWithMouseHighlight(AbstractContainerScreen<?> screen, GuiGraphicsExtractor extractor, int screenx, int screeny, int textx, int texty, int sizex, int sizey, int mousex, int mousey) {
        boolean hovered = mousex >= screenx && mousex < screenx + sizex && mousey >= screeny && mousey < screeny + sizey;
        if (hovered) {
            extractor.blit(RenderPipelines.GUI_TEXTURED, ICONS, screenx, screeny, textx, texty, sizex, sizey, 256, 256);
        } else if (ConfigurationHandler.halfSizeButtons()) {
            Matrix3x2fStack matrices = extractor.pose();
            matrices.pushMatrix();
            matrices.translate(screenx + sizex / 4.0f, screeny + sizey / 4.0f);
            matrices.scale(0.5f, 0.5f);
            matrices.translate(-(screenx + sizex / 4.0f), -(screeny + sizey / 4.0f));
            if (ConfigurationHandler.toneDownButtons()) {
                extractor.blit(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, ICONS, screenx, screeny, textx, texty, sizex, sizey, 256, 256);
            } else {
                extractor.blit(RenderPipelines.GUI_TEXTURED, ICONS, screenx, screeny, textx, texty, sizex, sizey, 256, 256);
            }
            matrices.popMatrix();
        } else if (ConfigurationHandler.toneDownButtons()) {
            extractor.blit(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, ICONS, screenx, screeny, textx, texty, sizex, sizey, 256, 256);
        } else {
            extractor.blit(RenderPipelines.GUI_TEXTURED, ICONS, screenx, screeny, textx, texty, sizex, sizey, 256, 256);
        }
    }

    private static void myTooltip(GuiGraphicsExtractor extractor, int screenx, int screeny, int sizex, int sizey, int mousex, int mousey, Component tooltip) {
        if (tooltip != null && mousex >= screenx && mousex <= screenx + sizex && mousey >= screeny && mousey <= screeny + sizey) {
            extractor.setTooltipForNextFrame(Minecraft.getInstance().font, tooltip, mousex, mousey);
        }
    }

    public static void sortInventory(SlotClicker screen, boolean isChest, Container inv) {
        int size = isChest ? inv.getContainerSize() : 36;
        if (size > 9 * 6 && !ConfigurationHandler.allowExtraLargeChests())
            size = 9 * 6;
        for (int toSlot = 0; toSlot < size; toSlot++) {
            ItemStack toStack = inv.getItem(toSlot);
            String targetItemName = toStack.getItem().getDescriptionId();
            if (toStack.getItem() == Items.AIR) {
                if (!isChest && toSlot < 9)
                    continue;
                targetItemName = "§§§";
            }

            if (isChest || toSlot >= 9 && FrozenSlotDatabase.isSlotActionable(toSlot)) {
                for (int fromSlot = toSlot + 1; fromSlot < size; fromSlot++) {
                    if (!isChest && !FrozenSlotDatabase.isSlotActionable(fromSlot))
                        continue;
                    ItemStack slotStack = inv.getItem(fromSlot);
                    if (slotStack.getItem() == Items.AIR)
                        continue;
                    String slotItem = inv.getItem(fromSlot).getItem().getDescriptionId();
                    if (slotItem.compareToIgnoreCase(targetItemName) < 0) {
                        targetItemName = slotItem;
                    }
                }
            } else {
                if (toStack.getCount() >= maxStackSize(toStack)) {
                    continue;
                }
            }

            for (int fromSlot = toSlot + 1; fromSlot < size; fromSlot++) {
                if (!isChest && !FrozenSlotDatabase.isSlotActionable(fromSlot)) {
                    continue;
                }
                toStack = inv.getItem(toSlot);
                ItemStack fromStack = inv.getItem(fromSlot);
                if (fromStack.getItem().getDescriptionId().equals(targetItemName)
                        && (!toStack.getItem().getDescriptionId().equals(targetItemName)
                        || stackShouldGoBefore(fromStack, toStack))) {
                    screen.Chestifier$onMouseClick(null, isChest ? fromSlot : screen.Chestifier$slotIndexfromPlayerInventoryIndex(fromSlot), 0, ContainerInput.PICKUP);
                    screen.Chestifier$onMouseClick(null, isChest ? toSlot : screen.Chestifier$slotIndexfromPlayerInventoryIndex(toSlot), 0, ContainerInput.PICKUP);
                    screen.Chestifier$onMouseClick(null, isChest ? fromSlot : screen.Chestifier$slotIndexfromPlayerInventoryIndex(fromSlot), 0, ContainerInput.PICKUP);
                }
            }
        }
    }

    private static int maxStackSize(ItemStack stack) {
        return stack.getOrDefault(DataComponents.MAX_STACK_SIZE, 1);
    }

    private static boolean stackShouldGoBefore(ItemStack replacement, ItemStack original) {
        String replacementName = replacement.getHoverName().getString();
        String originalName    = original.getHoverName().getString();

        if (replacementName.compareToIgnoreCase(originalName) > 0) {
            return false;
        }
        if (replacement.isDamageableItem() && original.isDamageableItem()
                && replacement.getDamageValue() > original.getDamageValue()) {
            return false;
        }

        // Enchanted books store their enchantments under STORED_ENCHANTMENTS
        ItemEnchantments originalEnch = original.getOrDefault(DataComponents.STORED_ENCHANTMENTS,
                original.getEnchantments());
        ItemEnchantments replacementEnch = replacement.getOrDefault(DataComponents.STORED_ENCHANTMENTS,
                replacement.getEnchantments());

        if (replacementEnch.isEmpty()) {
            if (originalEnch.isEmpty()) {
                return original.getCount() != maxStackSize(original);
            }
            return true;
        }
        if (originalEnch.isEmpty()) {
            return false;
        }
        return enchantedReplacementShouldGoBefore(replacementEnch, originalEnch);
    }

    /** Both stacks are enchanted: fewer enchantments sort first, then compare by sorted id/level. */
    private static boolean enchantedReplacementShouldGoBefore(ItemEnchantments replacementEnch, ItemEnchantments originalEnch) {
        int replSize = replacementEnch.size();
        int origSize = originalEnch.size();
        if (replSize < origSize) {
            return true;
        }
        if (replSize > origSize) {
            return false;
        }
        // Compare enchantment IDs by sorted order, carrying each entry's level along
        // instead of re-deriving it via a second lookup that could throw if the two
        // streams ever go out of sync.
        var replEntries = replacementEnch.keySet().stream()
                .map(e -> new EnchEntry(e.getRegisteredName(), replacementEnch.getLevel(e)))
                .sorted((a, b) -> a.id().compareTo(b.id())).toList();
        var origEntries = originalEnch.keySet().stream()
                .map(e -> new EnchEntry(e.getRegisteredName(), originalEnch.getLevel(e)))
                .sorted((a, b) -> a.id().compareTo(b.id())).toList();
        for (int i = 0; i < replSize; i++) {
            EnchEntry origEntry = origEntries.get(i);
            EnchEntry replEntry = replEntries.get(i);
            int cmp = origEntry.id().compareTo(replEntry.id());
            if (cmp < 0) return false;
            if (cmp > 0) return true;
            if (origEntry.level() != replEntry.level()) return replEntry.level() < origEntry.level();
        }
        return false;
    }

    private record EnchEntry(String id, int level) {}

    public static void moveMatchingItems(AbstractContainerScreen<?> screen, boolean isChestToPlayer) {
        Container from, to;
        int fromSize, toSize;
        Minecraft minecraft = Minecraft.getInstance();
        if (screen.getMenu().slots.isEmpty()) return;
        Container containerInventory = screen.getMenu().getSlot(0).container;

        if (isChestToPlayer) {
            from = containerInventory;               fromSize = from.getContainerSize();
            to   = minecraft.player.getInventory(); toSize   = 36;
        } else {
            from = minecraft.player.getInventory(); fromSize = 36;
            to   = containerInventory;               toSize   = to.getContainerSize();
        }
        if (!ConfigurationHandler.allowExtraLargeChests()) {
            if (fromSize > 9 * 6) fromSize = 9 * 6;
            if (toSize   > 9 * 6) toSize   = 9 * 6;
        }
        for (int i = 0; i < fromSize; i++) {
            if (!isChestToPlayer && !FrozenSlotDatabase.isSlotActionable(i))
                continue;
            ItemStack fromStack = from.getItem(i);
            int slot;
            if (isChestToPlayer) {
                slot = i;
            } else {
                slot = ((SlotClicker) screen).Chestifier$slotIndexfromPlayerInventoryIndex(i);
            }
            for (int j = 0; j < toSize; j++) {
                ItemStack toStack = to.getItem(j);
                if (ItemStack.isSameItemSameComponents(fromStack, toStack)) {
                    ((SlotClicker) screen).Chestifier$onMouseClick(null, slot, 0, ContainerInput.QUICK_MOVE);
                }
            }
        }
    }
}
