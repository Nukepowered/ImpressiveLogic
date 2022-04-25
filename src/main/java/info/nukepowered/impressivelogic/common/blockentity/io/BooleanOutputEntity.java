package info.nukepowered.impressivelogic.common.blockentity.io;

import info.nukepowered.impressivelogic.api.logic.io.INetworkOutput;
import info.nukepowered.impressivelogic.common.blockentity.BaseNetworkEntity;

import info.nukepowered.impressivelogic.common.logic.network.Network;
import info.nukepowered.impressivelogic.common.logic.network.Network.Entity;
import info.nukepowered.impressivelogic.common.util.ComponentUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/*
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class BooleanOutputEntity extends BaseNetworkEntity implements INetworkOutput<Boolean> {

    public static final BooleanProperty ACTIVE_PROPERTY = BooleanProperty.create("active");
    public static final Set<Direction> CONNECTABLE = Set.of(Direction.values());

    private boolean dirty = false;
    // Volatile will make sure changes between threads will be visible
    private volatile boolean active;

    public BooleanOutputEntity(BlockEntityType<? extends BooleanOutputEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state, PartType.IO);
    }

    @Override
    public void provideNetworkDebug(List<Component> components, Network network, Entity part) {
        super.provideNetworkDebug(components, network, part);
        components.add(ComponentUtils.property("Active", this.active, 2));
    }

    @Override
    public void update() {
        if (!this.level.isClientSide && this.dirty) {
            this.level.setBlock(this.worldPosition, getBlockState().setValue(ACTIVE_PROPERTY, active), 2);
            this.dirty = false;
        }
    }

    @Override
    public void setState(Boolean state) {
        if (state != this.active) {
            this.active = state;
            this.dirty = true;
        }
    }

    @Override
    public Collection<Direction> getConnectableSides(Level level, BlockPos pos) {
        return CONNECTABLE;
    }
}
