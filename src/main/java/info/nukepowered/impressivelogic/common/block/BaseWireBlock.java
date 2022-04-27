package info.nukepowered.impressivelogic.common.block;

import info.nukepowered.impressivelogic.api.logic.INetworkCable;
import info.nukepowered.impressivelogic.common.logic.network.LogicNetManager;
import info.nukepowered.impressivelogic.common.logic.network.Network;
import info.nukepowered.impressivelogic.common.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static info.nukepowered.impressivelogic.ImpressiveLogic.COMMON_MARKER;
import static info.nukepowered.impressivelogic.ImpressiveLogic.LOGGER;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public abstract class BaseWireBlock extends AbstractNetworkBlock implements INetworkCable {

    protected final static Set<Direction> supportedDirections = Set.of(Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH);
    protected final static Map<Direction, BooleanProperty> DIRECTION_STATES = supportedDirections.stream()
        .collect(Collectors.toUnmodifiableMap(Function.identity(), dir -> BooleanProperty.create(dir.getName())));

    public BaseWireBlock(Material material) {
        this(Properties.of(material));
    }

    public BaseWireBlock(Properties props) {
        super(props.noCollission().instabreak());
    }

    @Override
    protected BlockState registerDefaultBlockState() {
        var stateDef = this.stateDefinition.any();
        for (var state : DIRECTION_STATES.values()) {
            stateDef = stateDef.setValue(state, false);
        }

        return stateDef;
    }

    @Override
    public Collection<Direction> getConnectableSides(Level level, BlockPos pos) {
        return supportedDirections;
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        DIRECTION_STATES.values().forEach(builder::add);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState previousState, boolean bool) {
        super.onPlace(state, level, pos, previousState, bool);
        if (!level.isClientSide && state.getBlock() != previousState.getBlock()) {
            var network = LogicNetManager.findNetwork(level, pos);
            if (network.isPresent()) {
                this.updateConnectionState(level, network.get(), pos, state);
            }
        }
    }

    @Override
    public void neighborChanged(BlockState thisState, Level world, BlockPos thisPos, Block updateBlock, BlockPos updatePos, boolean bool) {
        super.neighborChanged(thisState, world, thisPos, updateBlock, updatePos, bool);

        if (!world.isClientSide) {
            if (!thisState.canSurvive(world, thisPos)) {
                dropResources(thisState, world, thisPos);
                world.removeBlock(thisPos, false);
                return;
            }

            var network = LogicNetManager.findNetwork(world, thisPos);
            if (!network.isPresent()) {
                LOGGER.error(COMMON_MARKER, thisPos + " exists, but no network have been found due update");
                this.onPlace(thisState, world, thisPos, thisState, false);
                network = LogicNetManager.findNetwork(world, thisPos);
            }

            this.updateConnectionState(world, network.get(), thisPos, thisState);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return Block.box(0, 0, 0, 16, 1, 16); // TODO dynamic collision
    }

    protected void updateConnectionState(Level level, Network network, BlockPos pos, BlockState state) {
        var connectableSides = getConnectableSides(level, pos);
        var connectedSides = NetworkUtils.getConnectedDirections(network, pos, connectableSides);
        var newState = state;

        for (var entry : DIRECTION_STATES.entrySet()) {
            newState = newState.setValue(entry.getValue(), connectedSides.contains(entry.getKey()));
        }

        if (state != newState) {
            level.setBlockAndUpdate(pos, newState);
        }
    }
}