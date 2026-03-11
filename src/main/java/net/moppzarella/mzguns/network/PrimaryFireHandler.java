package net.moppzarella.mzguns.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.item.custom.GunItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = MZGuns.MODID)
public class PrimaryFireHandler {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                IsPrimaryFireKeyPressed.IS_PRIMARY_FIRE_KEY_PRESSED_TYPE,
                IsPrimaryFireKeyPressed.IS_PRIMARY_FIRE_KEY_PRESSED_STREAM_CODEC,
                new IPayloadHandler<IsPrimaryFireKeyPressed>() {

                    @Override
                    public void handle(IsPrimaryFireKeyPressed payload, IPayloadContext context) {

                        Player player = context.player();
                        Level level = context.player().level();
                        Item gun_that_the_player_is_using = player.getItemInHand(InteractionHand.MAIN_HAND).getItem();
                        if(payload.primaryfirekeydown && gun_that_the_player_is_using instanceof GunItem) {
                            ((GunItem) gun_that_the_player_is_using).primaryFire(level, player, InteractionHand.MAIN_HAND);

                        }

                    }

                }
        );
    }

    public record IsPrimaryFireKeyPressed(boolean primaryfirekeydown, Long timestamp) implements CustomPacketPayload {

        public static final CustomPacketPayload.Type<IsPrimaryFireKeyPressed> IS_PRIMARY_FIRE_KEY_PRESSED_TYPE =
                new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MZGuns.MODID, "isprimaryfirekeypressed"));

        public static final StreamCodec<ByteBuf, IsPrimaryFireKeyPressed> IS_PRIMARY_FIRE_KEY_PRESSED_STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                IsPrimaryFireKeyPressed::primaryfirekeydown,
                ByteBufCodecs.VAR_LONG,
                IsPrimaryFireKeyPressed::timestamp,
                IsPrimaryFireKeyPressed::new
        );

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return IS_PRIMARY_FIRE_KEY_PRESSED_TYPE;
        }

    }

}
