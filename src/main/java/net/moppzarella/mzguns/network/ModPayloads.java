package net.moppzarella.mzguns.network;

import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.network.PrimaryFire.PrimaryFirePayload;
import net.moppzarella.mzguns.network.PrimaryFire.PrimaryFirePayloadHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = MZGuns.MODID)
public class ModPayloads {

    @SubscribeEvent // on the mod event bus
    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                PrimaryFirePayload.TYPE,
                PrimaryFirePayload.STREAM_CODEC,
                PrimaryFirePayloadHandler::handleOnMain
        );

    }

}
