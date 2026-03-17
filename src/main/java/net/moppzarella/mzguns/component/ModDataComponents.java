package net.moppzarella.mzguns.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.moppzarella.mzguns.MZGuns;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class ModDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MZGuns.MODID);

   public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CURRENT_FIRING_COOLDOWN = register("current_firing_cooldown",
            builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> CURRENT_BLOOM = register("current_bloom",
            builder -> builder.persistent(Codec.DOUBLE ));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CURRENT_CLIP = register("current_clip",
            builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> RELOAD_PERCENTILE = register("reload_percentile",
            builder -> builder.persistent(Codec.INT));

    private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name,
                                                                                          UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }



}
