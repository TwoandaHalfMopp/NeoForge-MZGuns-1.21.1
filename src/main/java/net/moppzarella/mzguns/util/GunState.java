package net.moppzarella.mzguns.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.FastColor;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.function.IntFunction;

public enum GunState implements StringRepresentable {
    HOLSTERED(0, "holstered"),
    DEPLOYING(1, "deploying"),
    ACTIVE_IDLE(2,"active_idle"),
    ACTIVE_FIRING(3,"active_firing"),
    ACTIVE_RELOAD(4,"active_reload"),
    ACTIVE_RELOAD_CONSECUTIVE(5,"active_reload_consecutive");

    private static final IntFunction<GunState> BY_ID = ByIdMap.continuous(GunState::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StringRepresentable.EnumCodec<GunState> CODEC = StringRepresentable.fromEnum(GunState::values);
    public static final StreamCodec<ByteBuf, GunState> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, GunState::getId);


    private final int id;
    private final String name;
    private GunState(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public static GunState byId(int stateId) {
        return (GunState) BY_ID.apply(stateId);
    }

    @Nullable
    @Contract("_,!null->!null;_,null->_")
    public static GunState byName(String translationKey, @Nullable GunState fallback) {
        GunState gunstate = (GunState) CODEC.byName(translationKey);
        return gunstate != null ? gunstate : fallback;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
