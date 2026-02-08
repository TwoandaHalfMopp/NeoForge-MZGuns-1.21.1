package net.moppzarella.mzguns.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.moppzarella.mzguns.MZGuns;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MZGuns.MODID);

    public static final Supplier<CreativeModeTab> GUNS_TAB = CREATIVE_MODE_TAB.register("guns_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.BETA_GUN.get()))
                    .title(Component.translatable("creativetab.mzguns.guns"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.BETA_GUN);
                    }).build()
            );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
