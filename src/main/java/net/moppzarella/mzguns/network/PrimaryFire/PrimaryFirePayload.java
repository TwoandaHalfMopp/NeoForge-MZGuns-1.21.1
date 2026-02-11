package net.moppzarella.mzguns.network.PrimaryFire;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.moppzarella.mzguns.MZGuns;

public record PrimaryFirePayload(int id) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PrimaryFirePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MZGuns.MODID, "primary_fire"));

    public static final StreamCodec<ByteBuf, PrimaryFirePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            PrimaryFirePayload::id,
            PrimaryFirePayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
