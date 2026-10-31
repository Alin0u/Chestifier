package dev.alinou.chestifier;

import java.util.HashMap;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import dev.alinou.chestifier.storagemodapi.ChestGuiInfo;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value = Chestifier.MODID, dist = Dist.CLIENT)
public class Chestifier {

    static final String MODID = "chestifier";
    static final String MODNAME = "Chestifier";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODNAME);

    public static KeyMapping keySortChest, keyMoveToChest,
                             keySortPlInv, keyMoveToPlInv,
                             keySearchBox;

    private static final HashMap<String, ChestGuiInfo> modHelpers = new HashMap<>();

    public Chestifier(IEventBus modBus, ModContainer container) {
        ConfigurationHandler.load();
        FrozenSlotDatabase.init(FMLPaths.CONFIGDIR.get().toFile());

        modBus.addListener(Chestifier::registerKeys);
        if (ModList.get().isLoaded("cloth_config")) {
            container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> ClothConfigScreen.create(parent));
        }
    }

    private static void registerKeys(RegisterKeyMappingsEvent event) {
        Category category = new Category(Identifier.fromNamespaceAndPath(MODID, "key.categories.chestifier"));
        event.registerCategory(category);
        keySortChest  = registerKey(event, "sortchest",  InputConstants.KEY_NUMPAD7, category);
        keyMoveToChest= registerKey(event, "matchup",    InputConstants.KEY_NUMPAD8, category);
        keySortPlInv  = registerKey(event, "sortplayer", InputConstants.KEY_NUMPAD1, category);
        keyMoveToPlInv= registerKey(event, "matchdown",  InputConstants.KEY_NUMPAD2, category);
        keySearchBox  = registerKey(event, "searchbox",  0, category);
    }

    public static void registerMod(String screenHandlerClassName, ChestGuiInfo helper) {
        modHelpers.put(screenHandlerClassName, helper);
    }

    public static void registerMod(String modName, String screenHandlerClassName, String helperClassName) {
        try {
            Class.forName(screenHandlerClassName);
            ChestGuiInfo helper = (ChestGuiInfo) Class.forName(helperClassName).getDeclaredConstructor().newInstance();
            registerMod(screenHandlerClassName, helper);
            LOGGER.info("Chestifier enabling support for {}", modName);
        } catch (ReflectiveOperationException ex) {
            LOGGER.info("Chestifier did not find mod {}, not enabling support", modName);
        } catch (Exception ex) {
            LOGGER.warn("Chestifier failed to enable support for {}", modName, ex);
        }
    }

    public static ChestGuiInfo getHelperForHandler(AbstractContainerMenu handler) {
        return modHelpers.get(handler.getClass().getCanonicalName());
    }

    private static KeyMapping registerKey(RegisterKeyMappingsEvent event, String key, int code, Category category) {
        KeyMapping binding = new KeyMapping("key.chestifier." + key, InputConstants.Type.KEYBOARD, code, category);
        event.register(binding);
        return binding;
    }
}
