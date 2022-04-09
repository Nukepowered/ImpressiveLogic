package info.nukepowered.impressivelogic.common.block;

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

/**
 * @author TheDarkDnKTv
 *
 */
public abstract class BaseWireBlock extends Block {

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
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block updateBlock, BlockPos updatePos, boolean bool) {
		if (!world.isClientSide) {
			if (!state.canSurvive(world, pos)) {
				dropResources(state, world, pos);
				world.removeBlock(pos, false);
			}
		}
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return Block.box(0, 0, 0, 16, 1, 16); // TODO dynamic collision
	}
}
