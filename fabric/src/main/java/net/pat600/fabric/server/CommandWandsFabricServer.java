package net.pat600.fabric.server;

import net. fabricmc. api. DedicatedServerModInitializer;
import net.pat600.common.CommandWands;

public class CommandWandsFabricServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        CommandWands.serverInit();
    }
}
