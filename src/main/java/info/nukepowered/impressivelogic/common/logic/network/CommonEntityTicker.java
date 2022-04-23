package info.nukepowered.impressivelogic.common.logic.network;

import info.nukepowered.impressivelogic.ImpressiveLogic;
import info.nukepowered.impressivelogic.common.blockentity.BaseNetworkEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/*
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public final class CommonEntityTicker {

    public static final Marker BLOCK_ENTITY_MARKER = MarkerFactory.getMarker("BLOCK_LOGIC");

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T entity) {
        try {
            ((BaseNetworkEntity) entity).update();
        } catch (Exception e) {
            ImpressiveLogic.LOGGER.error(BLOCK_ENTITY_MARKER, "Unexpected exception thrown during block update", e);
            entity.setRemoved(); // add config for this (no removal by default)
        }
    }
}
