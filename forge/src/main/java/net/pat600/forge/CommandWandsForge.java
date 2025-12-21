package net.pat600.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.pat600.common.CommandWands;
import net.pat600.common.server.WandCommand;

@Mod(CommandWands.MOD_ID)
public final class CommandWandsForge {
    public CommandWandsForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(CommandWands.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        CommandWands.init();
    }
}

@Mod.EventBusSubscriber(
        modid = CommandWands.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
class ForgeCommandEvents {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        WandCommand.register(
                event.getDispatcher(),
                event.getBuildContext()
        );
    }
}

