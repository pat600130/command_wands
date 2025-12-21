package net.pat600.common.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import static net.pat600.common.CommandWands.LOG;

public class WandCommand {

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
                                .then(argument("command", StringArgumentType.greedyString())
                                        .suggests((ctx, builder) -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            CommandDispatcher<CommandSourceStack> dispatcher1 = player.getServer().getCommands().getDispatcher();

                                            return SharedSuggestionProvider.suggest(
                                                    dispatcher1.getRoot().getChildren().stream()
                                                            .map(node -> node.getName())
                                                            .toList(),
                                                    builder
                                            );
                                        })
                                        .executes(ctx -> { // <-- execute is now on the command argument
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            ItemStack stack = ItemArgument.getItem(ctx, "item").createItemStack(1, false);

                                            String command = StringArgumentType.getString(ctx, "command");
                                            CompoundTag tag = stack.getOrCreateTag();
                                            tag.putString("StoredCommand", command);

                                            CompoundTag display = tag.getCompound("display");
                                            ListTag lore = new ListTag();
                                            lore.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("wand command: " + command))));
                                            display.put("Lore", lore);
                                            tag.put("display", display); 

                                            player.getInventory().add(stack);

                                            LOG.info("player {} created wand with command: {}", player.getName(), command);
                                            return 1;
                                        })
                                )
                        )
        );

        LOG.info("loaded wand command");
    }
}
