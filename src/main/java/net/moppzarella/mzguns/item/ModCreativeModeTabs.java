package net.moppzarella.mzguns.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.moppzarella.mzguns.MZGuns;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MZGuns.MODID);

    public static final Supplier<CreativeModeTab> GUNS_TAB = CREATIVE_MODE_TAB.register("guns_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(Items.GUNPOWDER.asItem()))
                    .title(Component.translatable("creativetab.mzguns.guns"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(ModItems.PISTOL);
                        output.accept(ModItems.SHOTGUN);
                        output.accept(ModItems.BURST_SMG);

                    }).build()
            );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
