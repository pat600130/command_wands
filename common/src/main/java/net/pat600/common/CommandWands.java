package net.pat600.common;

import net.pat600.common.server.CommandWandUse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommandWands {
    public static boolean ServerRunning = false;
    public static final String MOD_ID = "command_wand";

    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
    }
    public static void serverInit() {
        LOG.info("Initializing Command Wands server");
        CommandWandUse.registerClick();
        CommandWandUse.registerOnTick();
        ServerRunning = true;
    }

}
