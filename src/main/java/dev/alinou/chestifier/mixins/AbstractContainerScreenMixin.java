package dev.alinou.chestifier.mixins;

import dev.alinou.chestifier.ConfigurationHandler;
import dev.alinou.chestifier.Chestifier;
import dev.alinou.chestifier.ExtendedGuiChest;
import dev.alinou.chestifier.FrozenSlotDatabase;
import dev.alinou.chestifier.InventoryLayout;
import dev.alinou.chestifier.KeyModifiers;
import dev.alinou.chestifier.interfaces.SlotClicker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.alinou.chestifier.storagemodapi.ChestGuiInfo;

@Mixin(HandledScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen implements SlotClicker {

    private static final int PLAYERSLOTS = InventoryLayout.PLAYER_SLOTS;
    private static final int PLAYERINVCOLS = InventoryLayout.PLAYER_INV_COLS;
    private static final int PLAYERINVROWS = InventoryLayout.PLAYER_INV_ROWS;

    private TextFieldWidget searchWidget;

    @Shadow protected abstract void onMouseClick(Slot slot, int invSlot, int button, SlotActionType slotActionType);
    @Shadow protected abstract void drawMouseoverTooltip(DrawContext context, int x, int y);
    @Shadow protected abstract boolean isPointWithinBounds(int x, int y, int w, int h, double pX, double pY);
    @Shadow @Final protected ScreenHandler handler;
    @Shadow protected int x, y, backgroundWidth, backgroundHeight;

    protected AbstractContainerScreenMixin() { super(null); }

    @Override
    public void Chestifier$onMouseClick(Slot slot, int invSlot, int button, SlotActionType slotActionType) {
        this.onMouseClick(slot, invSlot, button, slotActionType);
    }

    @Override
    public int Chestifier$getPlayerInventoryStartIndex() {
        if (handler instanceof PlayerScreenHandler) {
            return PLAYERINVCOLS;
        } else {
            return this.handler.slots.size() - PLAYERSLOTS;
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

    // Draws frozen-slot markers before the vanilla slot/item render pass (render HEAD),
    // so the marker sits behind the item instead of on top of it. drawSlot() isn't a
    // reliable hook for this: it isn't called for the hotbar row on all screens.
    @Inject(method = "render", at = @At("HEAD"))
    public void Chestifier$DrawFrozenSlotMarkers(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!isSupportedScreenHandler(handler) || KeyModifiers.hasShiftDown()) {
            return;
        }
        for (int i = 0; i < PLAYERSLOTS; i++) {
            if (FrozenSlotDatabase.isSlotFrozen(i)) {
                Slot slot = this.handler.slots.get(Chestifier$slotIndexfromPlayerInventoryIndex(i));
                context.drawTexture(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, ExtendedGuiChest.ICONS, x + slot.x, y + slot.y, 7 * 18 + 1, 3 * 18 + 1, 16, 16, 256, 256);
            }
        }
    }

    @Inject(method = "drawSlot", at = @At("RETURN"))
    public void Chestifier$DrawSlotIndex(DrawContext context, Slot slot, CallbackInfo ci) {
        if (KeyModifiers.hasAltDown()) {
            context.drawText(this.textRenderer, Integer.toString(slot.id), slot.x, slot.y, 0x808090, false);
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void Chestifier$renderSpecialButtons(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        HandledScreen<?> hScreen = (HandledScreen<?>) (Object) this;

        ExtendedGuiChest.drawPlayerInventoryBroom(context, hScreen, x + backgroundWidth, y + backgroundHeight - 30 - 3 * 18, mouseX, mouseY);
        if (isSupportedScreenHandler(handler)) {

            int cols = getSlotColumnCount();
            int rows = getSlotRowCount();

            if (ConfigurationHandler.enableColumnButtons()) {
                int startx = (x + backgroundWidth / 2) - (18 / 2) * cols;
                for (int i = 0; i < cols; i++) {
                    ExtendedGuiChest.drawTexturedModalRectWithMouseHighlight(hScreen, context, startx + i * 18, y + -18,           1 * 18, 2 * 18, 18, 18, mouseX, mouseY);
                }
                startx = (x + backgroundWidth / 2) - 9 * PLAYERINVCOLS;
                for (int i = 0; i < PLAYERINVCOLS; i++) {
                    ExtendedGuiChest.drawTexturedModalRectWithMouseHighlight(hScreen, context, startx + i * 18, y + 40 + (rows + 4) * 18, 9 * 18, 2 * 18, 18, 18, mouseX, mouseY);
                }
            }

            if (ConfigurationHandler.enableRowButtons()) {
                for (int i = 0; i < rows; i++) {
                    ExtendedGuiChest.drawTexturedModalRectWithMouseHighlight(hScreen, context, x + -18, y + 17 + i * 18,        1 * 18, 2 * 18, 18, 18, mouseX, mouseY);
                }
                for (int i = 0; i < PLAYERINVROWS; i++) {
                    ExtendedGuiChest.drawTexturedModalRectWithMouseHighlight(hScreen, context, x + -18, y + 28 + (i + rows) * 18, 9 * 18, 2 * 18, 18, 18, mouseX, mouseY);
                }
            }

            if (ConfigurationHandler.enableSearch()) {
                if (searchWidget == null) {
                    searchWidget = new TextFieldWidget(textRenderer, x + backgroundWidth - 85, y + 3, 80, 12, Text.literal("Search"));
                } else {
                    searchWidget.setX(x + backgroundWidth - 85);
                    searchWidget.setY(y + 3);
                }
                searchWidget.render(context, mouseX, mouseY, delta);

                String search = searchWidget.getText().toLowerCase();
                if (!search.isEmpty()) {
                    int highlight = (int) Long.parseLong(ConfigurationHandler.getHighlightColor().toUpperCase(), 16);
                    for (int i = 0; i < this.handler.slots.size(); i++) {
                        Slot slot = this.handler.slots.get(i);
                        Item item = slot.getStack().getItem();
                        if (item == Items.AIR) {
                            continue;
                        }
                        if (I18n.translate(item.getTranslationKey()).toLowerCase().contains(search)) {
                            context.fill(x + slot.x - 1, y + slot.y - 1, x + slot.x + 18 - 1, y + slot.y + 18 - 1, highlight);
                        }
                    }
                }
            }
            ExtendedGuiChest.drawPlayerInventoryAllUp(context, hScreen, x + backgroundWidth, y + backgroundHeight - 30 - 2 * 18, mouseX, mouseY);
            ExtendedGuiChest.drawChestInventoryBroom(context, hScreen, x + backgroundWidth, y + 17, mouseX, mouseY);
            ExtendedGuiChest.drawChestInventoryAllDown(context, hScreen, x + this.backgroundWidth, y + 17 + 18, mouseX, mouseY);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void Chestifier$checkMyButtons(Click click, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        double mouseX = click.x();
        double mouseY = click.y();
        int mouseButton = click.button();

        if (isSupportedScreenHandler(handler)
        && ConfigurationHandler.enableSearch()
        && searchWidget != null
        && searchWidget.mouseClicked(click, bl)) {
            searchWidget.setFocused(true);
            cir.setReturnValue(true);
            cir.cancel();
            return;
        } else if (searchWidget != null) {
            searchWidget.setFocused(false);
        }

        if (mouseX >= x + backgroundWidth && mouseX <= x + backgroundWidth + 18) {
            HandledScreen<?> hScreen = (HandledScreen<?>) (Screen) this;
            if (mouseY >= y + backgroundHeight - 30 - 3 * 18 && mouseY < y + backgroundHeight - 30 - 2 * 18) {
                ExtendedGuiChest.sortInventory(this, false, MinecraftClient.getInstance().player.getInventory());
                cir.setReturnValue(true);
                cir.cancel();
                return;
            } else if (mouseY >= y + backgroundHeight - 30 - 2 * 18 && mouseY < y + backgroundHeight - 30 - 1 * 18) {
                if (!isSupportedScreenHandler(handler)) return;
                ExtendedGuiChest.moveMatchingItems(hScreen, false);
                cir.setReturnValue(true);
                cir.cancel();
                return;
            } else if (!isSupportedScreenHandler(handler)) {
                return;
            } else if (mouseY > y + 17 && mouseY < y + 17 + 18) {
                if (handler.slots.isEmpty()) return;
                ExtendedGuiChest.sortInventory(this, true, handler.getSlot(0).inventory);
                cir.setReturnValue(true);
                cir.cancel();
                return;
            } else if (mouseY > y + 17 + 18 && mouseY < y + 17 + 36) {
                ExtendedGuiChest.moveMatchingItems(hScreen, true);
                cir.setReturnValue(true);
                cir.cancel();
                return;
            }
        }
        if (!isSupportedScreenHandler(handler)) {
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

        if (ConfigurationHandler.enableRowButtons() && mouseX >= x - 18 && mouseX <= x) {
            int deltay = (int) mouseY - y;
            if (deltay < rows * 18 + 17) {
                clickSlotsInRow((deltay - 17) / 18);
                return true;
            } else if (deltay < (rows + PLAYERINVROWS) * 18 + 28) {
                clickSlotsInRow((deltay - 28) / 18);
                return true;
            }
        }
        if (ConfigurationHandler.enableColumnButtons() && mouseX > x + 7 && mouseX < x + backgroundWidth) {
            boolean isChest;
            int column;
            if (mouseY > y - 18 && mouseY < y) {
                int startx = x + backgroundWidth / 2 - (18 / 2) * cols;
                isChest = true;
                column = ((int) mouseX - startx) / 18;
                if (column < 0 || column >= cols) {
                    return false;
                }
            } else if (mouseY > y + 40 + (rows + PLAYERINVROWS) * 18 && mouseY < y + 40 + (rows + PLAYERINVROWS) * 18 + 18) {
                int startx = x + backgroundWidth / 2 - (18 / 2) * PLAYERINVCOLS;
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
        for (int i = 0; i < this.handler.slots.size(); ++i) {
            int invIndex = this.Chestifier$playerInventoryIndexFromSlotIndex(i);
            if (invIndex == -1)
                continue;
            Slot slot = this.handler.slots.get(i);
            if (isPointWithinBounds(slot.x, slot.y, 16, 16, mouseX, mouseY)) {
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
                slotClick(slot, 0, SlotActionType.QUICK_MOVE);
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
                slotClick(slot, 0, SlotActionType.QUICK_MOVE);
        }
    }

    private void slotClick(int slot, int mouseButton, SlotActionType clickType) {
        ((SlotClicker) this).Chestifier$onMouseClick(null, slot, mouseButton, clickType);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void Chestifier$keyPressed(KeyInput keyInput, CallbackInfoReturnable<Boolean> cir) {
        HandledScreen<?> hScreen = (HandledScreen<?>) (Screen) this;
        int keyCode = keyInput.key();
        int scanCode = keyInput.scancode();

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            return;
        }

        if (isSupportedScreenHandler(handler)
        && ConfigurationHandler.enableSearch()
        && searchWidget != null
        && searchWidget.isActive()) {
            boolean value = searchWidget.keyPressed(keyInput);
            cir.setReturnValue(value);
            cir.cancel();
            return;
        }

        if (Chestifier.keySortPlInv.matchesKey(keyInput)) {
            ExtendedGuiChest.sortInventory(this, false, MinecraftClient.getInstance().player.getInventory());
            cir.setReturnValue(true);
            cir.cancel();
        } else if (!isSupportedScreenHandler(handler)) {
            return;
        } else if (Chestifier.keyMoveToChest.matchesKey(keyInput)) {
            ExtendedGuiChest.moveMatchingItems(hScreen, false);
            cir.setReturnValue(true);
            cir.cancel();
        } else if (Chestifier.keySortChest.matchesKey(keyInput)) {
            if (handler.slots.isEmpty()) return;
            ExtendedGuiChest.sortInventory(this, true, handler.getSlot(0).inventory);
            cir.setReturnValue(true);
            cir.cancel();
        } else if (Chestifier.keyMoveToPlInv.matchesKey(keyInput)) {
            ExtendedGuiChest.moveMatchingItems(hScreen, true);
            cir.setReturnValue(true);
            cir.cancel();
        } else if (Chestifier.keySearchBox.matchesKey(keyInput)) {
            ConfigurationHandler.toggleSearchBox();
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Override
    public boolean charTyped(CharInput charInput) {
        if (isSupportedScreenHandler(handler)
        && ConfigurationHandler.enableSearch()
        && searchWidget != null
        && searchWidget.isActive()) {
            return searchWidget.charTyped(charInput);
        }
        return super.charTyped(charInput);
    }

    private boolean loggedScreenHandlerClass = false;

    public boolean isSupportedScreenHandler(ScreenHandler handler) {
        if (handler == null) {
            return false;
        }
        if (handler instanceof GenericContainerScreenHandler || handler instanceof ShulkerBoxScreenHandler) {
            return true;
        }
        if (Chestifier.getHelperForHandler(handler) != null) {
            return true;
        }
        if (!loggedScreenHandlerClass && !handler.getClass().getSimpleName().startsWith("class_")) {
            LoggerFactory.getLogger(this.getClass()).info("opening class {}/{}", handler.getClass().getSimpleName(), handler.getClass().getCanonicalName());
            loggedScreenHandlerClass = true;
        }
        return false;
    }

    public int getSlotRowCount() {
        if (ConfigurationHandler.allowExtraLargeChests()) {
            ChestGuiInfo helper = Chestifier.getHelperForHandler(handler);
            if (helper != null) {
                int rows = helper.getRows(handler);
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
            ChestGuiInfo helper = Chestifier.getHelperForHandler(handler);
            if (helper != null) {
                int cols = helper.getColumns(handler);
                if (cols != -1) {
                    return cols;
                }
            }
            return (size <= 81 ? PLAYERINVCOLS : size / PLAYERINVCOLS);
        }
        return PLAYERINVCOLS;
    }

    private int chestSlotCount() {
        return handler.slots.size() - PLAYERSLOTS;
    }
}
