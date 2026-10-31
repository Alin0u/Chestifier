package dev.alinou.chestifier;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class MMConfigurationHandler implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (!FabricLoader.getInstance().isModLoaded("cloth-config")) {
            return ModMenuApi.super.getModConfigScreenFactory();
        }
        return parent -> ClothConfigScreen.create(parent);
    }
}
