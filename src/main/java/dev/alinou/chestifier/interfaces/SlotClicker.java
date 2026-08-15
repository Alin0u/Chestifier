package dev.alinou.chestifier.interfaces;

import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;

public interface SlotClicker {
    void Chestifier$onMouseClick(Slot slot, int invSlot, int button, ContainerInput slotActionType);
    int  Chestifier$getPlayerInventoryStartIndex();
    int  Chestifier$playerInventoryIndexFromSlotIndex(int slot);
    int  Chestifier$slotIndexfromPlayerInventoryIndex(int slot);
}
