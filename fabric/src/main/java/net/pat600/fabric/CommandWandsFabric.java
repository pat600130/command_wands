package net.pat600.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.pat600.common.*;
import net.pat600.common.server.WandCommand;


public final class CommandWandsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandWands.init();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            if (!server.isDedicatedServer() && !CommandWands.ServerRunning) {
                CommandWands.serverInit();
            }
        });
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        WandCommand.register(dispatcher,registryAccess)
        );
    }
}
