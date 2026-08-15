package dev.alinou.chestifier.mixins;

import dev.alinou.chestifier.ConfigurationHandler;
import dev.alinou.chestifier.Chestifier;
import dev.alinou.chestifier.ExtendedGuiChest;
import dev.alinou.chestifier.FrozenSlotDatabase;
import dev.alinou.chestifier.InventoryLayout;
import dev.alinou.chestifier.KeyModifiers;
import dev.alinou.chestifier.interfaces.SlotClicker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.alinou.chestifier.storagemodapi.ChestGuiInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen implements SlotClicker {

    private static final int PLAYERSLOTS = InventoryLayout.PLAYER_SLOTS;
    private static final int PLAYERINVCOLS = InventoryLayout.PLAYER_INV_COLS;
    private static final int PLAYERINVROWS = InventoryLayout.PLAYER_INV_ROWS;

    private EditBox searchWidget;

    @Shadow protected abstract void slotClicked(Slot slot, int invSlot, int button, ContainerInput slotActionType);
    @Shadow protected abstract boolean isHovering(int x, int y, int w, int h, double pX, double pY);
    @Shadow @Final protected AbstractContainerMenu menu;
    @Shadow protected int leftPos, topPos;
    @Shadow @Final protected int imageWidth, imageHeight;

    protected AbstractContainerScreenMixin() { super(null); }

    @Override
    public void Chestifier$onMouseClick(Slot slot, int invSlot, int button, ContainerInput slotActionType) {
        this.slotClicked(slot, invSlot, button, slotActionType);
    }

    @Override
    public int Chestifier$getPlayerInventoryStartIndex() {
        if (menu instanceof InventoryMenu) {
            return PLAYERINVCOLS;
        } else {
            return this.menu.slots.size() - PLAYERSLOTS;
        }
    }

    @Override
    public int Chestifier$playerInventoryIndexFromSlotIndex(int slot) {
        int firstSlot = Chestifier$getPlayerInventoryStartIndex();
        if (slot < firstSlot) {
            return -1;
        } else if (slot < firstSlot + (PLAYERSLOTS - PLAYERINVCOLS)) {
            return slot - firstSlot + PLAYERINVCOLS;
        } else {
            return slot - firstSlot - (PLAYERSLOTS - PLAYERINVCOLS);
        }
    }

    @Override
    public int Chestifier$slotIndexfromPlayerInventoryIndex(int slot) {
        int firstSlot = Chestifier$getPlayerInventoryStartIndex();
        if (slot < PLAYERINVCOLS) {
            return slot + firstSlot + (PLAYERSLOTS - PLAYERINVCOLS);
        } else {
            return slot + firstSlot - PLAYERINVCOLS;
        }
    }

    // Draws frozen-slot markers before the vanilla slot/item extraction pass (HEAD),
    // so the marker sits behind the item instead of on top of it. extractSlot() isn't a
    // reliable hook for this: it isn't called for the hotbar row on all screens.
    @Inject(method = "extractRenderState", at = @At("HEAD"))
    public void Chestifier$DrawFrozenSlotMarkers(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!isSupportedScreenHandler(menu) || KeyModifiers.hasShiftDown()) {
            return;
        }
        for (int i = 0; i < PLAYERSLOTS; i++) {
            if (FrozenSlotDatabase.isSlotFrozen(i)) {
                Slot slot = this.menu.slots.get(Chestifier$slotIndexfromPlayerInventoryIndex(i));
                extractor.blit(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, ExtendedGuiChest.ICONS, leftPos + slot.x, topPos + slot.y, 7 * 18 + 1, 3 * 18 + 1, 16, 16, 256, 256);
            }
        }
    }

    @Inject(method = "extractSlot", at = @At("RETURN"))
    public void Chestifier$DrawSlotIndex(GuiGraphicsExtractor extractor, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        if (KeyModifiers.hasAltDown()) {
            extractor.text(this.font, Integer.toString(slot.index), slot.x, slot.y, 0x808090, false);
        }
    }

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    public void Chestifier$renderSpecialButtons(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        AbstractContainerScreen<?> hScreen = (AbstractContainerScreen<?>) (Object) this;

        ExtendedGuiChest.drawPlayerInventoryBroom(extractor, hScreen, leftPos + imageWidth, topPos + imageHeight - 30 - 3 * 18, mouseX, mouseY);
        if (isSupportedScreenHandler(menu)) {

            int cols = getSlotColumnCount();
            int rows = getSlotRowCount();

            if (ConfigurationHandler.enableColumnButtons()) {
                int startx = (leftPos + imageWidth / 2) - (18 / 2) * cols;
                for (int i = 0; i < cols; i++) {
                    ExtendedGuiChest.drawTexturedModalRectWithMouseHighlight(hScreen, extractor, startx + i * 18, topPos + -18,           1 * 18, 2 * 18, 18, 18, mouseX, mouseY);
                }
                startx = (leftPos + imageWidth / 2) - 9 * PLAYERINVCOLS;
                for (int i = 0; i < PLAYERINVCOLS; i++) {
                    ExtendedGuiChest.drawTexturedModalRectWithMouseHighlight(hScreen, extractor, startx + i * 18, topPos + 40 + (rows + 4) * 18, 9 * 18, 2 * 18, 18, 18, mouseX, mouseY);
                }
            }

            if (ConfigurationHandler.enableRowButtons()) {
                for (int i = 0; i < rows; i++) {
                    ExtendedGuiChest.drawTexturedModalRectWithMouseHighlight(hScreen, extractor, leftPos + -18, topPos + 17 + i * 18,        1 * 18, 2 * 18, 18, 18, mouseX, mouseY);
                }
                for (int i = 0; i < PLAYERINVROWS; i++) {
                    ExtendedGuiChest.drawTexturedModalRectWithMouseHighlight(hScreen, extractor, leftPos + -18, topPos + 28 + (i + rows) * 18, 9 * 18, 2 * 18, 18, 18, mouseX, mouseY);
                }
            }

            if (ConfigurationHandler.enableSearch()) {
                if (searchWidget == null) {
                    searchWidget = new EditBox(font, leftPos + imageWidth - 85, topPos + 3, 80, 12, Component.literal("Search"));
                } else {
                    searchWidget.setX(leftPos + imageWidth - 85);
                    searchWidget.setY(topPos + 3);
                }
                searchWidget.extractRenderState(extractor, mouseX, mouseY, delta);

                String search = searchWidget.getValue().toLowerCase();
                if (!search.isEmpty()) {
                    int highlight = (int) Long.parseLong(ConfigurationHandler.getHighlightColor().toUpperCase(), 16);
                    for (int i = 0; i < this.menu.slots.size(); i++) {
                        Slot slot = this.menu.slots.get(i);
                        Item item = slot.getItem().getItem();
                        if (item == Items.AIR) {
                            continue;
                        }
                        if (I18n.get(item.getDescriptionId()).toLowerCase().contains(search)) {
                            extractor.fill(leftPos + slot.x - 1, topPos + slot.y - 1, leftPos + slot.x + 18 - 1, topPos + slot.y + 18 - 1, highlight);
                        }
                    }
                }
            }
            ExtendedGuiChest.drawPlayerInventoryAllUp(extractor, hScreen, leftPos + imageWidth, topPos + imageHeight - 30 - 2 * 18, mouseX, mouseY);
            ExtendedGuiChest.drawChestInventoryBroom(extractor, hScreen, leftPos + imageWidth, topPos + 17, mouseX, mouseY);
            ExtendedGuiChest.drawChestInventoryAllDown(extractor, hScreen, leftPos + this.imageWidth, topPos + 17 + 18, mouseX, mouseY);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void Chestifier$checkMyButtons(MouseButtonEvent event, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        double mouseX = event.x();
        double mouseY = event.y();
        int mouseButton = event.button();

        if (isSupportedScreenHandler(menu)
        && ConfigurationHandler.enableSearch()
        && searchWidget != null
        && searchWidget.mouseClicked(event, bl)) {
            searchWidget.setFocused(true);
            cir.setReturnValue(true);
            cir.cancel();
            return;
        } else if (searchWidget != null) {
            searchWidget.setFocused(false);
        }

        if (mouseX >= leftPos + imageWidth && mouseX <= leftPos + imageWidth + 18) {
            AbstractContainerScreen<?> hScreen = (AbstractContainerScreen<?>) (Screen) this;
            if (mouseY >= topPos + imageHeight - 30 - 3 * 18 && mouseY < topPos + imageHeight - 30 - 2 * 18) {
                ExtendedGuiChest.sortInventory(this, false, Minecraft.getInstance().player.getInventory());
                cir.setReturnValue(true);
                cir.cancel();
                return;
            } else if (mouseY >= topPos + imageHeight - 30 - 2 * 18 && mouseY < topPos + imageHeight - 30 - 1 * 18) {
                if (!isSupportedScreenHandler(menu)) return;
                ExtendedGuiChest.moveMatchingItems(hScreen, false);
                cir.setReturnValue(true);
                cir.cancel();
                return;
            } else if (!isSupportedScreenHandler(menu)) {
                return;
            } else if (mouseY > topPos + 17 && mouseY < topPos + 17 + 18) {
                if (menu.slots.isEmpty()) return;
                ExtendedGuiChest.sortInventory(this, true, menu.getSlot(0).container);
                cir.setReturnValue(true);
                cir.cancel();
                return;
            } else if (mouseY > topPos + 17 + 18 && mouseY < topPos + 17 + 36) {
                ExtendedGuiChest.moveMatchingItems(hScreen, true);
                cir.setReturnValue(true);
                cir.cancel();
                return;
            }
        }
        if (!isSupportedScreenHandler(menu)) {
            return;
        }
        if (mouseButton == 0 && checkForMyButtons(mouseX, mouseY)) {
            cir.setReturnValue(true);
            cir.cancel();
            return;
        }
        if (mouseButton == 2 && checkForToggleFrozen(mouseX, mouseY)) {
            cir.setReturnValue(true);
            cir.cancel();
            return;
        }
    }

    private boolean checkForMyButtons(double mouseX, double mouseY) {
        int rows = getSlotRowCount();
        int cols = getSlotColumnCount();

        if (ConfigurationHandler.enableRowButtons() && mouseX >= leftPos - 18 && mouseX <= leftPos) {
            int deltay = (int) mouseY - topPos;
            if (deltay < rows * 18 + 17) {
                clickSlotsInRow((deltay - 17) / 18);
                return true;
            } else if (deltay < (rows + PLAYERINVROWS) * 18 + 28) {
                clickSlotsInRow((deltay - 28) / 18);
                return true;
            }
        }
        if (ConfigurationHandler.enableColumnButtons() && mouseX > leftPos + 7 && mouseX < leftPos + imageWidth) {
            boolean isChest;
            int column;
            if (mouseY > topPos - 18 && mouseY < topPos) {
                int startx = leftPos + imageWidth / 2 - (18 / 2) * cols;
                isChest = true;
                column = ((int) mouseX - startx) / 18;
                if (column < 0 || column >= cols) {
                    return false;
                }
            } else if (mouseY > topPos + 40 + (rows + PLAYERINVROWS) * 18 && mouseY < topPos + 40 + (rows + PLAYERINVROWS) * 18 + 18) {
                int startx = leftPos + imageWidth / 2 - (18 / 2) * PLAYERINVCOLS;
                isChest = false;
                column = ((int) mouseX - startx) / 18;
                if (column < 0 || column >= PLAYERINVCOLS) {
                    return false;
                }
            } else {
                return false;
            }
            clickSlotsInColumn(column, isChest);
            return true;
        }
        return false;
    }

    private boolean checkForToggleFrozen(double mouseX, double mouseY) {
        for (int i = 0; i < this.menu.slots.size(); ++i) {
            int invIndex = this.Chestifier$playerInventoryIndexFromSlotIndex(i);
            if (invIndex == -1)
                continue;
            Slot slot = this.menu.slots.get(i);
            if (isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY)) {
                FrozenSlotDatabase.setSlotFrozen(invIndex, !FrozenSlotDatabase.isSlotFrozen(invIndex));
                return true;
            }
        }
        return false;
    }

    private void clickSlotsInRow(int row) {
        int rows = getSlotRowCount();
        int cols = getSlotColumnCount();
        int firstSlot;

        if (row <= rows) {
            firstSlot = row * cols;
        } else {
            firstSlot = rows * cols + (row - rows) * PLAYERINVCOLS;
            cols = PLAYERINVCOLS;
        }

        for (int slot = firstSlot; slot < firstSlot + cols; slot++)
            if (FrozenSlotDatabase.isSlotActionable(Chestifier$playerInventoryIndexFromSlotIndex(slot))) {
                slotClick(slot, 0, ContainerInput.QUICK_MOVE);
            }
    }

    private void clickSlotsInColumn(int column, boolean isChest) {
        int cols = getSlotColumnCount();
        int rows = getSlotRowCount();
        int first, count;

        if (isChest) {
            first = column;
            count = rows;
        } else {
            first = rows * cols + column;
            count = PLAYERINVROWS;
            cols = PLAYERINVCOLS;
        }
        for (int i = 0; i < count; i++) {
            int slot = first + i * cols;
            if (FrozenSlotDatabase.isSlotActionable(Chestifier$playerInventoryIndexFromSlotIndex(slot)))
                slotClick(slot, 0, ContainerInput.QUICK_MOVE);
        }
    }

    private void slotClick(int slot, int mouseButton, ContainerInput clickType) {
        ((SlotClicker) this).Chestifier$onMouseClick(null, slot, mouseButton, clickType);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void Chestifier$keyPressed(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        AbstractContainerScreen<?> hScreen = (AbstractContainerScreen<?>) (Screen) this;
        int keyCode = keyEvent.key();

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            return;
        }

        if (isSupportedScreenHandler(menu)
        && ConfigurationHandler.enableSearch()
        && searchWidget != null
        && searchWidget.isActive()) {
            boolean value = searchWidget.keyPressed(keyEvent);
            cir.setReturnValue(value);
            cir.cancel();
            return;
        }

        if (Chestifier.keySortPlInv.matches(keyEvent)) {
            ExtendedGuiChest.sortInventory(this, false, Minecraft.getInstance().player.getInventory());
            cir.setReturnValue(true);
            cir.cancel();
        } else if (!isSupportedScreenHandler(menu)) {
            return;
        } else if (Chestifier.keyMoveToChest.matches(keyEvent)) {
            ExtendedGuiChest.moveMatchingItems(hScreen, false);
            cir.setReturnValue(true);
            cir.cancel();
        } else if (Chestifier.keySortChest.matches(keyEvent)) {
            if (menu.slots.isEmpty()) return;
            ExtendedGuiChest.sortInventory(this, true, menu.getSlot(0).container);
            cir.setReturnValue(true);
            cir.cancel();
        } else if (Chestifier.keyMoveToPlInv.matches(keyEvent)) {
            ExtendedGuiChest.moveMatchingItems(hScreen, true);
            cir.setReturnValue(true);
            cir.cancel();
        } else if (Chestifier.keySearchBox.matches(keyEvent)) {
            ConfigurationHandler.toggleSearchBox();
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Override
    public boolean charTyped(CharacterEvent charEvent) {
        if (isSupportedScreenHandler(menu)
        && ConfigurationHandler.enableSearch()
        && searchWidget != null
        && searchWidget.isActive()) {
            return searchWidget.charTyped(charEvent);
        }
        return super.charTyped(charEvent);
    }

    private boolean loggedScreenHandlerClass = false;

    public boolean isSupportedScreenHandler(AbstractContainerMenu handler) {
        if (handler == null) {
            return false;
        }
        if (handler instanceof ChestMenu || handler instanceof ShulkerBoxMenu) {
            return true;
        }
        if (Chestifier.getHelperForHandler(handler) != null) {
            return true;
        }
        if (!loggedScreenHandlerClass) {
            LoggerFactory.getLogger(this.getClass()).info("opening class {}/{}", handler.getClass().getSimpleName(), handler.getClass().getCanonicalName());
            loggedScreenHandlerClass = true;
        }
        return false;
    }

    public int getSlotRowCount() {
        if (ConfigurationHandler.allowExtraLargeChests()) {
            ChestGuiInfo helper = Chestifier.getHelperForHandler(menu);
            if (helper != null) {
                int rows = helper.getRows(menu);
                if (rows != -1) {
                    return rows;
                }
            }
            return chestSlotCount() / getSlotColumnCount();
        }
        return Math.min(6, chestSlotCount() / PLAYERINVCOLS);
    }

    public int getSlotColumnCount() {
        int size = chestSlotCount();
        if (ConfigurationHandler.allowExtraLargeChests()) {
            ChestGuiInfo helper = Chestifier.getHelperForHandler(menu);
            if (helper != null) {
                int cols = helper.getColumns(menu);
                if (cols != -1) {
                    return cols;
                }
            }
            return (size <= 81 ? PLAYERINVCOLS : size / PLAYERINVCOLS);
        }
        return PLAYERINVCOLS;
    }

    private int chestSlotCount() {
        return menu.slots.size() - PLAYERSLOTS;
    }
}
