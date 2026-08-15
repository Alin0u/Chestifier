package dev.alinou.chestifier.storagemodapi;

import net.minecraft.world.inventory.AbstractContainerMenu;

public interface ChestGuiInfo {
    int getRows(AbstractContainerMenu handler);
    int getColumns(AbstractContainerMenu handler);
}
