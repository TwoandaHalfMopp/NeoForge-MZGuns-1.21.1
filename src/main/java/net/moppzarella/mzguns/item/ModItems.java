package net.moppzarella.mzguns.item;

import net.minecraft.world.item.Item;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.item.custom.BaseGunItem;
import net.moppzarella.mzguns.item.custom.PistolItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MZGuns.MODID);

    public static final int universalDeployTime = 10;

    public static final DeferredItem<BaseGunItem> BETA_GUN = ITEMS.register("beta_gun",
            () -> new BaseGunItem(4.5F,6,10,30, universalDeployTime, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<PistolItem> PISTOL = ITEMS.register("pistol",
            () -> new PistolItem(1.7F,12,4,20,universalDeployTime,25,25,1.15F, new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
