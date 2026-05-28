package net.moppzarella.mzguns.item;

import net.minecraft.world.item.Item;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.item.custom.BetaShotgunItem;
import net.moppzarella.mzguns.item.custom.GunItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MZGuns.MODID);

    public static final DeferredItem<Item> BETA_GUN = ITEMS.register("beta_gun",
            () -> new GunItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> BETA_SHOTGUN = ITEMS.register("beta_shotgun",
            () -> new BetaShotgunItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
