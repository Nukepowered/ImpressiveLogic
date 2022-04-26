package info.nukepowered.impressivelogic.common.blockentity;

import info.nukepowered.impressivelogic.api.logic.INetworkPart;

import info.nukepowered.impressivelogic.common.block.AbstractNetworkBlock;
import info.nukepowered.impressivelogic.common.logic.network.LogicNetManager;
import info.nukepowered.impressivelogic.common.logic.network.Network;
import info.nukepowered.impressivelogic.common.logic.network.Network.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;

/*
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public abstract class BaseNetworkEntity extends BlockEntity implements INetworkPart {

    protected final PartType partType;

    protected WeakReference<Network> networkRef = new WeakReference<>(null);

    public BaseNetworkEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, PartType partType) {
        super(type, pos, state);
        this.partType = partType;
    }

    protected Network getNetwork() {
        var network = this.networkRef.get();

        if (network == null) {
            network = LogicNetManager.findNetwork(this.level, this.worldPosition)
                .orElseThrow(() -> new IllegalStateException("Was trying to get network of entity that not member of any " + this.worldPosition));
            this.networkRef = new WeakReference<>(network);
        }

        return network;
    }

    /**
     * Will be called for logic update, only if container block set as tickable
     */
    public void update() {
    }

    /**
     * Will be called 4 times total:
     *  Client / Server
     *  Main hand / Off-hand
     */
    public InteractionResult onRightClick(Player player, InteractionHand hand, BlockHitResult hitResult) {
        return InteractionResult.PASS;
    }

    public void onLeftClick(Player player) {
    }

    public void onNeighbourChanged(Block updatedBy, BlockPos updatedFrom) {
    }

    @Override
    public void provideNetworkDebug(List<Component> components, Network network, Entity part) {
        // Call same code for network entity info
        if (this.getBlockState().getBlock() instanceof AbstractNetworkBlock block) {
            block.provideNetworkDebug(components, network, part);
        }
    }

    @Override
    public PartType getPartType() {
        return partType;
    }
}
