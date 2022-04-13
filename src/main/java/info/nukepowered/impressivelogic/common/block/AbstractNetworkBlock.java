package info.nukepowered.impressivelogic.common.block;

import info.nukepowered.impressivelogic.api.logic.INetworkPart;
import info.nukepowered.impressivelogic.common.logic.network.LogicNetManager;
import info.nukepowered.impressivelogic.common.logic.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public abstract class AbstractNetworkBlock extends Block implements INetworkPart {

    public AbstractNetworkBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState previousState, boolean bool) {
        if (!level.isClientSide) {
            var networksJoined = new HashSet<Network>();

            for (var dir : getConnectableSides(level, pos)) {
                var opt = LogicNetManager.findNetwork(level, pos.relative(dir));
                if (opt.isPresent()) {
                    var network = opt.get();
                    if (networksJoined.contains(network)) {
                        continue;
                    }

                    if (LogicNetManager.joinNetwork(level, network, pos, dir, this)) {
                        networksJoined.add(network);
                    }
                }
            }

            // Register new network if no connections found
            if (networksJoined.isEmpty()) {
                LogicNetManager.registerNewNetwork(level, pos, this);
            } else {
                LogicNetManager.mergeNetworks(level, networksJoined);
            }
        }
    }

    @Override
    public void destroy(LevelAccessor levelAccessor, BlockPos pos, BlockState state) {
        if (!levelAccessor.isClientSide()) {
            LogicNetManager.removeFromNetwork((Level) levelAccessor, pos);
        }
    }

    @Override
    public void neighborChanged(BlockState thisState, Level world, BlockPos thisPos, Block updateBlock, BlockPos updatePos, boolean bool) {
        super.neighborChanged(thisState, world, thisPos, updateBlock, updatePos, bool);

        var from = Direction.fromNormal(updatePos.subtract(thisPos));
        if (acceptConnection(world, thisPos, from) && updateBlock instanceof INetworkPart) {
            LogicNetManager.validateNetwork(world, thisPos, from);
        }
    }
}