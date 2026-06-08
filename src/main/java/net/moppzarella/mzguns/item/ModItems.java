package net.moppzarella.mzguns.item;

import net.minecraft.world.item.Item;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.item.custom.RevolverItem;
import net.moppzarella.mzguns.item.custom.ShotgunItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MZGuns.MODID);

    public static final DeferredItem<Item> REVOLVER = ITEMS.register("revolver",
            () -> new RevolverItem(6, 4.5F, 12, 23, 6, 0.25D, 0.04D, 0.7D, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> SHOTGUN = ITEMS.register("shotgun",
            () -> new ShotgunItem(6, 0.75F, 12, 20, 10, 1, new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
