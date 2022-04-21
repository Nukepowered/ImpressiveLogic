package info.nukepowered.impressivelogic.common.logic.network.blockentity;

import info.nukepowered.impressivelogic.api.logic.INetworkPart;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/*
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public abstract class BaseNetworkEntity extends BlockEntity implements INetworkPart {

    protected final PartType partType;
    public BaseNetworkEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, PartType partType) {
        super(type, pos, state);
        this.partType = partType;
    }

    /**
     * Will be called for logic update, only if container block set as tickable
     */
    public void update() {
    }

    @Override
    public PartType getPartType() {
        return partType;
    }
}
