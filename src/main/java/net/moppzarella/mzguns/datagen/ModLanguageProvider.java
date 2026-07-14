package net.moppzarella.mzguns.datagen;

import net.minecraft.data.PackOutput;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.item.ModCreativeModeTabs;
import net.moppzarella.mzguns.item.ModItems;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, MZGuns.MODID, locale);
    }
    @Override
    protected void addTranslations() {

        add("creativetab.mzguns.guns", "MZ Guns");
        add(ModItems.BETA_GUN.asItem(), "Beta Gun");
        add(ModItems.PISTOL.asItem(), "Pistol");
        add(ModItems.SHOTGUN.asItem(), "Shotgun");
        add(ModItems.BURST_SMG.asItem(), "Burst SMG");
    }
}
