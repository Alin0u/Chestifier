package dev.alinou.chestifier;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public class MMConfigurationHandler implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("Chestifier Settings"))
                    .setSavingRunnable(ConfigurationHandler::save);

            ConfigEntryBuilder eb = builder.entryBuilder();
            ConfigCategory cat = builder.getOrCreateCategory(Text.literal("General"));

            cat.addEntry(eb.startBooleanToggle(Text.translatable("chestifier.config.largechests"), ConfigurationHandler.allowExtraLargeChests())
                    .setDefaultValue(false)
                    .setTooltip(Text.translatable("chestifier.config.tt.largechests"))
                    .setSaveConsumer(ConfigurationHandler::setAllowExtraLargeChests)
                    .build());

            cat.addEntry(eb.startBooleanToggle(Text.translatable("chestifier.config.halfsize"), ConfigurationHandler.halfSizeButtons())
                    .setDefaultValue(false)
                    .setTooltip(Text.translatable("chestifier.config.tt.halfsize"))
                    .setSaveConsumer(ConfigurationHandler::setHalfSizeButtons)
                    .build());

            cat.addEntry(eb.startBooleanToggle(Text.translatable("chestifier.config.transparent"), ConfigurationHandler.toneDownButtons())
                    .setDefaultValue(true)
                    .setTooltip(Text.translatable("chestifier.config.tt.transparent"))
                    .setSaveConsumer(ConfigurationHandler::setToneDownButtons)
                    .build());

            cat.addEntry(eb.startBooleanToggle(Text.translatable("chestifier.config.enablesearch"), ConfigurationHandler.enableSearch())
                    .setDefaultValue(true)
                    .setTooltip(Text.translatable("chestifier.config.tt.enablesearch"))
                    .setSaveConsumer(ConfigurationHandler::setEnableSearch)
                    .build());

            cat.addEntry(eb.startBooleanToggle(Text.translatable("chestifier.config.enablerowbuttons"), ConfigurationHandler.enableRowButtons())
                    .setDefaultValue(true)
                    .setTooltip(Text.translatable("chestifier.config.tt.enablerowbuttons"))
                    .setSaveConsumer(ConfigurationHandler::setEnableRowButtons)
                    .build());

            cat.addEntry(eb.startBooleanToggle(Text.translatable("chestifier.config.enablecolumnbuttons"), ConfigurationHandler.enableColumnButtons())
                    .setDefaultValue(true)
                    .setTooltip(Text.translatable("chestifier.config.tt.enablecolumnbuttons"))
                    .setSaveConsumer(ConfigurationHandler::setEnableColumnButtons)
                    .build());

            cat.addEntry(eb.startStrField(Text.translatable("chestifier.config.highlight"), ConfigurationHandler.getHighlightColor())
                    .setDefaultValue("4000ff00")
                    .setTooltip(Text.translatable("chestifier.config.tt.highlight"))
                    .setSaveConsumer(ConfigurationHandler::setHighlightColor)
                    .build());

            return builder.build();
        };
    }
}
