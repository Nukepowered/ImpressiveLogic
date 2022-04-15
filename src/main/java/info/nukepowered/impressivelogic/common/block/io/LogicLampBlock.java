package info.nukepowered.impressivelogic.common.block.io;

import info.nukepowered.impressivelogic.api.logic.INetworkIO;
import info.nukepowered.impressivelogic.common.block.AbstractNetworkBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;

import java.util.Collection;
import java.util.Set;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class LogicLampBlock extends AbstractNetworkBlock implements INetworkIO<Boolean> {

    protected static final BooleanProperty LIT = BooleanProperty.create("lit");
    protected static final Set<Direction> CONNECTABLE = Set.of(Direction.values());

    public LogicLampBlock() {
        super(Properties.of(Material.BUILDABLE_GLASS)
                .lightLevel(state -> state.getValue(LIT) ? 15 : 0)
                .sound(SoundType.GLASS)
                .strength(0.5F)
        );
    }

    @Override
    public Boolean getState(Level level, BlockPos pos) {
        return level.getBlockState(pos).getValue(LIT);
    }

    @Override
    public void setState(Level level, BlockPos pos, Boolean state) {
        level.setBlockAndUpdate(pos, this.stateDefinition.any().setValue(LIT, state));
    }

    @Override
    public void neighborChanged(BlockState thisState, Level world, BlockPos thisPos, Block updateBlock, BlockPos updatePos, boolean bool) {
        super.neighborChanged(thisState, world, thisPos, updateBlock, updatePos, bool);
        if (!world.isClientSide && thisState.getValue(LIT)) {
            thisState = thisState.setValue(LIT, false);
            world.setBlock(thisPos, thisState, 2);
        }
    }

    @Override
    protected BlockState registerDefaultBlockState() {
        return this.stateDefinition.any()
                .setValue(LIT, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public Collection<Direction> getConnectableSides(Level level, BlockPos pos) {
        return CONNECTABLE;
    }

    @Override
    public PartType getType() {
        return PartType.IO;
    }
}