package net.pat600.common.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.*;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import static net.pat600.common.CommandWands.LOG;

public class WandCommand {

    private static boolean isCommandValid(String command, CommandSourceStack source, MinecraftServer server) {
        try {
            ParseResults<CommandSourceStack> results = server.getCommands().getDispatcher().parse(command, source);
            return !results.getReader().canRead() || results.getContext().getNodes().isEmpty();
        } catch (Exception e) {  //testing
            return false;
        }
    }

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext context) {

        dispatcher.register(
                literal("commandwand")
                        .requires(src -> src.hasPermission(2)) // OP ONLY
                        .then(argument("item", ItemArgument.item(context))
                                .suggests((ctx, builder) ->
                                        SharedSuggestionProvider.suggestResource(
                                                net.minecraft.core.registries.BuiltInRegistries.ITEM.keySet(),
                                                builder
                                        )
                                )
                                .then(argument("cooldown", IntegerArgumentType.integer(0))
                                        .then(argument("command", StringArgumentType.greedyString())
                                                .executes(ctx -> {
                                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                                    ItemStack stack = ItemArgument.getItem(ctx, "item").createItemStack(1, false);

                                                    String command = ctx.getArgument("command", String.class);
                                                    int cooldown = ctx.getArgument("cooldown", Integer.class);

                                                    CompoundTag tag = stack.getOrCreateTag();
                                                    tag.putString("StoredCommand", command);
                                                    if (cooldown > 0)
                                                        tag.putInt("CommandWandCooldown", cooldown);

                                                    if (!isCommandValid(command, ctx.getSource(), player.getServer())) {
                                                        ctx.getSource().sendFailure(Component.literal("Warning: The command \"" + command + "\" may be invalid."));
                                                        return 1;
                                                    }



                                                    CompoundTag display = tag.getCompound("display");
                                                    ListTag lore = new ListTag();
                                                    lore.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("wand command: " + command))));
                                                    if (cooldown >0)
                                                        lore.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("cooldown: " + cooldown + " ticks"))));
                                                    display.put("Lore", lore);
                                                    tag.put("display", display);

                                                    Component itemComponent = Component.literal( "[" + stack.getHoverName().getString() + "]")
                                                            .withStyle(
                                                                    style -> style.withHoverEvent(
                                                                    new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(stack))
                                                            ));


                                                    ctx.getSource().getServer().getPlayerList().broadcastSystemMessage(
                                                            Component.literal("")
                                                                    .append(Component.literal("["+player.getGameProfile().getName()+" Created wand: ").withStyle(ChatFormatting.GRAY))
                                                                    .append(itemComponent)
                                                                    .append(Component.literal(" with a cooldown of "+ cooldown +" ticks and command a on click of '"+command+"' ]").withStyle(ChatFormatting.GRAY))
                                                            .withStyle(ChatFormatting.ITALIC),
                                                            false
                                                    );

                                                    //LOG.info("player {} created wand with command: {}", player.getName(), command);

                                                    player.getInventory().add(stack);
                                                    return 0;
                                                })
                                        )
                                )
                        )

        );

        LOG.info("loaded wand command");
    }
}


