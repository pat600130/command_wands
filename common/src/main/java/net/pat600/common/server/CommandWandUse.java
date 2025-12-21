package net.pat600.common.server;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import static net.pat600.common.CommandWands.LOG;

public class CommandWandUse{
    public static void register() {
        InteractionEvent.RIGHT_CLICK_ITEM.register((player, world) -> {
            ItemStack stack = player.getMainHandItem();
            CompoundTag tag = stack.getTag();

            if (tag != null && tag.contains("StoredCommand")) {
                String command = tag.getString("StoredCommand");
                //LOG.info("Executing command from wand:{}", command);
                MinecraftServer server = player.getServer();
                if (server != null) {
                    CommandSourceStack CStack = server.createCommandSourceStack()
                    .withPermission(2)
                    .withPosition(player.position())
                    .withRotation(player.getRotationVector())
                    .withEntity(player);
                    try {
                        server.getCommands().getDispatcher().execute(command, CStack);
                    } catch (CommandSyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }

            } return CompoundEventResult.pass();
        });
    }
}