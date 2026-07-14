package net.moppzarella.mzguns.item;

import net.minecraft.world.item.Item;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.item.custom.BaseGunItem;
import net.moppzarella.mzguns.item.custom.BurstSMGItem;
import net.moppzarella.mzguns.item.custom.PistolItem;
import net.moppzarella.mzguns.item.custom.ShotgunItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MZGuns.MODID);

    public static final int universalDeployTime = 10;

    public static final DeferredItem<BaseGunItem> BETA_GUN = ITEMS.register("beta_gun",
            () -> new BaseGunItem(4.5F,6,10,30, universalDeployTime,6, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<PistolItem> PISTOL = ITEMS.register("pistol",
            () -> new PistolItem(1.7F,12,4,20,universalDeployTime,12,25,25,1.15F, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<ShotgunItem> SHOTGUN = ITEMS.register("shotgun",
            () -> new ShotgunItem(0.75F,6,12,20,10,1,universalDeployTime, 1, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BurstSMGItem> BURST_SMG = ITEMS.register("burst_smg",
            () -> new BurstSMGItem(1.7F, 18, 20, 22,universalDeployTime, 18, 25, 9,0.7F,4,3, new Item.Properties().stacksTo(1)));
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
