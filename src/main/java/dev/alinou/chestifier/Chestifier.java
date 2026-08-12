package dev.alinou.chestifier;

import java.io.File;
import java.util.HashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.KeyBinding.Category;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import dev.alinou.chestifier.storagemodapi.ChestGuiInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chestifier implements ClientModInitializer {

    static final String MODID = "chestifier";
    static final String MODNAME = "Chestifier";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODNAME);

    public static KeyBinding keySortChest, keyMoveToChest,
                             keySortPlInv, keyMoveToPlInv,
                             keySearchBox;

    private static final HashMap<String, ChestGuiInfo> modHelpers = new HashMap<>();

    @Override
    public void onInitializeClient() {
        ConfigurationHandler.load();
        FrozenSlotDatabase.init(FabricLoader.getInstance().getConfigDir().toFile());

        Category category = Category.create(Identifier.of(MODID, "key.categories.chestifier"));
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
        } catch (Exception ex) {
            LOGGER.info("Chestifier did not find mod {}, not enabling support", modName);
        }
    }

    public static ChestGuiInfo getHelperForHandler(ScreenHandler handler) {
        return modHelpers.get(handler.getClass().getCanonicalName());
    }

    private KeyBinding registerKey(String key, int code, Category category) {
        KeyBinding binding = new KeyBinding("key.chestifier." + key, net.minecraft.client.util.InputUtil.Type.KEYSYM, code, category);
        KeyBindingHelper.registerKeyBinding(binding);
        return binding;
    }
}
