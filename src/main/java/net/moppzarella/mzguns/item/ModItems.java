package net.moppzarella.mzguns.item;

import net.minecraft.world.item.Item;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.item.custom.RevolverItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MZGuns.MODID);

    public static final DeferredItem<Item> REVOLVER = ITEMS.register("revolver",
            () -> new RevolverItem(6, 0, 12, 23, 6, 0.25D, 0.04D, 0.7D, new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
