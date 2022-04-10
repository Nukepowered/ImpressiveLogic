package info.nukepowered.impressivelogic.common.item;

import info.nukepowered.impressivelogic.api.logic.INetworkPart;
import info.nukepowered.impressivelogic.common.logic.network.LogicNetworkRegistry;
import info.nukepowered.impressivelogic.common.registry.ImpressiveLogicTabs;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class ItemDebug extends Item {

    public ItemDebug() {
        super(
            new Properties()
                .stacksTo(1)
                .tab(ImpressiveLogicTabs.MAIN)
                .rarity(Rarity.UNCOMMON)
        );
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        final var level = context.getLevel();

        if (!level.isClientSide) {
            final var pos = context.getClickedPos();
            var state = level.getBlockState(pos);
            var player = context.getPlayer();
            if (state.getBlock() instanceof INetworkPart part) {
                if (player != null) {
                    var info = part.provideDebugInformation(level, pos);
                    if (info != null) {
                        player.sendMessage(info, Util.NIL_UUID);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }



        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        final var stack = player.getItemInHand(hand);

        if (player.isCrouching() && !level.isClientSide) {
            final var networks = LogicNetworkRegistry.getNetworksForLevel(level.dimension().location());
            final var component = new TextComponent("=== Networks ===\n")
                    .withStyle(ChatFormatting.GOLD);
            component.append(new TextComponent(" amount: " + networks.size() + "\n")
                    .withStyle(ChatFormatting.WHITE));
            if (networks.size() <= 10) {
                component.append(new TextComponent(" networks:\n")
                        .withStyle(ChatFormatting.WHITE));
                for (var net : networks) {
                    component.append(new TextComponent("  " + net + "\n")
                            .withStyle(ChatFormatting.WHITE));
                }
            }

            player.sendMessage(component, Util.NIL_UUID);
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
    }
}
