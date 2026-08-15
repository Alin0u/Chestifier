package dev.alinou.chestifier;

import java.util.HashMap;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.lwjgl.glfw.GLFW;
import dev.alinou.chestifier.storagemodapi.ChestGuiInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chestifier implements ClientModInitializer {

    static final String MODID = "chestifier";
    static final String MODNAME = "Chestifier";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODNAME);

    public static KeyMapping keySortChest, keyMoveToChest,
                             keySortPlInv, keyMoveToPlInv,
                             keySearchBox;

    private static final HashMap<String, ChestGuiInfo> modHelpers = new HashMap<>();

    @Override
    public void onInitializeClient() {
        ConfigurationHandler.load();
        FrozenSlotDatabase.init(FabricLoader.getInstance().getConfigDir().toFile());

        Category category = Category.register(Identifier.fromNamespaceAndPath(MODID, "key.categories.chestifier"));
        keySortChest  = registerKey("sortchest",  GLFW.GLFW_KEY_KP_7, category);
        keyMoveToChest= registerKey("matchup",    GLFW.GLFW_KEY_KP_8, category);
        keySortPlInv  = registerKey("sortplayer", GLFW.GLFW_KEY_KP_1, category);
        keyMoveToPlInv= registerKey("matchdown",  GLFW.GLFW_KEY_KP_2, category);
        keySearchBox  = registerKey("searchbox",  GLFW.GLFW_KEY_UNKNOWN, category);
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

    private KeyMapping registerKey(String key, int code, Category category) {
        KeyMapping binding = new KeyMapping("key.chestifier." + key, InputConstants.Type.KEYSYM, code, category);
        KeyMappingHelper.registerKeyMapping(binding);
        return binding;
    }
}
