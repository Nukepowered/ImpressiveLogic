package info.nukepowered.impressivelogic.api.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public interface INetworkIO<T> extends INetworkPart {

    T getState(Level level, BlockPos pos);

    void setState(Level level, BlockPos pos, T state);

    /**
     * Indicates that state is not represented by boolean type, but with some object
     *
     */
    default boolean isComplex() {
        return false;
    }
}
