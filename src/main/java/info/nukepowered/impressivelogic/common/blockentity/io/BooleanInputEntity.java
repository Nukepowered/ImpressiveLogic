package info.nukepowered.impressivelogic.common.blockentity.io;

import info.nukepowered.impressivelogic.api.logic.io.INetworkInput;
import info.nukepowered.impressivelogic.common.blockentity.BaseNetworkEntity;
import info.nukepowered.impressivelogic.common.logic.network.LogicNetManager;
import info.nukepowered.impressivelogic.common.logic.network.Network;
import info.nukepowered.impressivelogic.common.logic.network.Network.Entity;
import info.nukepowered.impressivelogic.common.util.ComponentUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static info.nukepowered.impressivelogic.common.blockentity.io.BooleanOutputEntity.ACTIVE_PROPERTY;

/*
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class BooleanInputEntity extends BaseNetworkEntity implements INetworkInput<Boolean> {

    public static final Set<Direction> CONNECTABLE = Set.of(Direction.WEST, Direction.EAST, Direction.SOUTH, Direction.NORTH);

    public BooleanInputEntity(BlockEntityType<? extends BooleanInputEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state, PartType.IO);
    }

    @Override
    public void provideNetworkDebug(List<Component> components, Network network, Entity<?> part) {
        super.provideNetworkDebug(components, network, part);
        components.add(ComponentUtils.property("Active", this.getState(), 2));
    }

    @Override
    public Collection<Direction> getConnectableSides(Level level, BlockPos pos) {
        return CONNECTABLE;
    }

    @Override
    public InteractionResult onRightClick(Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!this.level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            var state = this.getBlockState().setValue(ACTIVE_PROPERTY, !this.getState());
            level.setBlock(this.worldPosition, state, 2);
            this.getNetwork().markDirty();
            return InteractionResult.SUCCESS;
        }

        return super.onRightClick(player, hand, hitResult);
    }

    @Override
    public Boolean getState() {
        return this.level.getBlockState(this.worldPosition).getValue(ACTIVE_PROPERTY);
    }
}
