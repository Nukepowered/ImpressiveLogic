package info.nukepowered.impressivelogic.api.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public interface INetworkPart {

    @Nullable
    default Component provideDebugInformation(Level level, BlockPos pos) {
        return null;
    }

    /**
     * @param level
     * @param pos of this part
     * @param from
     */
    default boolean acceptConnection(Level level, BlockPos pos, Direction from) {
        return true;
    }
}
