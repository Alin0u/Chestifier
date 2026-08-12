package dev.alinou.chestifier.interfaces;

import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public interface SlotClicker {
    void Chestifier$onMouseClick(Slot slot, int invSlot, int button, SlotActionType slotActionType);
    int  Chestifier$getPlayerInventoryStartIndex();
    int  Chestifier$playerInventoryIndexFromSlotIndex(int slot);
    int  Chestifier$slotIndexfromPlayerInventoryIndex(int slot);
}
