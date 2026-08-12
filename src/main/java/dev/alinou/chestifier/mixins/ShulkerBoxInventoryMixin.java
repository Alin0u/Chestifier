package dev.alinou.chestifier.mixins;

import dev.alinou.chestifier.InventoryExporter;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ShulkerBoxScreenHandler.class)
public class ShulkerBoxInventoryMixin implements InventoryExporter {

    @Shadow @Final private Inventory inventory;

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
