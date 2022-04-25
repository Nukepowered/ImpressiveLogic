package info.nukepowered.impressivelogic.common.block;

import info.nukepowered.impressivelogic.api.logic.INetworkPart;
import info.nukepowered.impressivelogic.common.logic.network.LogicNetManager;
import info.nukepowered.impressivelogic.common.logic.network.Network;

import info.nukepowered.impressivelogic.common.logic.network.Network.Entity;
import info.nukepowered.impressivelogic.common.util.ComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public abstract class AbstractNetworkBlock extends Block implements INetworkPart {

    public AbstractNetworkBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.registerDefaultBlockState());
    }

    protected abstract BlockState registerDefaultBlockState();

    protected abstract void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder);

    @Override
    public void provideNetworkDebug(List<Component> components, Network network, Entity entity) {
        INetworkPart.super.provideNetworkDebug(components, network, entity);
        var connections = entity.getConnections().stream()
            .map(Direction::getName)
            .collect(Collectors.joining(", "));

        components.add(new TextComponent(" Network Entity").withStyle(ChatFormatting.YELLOW));

        components.add(ComponentUtils.property("Type", entity.getType().name(), 2));
        components.add(ComponentUtils.property("Connected", connections, 2));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState previousState, boolean bool) {
        if (!level.isClientSide && this.checkStateChanged(previousState, state)) {
            var networksJoined = new HashSet<Network>();
            var part = this.getPart(level, pos);

            for (var dir : part.getConnectableSides(level, pos)) {
                var opt = LogicNetManager.findNetwork(level, pos.relative(dir));
                if (opt.isPresent()) {
                    var network = opt.get();
                    if (networksJoined.contains(network)) {
                        continue;
                    }

                    if (LogicNetManager.joinNetwork(level, network, pos, dir, part)) {
                        networksJoined.add(network);
                    }
                }
            }

            // Register new network if no connections found
            if (networksJoined.isEmpty()) {
                LogicNetManager.registerNewNetwork(level, pos, part);
            } else {
                LogicNetManager.mergeNetworks(level, networksJoined);
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean bool) {
        super.onRemove(state, level, pos, newState, bool);
        if (!level.isClientSide && this.checkStateChanged(state, newState)) {
            LogicNetManager.removeFromNetwork(level, pos);
        }
    }

    @Override
    public void neighborChanged(BlockState thisState, Level world, BlockPos thisPos, Block updateBlock, BlockPos updatePos, boolean bool) {
        super.neighborChanged(thisState, world, thisPos, updateBlock, updatePos, bool);

        var from = Direction.fromNormal(updatePos.subtract(thisPos));
        if (acceptConnection(world, thisPos, from) && updateBlock instanceof INetworkPart) {
            if (world.getBlockState(updatePos).getBlock() != updateBlock) {
                LogicNetManager.validateNetwork(world, thisPos, from);
            }
        }
    }

    private boolean checkStateChanged(BlockState old, BlockState current) {
        var state = old.getBlock() != current.getBlock();
        return state;
    }
}
