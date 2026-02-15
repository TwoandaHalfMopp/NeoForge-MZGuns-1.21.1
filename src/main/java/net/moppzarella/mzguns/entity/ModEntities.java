package net.moppzarella.mzguns.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.entity.custom.HitscanPelletEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MZGuns.MODID);

    public static final Supplier<EntityType<HitscanPelletEntity>> HITSCAN_PELLET =
            ENTITY_TYPES.register("hitscan_pellet", () -> EntityType.Builder.<HitscanPelletEntity>of(HitscanPelletEntity::new, MobCategory.MISC)
                    .sized(0.5f,0.5f).build("hitscan_pellet")
            );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}