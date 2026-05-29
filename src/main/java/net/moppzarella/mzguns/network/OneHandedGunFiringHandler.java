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
import net.moppzarella.mzguns.item.custom.BaseGunItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = MZGuns.MODID)
public class OneHandedGunFiringHandler {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                IsPrimaryFireKeyPressed.IS_PRIMARY_FIRE_KEY_PRESSED_TYPE,
                IsPrimaryFireKeyPressed.IS_PRIMARY_FIRE_KEY_PRESSED_STREAM_CODEC,
                (payload, context) -> {

                    Player player = context.player();
                    Level level = context.player().level();
                    Item gun_that_the_player_is_using = player.getItemInHand(InteractionHand.MAIN_HAND).getItem();
                    if(payload.primaryfirekeydown && gun_that_the_player_is_using instanceof BaseGunItem) {
                        //((GunItem) gun_that_the_player_is_using).onAttackOrUse(level, player, InteractionHand.MAIN_HAND, player.getItemInHand(InteractionHand.MAIN_HAND));
                        ((BaseGunItem) gun_that_the_player_is_using).primaryFire(level, player, InteractionHand.MAIN_HAND);
                    }
                }
        );
        registrar.playToServer(
                IsAlternateFireKeyPressed.IS_ALTERNATE_FIRE_KEY_PRESSED_TYPE,
                IsAlternateFireKeyPressed.IS_ALTERNATE_FIRE_KEY_PRESSED_STREAM_CODEC,
                (payload, context) -> {

                    Player player = context.player();
                    Level level = context.player().level();
                    Item gun_that_the_player_is_using = player.getItemInHand(InteractionHand.OFF_HAND).getItem();
                    if(payload.alternatefirekeydown && gun_that_the_player_is_using instanceof BaseGunItem) {
                        MZGuns.LOGGER.info("alternatefirekeydown");
                        //((GunItem) gun_that_the_player_is_using).onAttackOrUse(level, player, InteractionHand.OFF_HAND, player.getItemInHand(InteractionHand.OFF_HAND));
                    }
                }
        );
    }

    public record IsPrimaryFireKeyPressed(boolean primaryfirekeydown) implements CustomPacketPayload {

        public static final CustomPacketPayload.Type<IsPrimaryFireKeyPressed> IS_PRIMARY_FIRE_KEY_PRESSED_TYPE =
                new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MZGuns.MODID, "isprimaryfirekeypressed"));

        public static final StreamCodec<ByteBuf, IsPrimaryFireKeyPressed> IS_PRIMARY_FIRE_KEY_PRESSED_STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                IsPrimaryFireKeyPressed::primaryfirekeydown,
                IsPrimaryFireKeyPressed::new
        );

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return IS_PRIMARY_FIRE_KEY_PRESSED_TYPE;
        }

    }

    public record IsAlternateFireKeyPressed(boolean alternatefirekeydown) implements CustomPacketPayload {

        public static final CustomPacketPayload.Type<IsAlternateFireKeyPressed> IS_ALTERNATE_FIRE_KEY_PRESSED_TYPE =
                new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MZGuns.MODID, "isalternatefirekeypressed"));

        public static final StreamCodec<ByteBuf, IsAlternateFireKeyPressed> IS_ALTERNATE_FIRE_KEY_PRESSED_STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                IsAlternateFireKeyPressed::alternatefirekeydown,
                IsAlternateFireKeyPressed::new
        );

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return IS_ALTERNATE_FIRE_KEY_PRESSED_TYPE;
        }

    }

}
