package info.nukepowered.impressivelogic.common.block;

import info.nukepowered.impressivelogic.api.logic.INetworkCable;
import info.nukepowered.impressivelogic.common.logic.network.LogicNetManager;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
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

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public abstract class BaseWireBlock extends AbstractNetworkBlock implements INetworkCable {

	protected final static Set<Direction> supportedDirections = Set.of(Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH);

	protected final static BooleanProperty EAST = BooleanProperty.create("east");
	protected final static BooleanProperty WEST = BooleanProperty.create("west");
	protected final static BooleanProperty NORTH = BooleanProperty.create("north");
	protected final static BooleanProperty SOUTH = BooleanProperty.create("south");

	public BaseWireBlock(Material material) {
		this(Properties.of(material));
	}

	public BaseWireBlock(Properties props) {
		super(props.noCollission().instabreak());
		this.registerDefaultState(this.registerDefaultBlockState());
	}

	protected BlockState registerDefaultBlockState() {
		return this.stateDefinition.any()
				.setValue(EAST, false)
				.setValue(WEST, false)
				.setValue(NORTH, false)
				.setValue(SOUTH, false);
	}

	@Override
	public Collection<Direction> getConnectableSides(Level level, BlockPos pos) {
		return supportedDirections;
	}

	@Nullable
	@Override
	public Component provideDebugInformation(Level level, BlockPos pos) {
		final var style = Style.EMPTY
				.withColor(ChatFormatting.YELLOW);
		final var component = new TextComponent("=== Network information ===\n")
				.setStyle(style);

		var opt = LogicNetManager.findNetwork(level, pos);
		if (opt.isPresent()) {
			var info = new TextComponent("");
			var entities = opt.get().getEntities();

			info.append(String.format(" size: %d\n", entities.size()));
			info.append(String.format(" entities: %s\n", entities));

			info.withStyle(ChatFormatting.WHITE);
			component.append(info);
		}

		return component.append(new TextComponent("===========================").setStyle(style));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(EAST)
			.add(WEST)
			.add(NORTH)
			.add(SOUTH);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return canSupportCenter(level, pos.below(), Direction.UP);
	}

	@Override
	public void neighborChanged(BlockState thisState, Level world, BlockPos thisPos, Block updateBlock, BlockPos updatePos, boolean bool) {
		super.neighborChanged(thisState, world, thisPos, updateBlock, updatePos, bool);

		if (!world.isClientSide) {
			if (!thisState.canSurvive(world, thisPos)) {
				dropResources(thisState, world, thisPos);
				world.removeBlock(thisPos, false);
			}
		}
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return Block.box(0, 0, 0, 16, 1, 16); // TODO dynamic collision
	}
}
