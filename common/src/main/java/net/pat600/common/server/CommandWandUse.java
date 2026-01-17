package net.pat600.common.server;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import static net.pat600.common.CommandWands.LOG;

public class CommandWandUse{
    public static void registerClick() {
        InteractionEvent.RIGHT_CLICK_ITEM.register((player, world) -> {
            ItemStack stack = player.getMainHandItem();
            CompoundTag tag = stack.getTag();

            if (!(tag != null && tag.contains("StoredCommand")))
                return CompoundEventResult.pass();

            String command = tag.getString("StoredCommand");
            Integer cooldown = tag.getInt("CommandWandCooldown");
            Integer cooldownV = tag.getInt("CommandWandCooldownValue");
            MinecraftServer server = player.getServer();
            if (!(server != null))
                return CompoundEventResult.pass();
            if (cooldownV > 0){
                //wLOG.info("command wand on cooldown for player {} for item {}", player.getName().getString(), stack.getItem());
                player.sendSystemMessage(Component.literal("Command Wand is on cooldown for " + cooldownV + " ticks."));
                return CompoundEventResult.pass();
            }
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

            if (!(cooldown > 0 && player instanceof ServerPlayer serverPlayer && !serverPlayer.isCreative()))
                return CompoundEventResult.pass();
            tag.putInt("CommandWandCooldownValue", cooldown);
            stack.setTag(tag);
                //serverPlayer.getCooldowns().addCooldown(stack.getItem(), cooldown);
                //LOG.info("applied cooldown {} ticks to player {} for item {}", cooldown, serverPlayer.getName().getString(), stack.getItem());
            return CompoundEventResult.pass();
        });
    }
    public static void registerOnTick() {
        TickEvent.SERVER_POST.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                for (ItemStack stack : player.getInventory().items) {
                    CompoundTag tag = stack.getTag();
                    if (tag != null && tag.contains("CommandWandCooldownValue")) {
                        int cooldownV = tag.getInt("CommandWandCooldownValue");
                        if (cooldownV > 0) {
                            cooldownV--;
                            tag.putInt("CommandWandCooldownValue", cooldownV);
                            stack.setTag(tag);
                        }
                    }
                }
            }
        });
    }
}