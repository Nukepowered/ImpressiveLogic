package info.nukepowered.impressivelogic.api.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Collection;

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
        return getConnectableSides(level, pos).contains(from);
    }

    /**
     * Override this method if this block a container of actual part
     */
    default INetworkPart getPart() {
        return this;
    }

    /**
     * Will search for networks on these sides.
     *
     * @return iterable of directions is supported for network connections
     * @param level
     * @param pos
     */
    Collection<Direction> getConnectableSides(Level level, BlockPos pos);

    PartType getType();

    enum PartType {
        /**
         * Should be instance of {@link INetworkCable}
         */
        CONNECTOR,
        STATEFUL,
        STATELESS,
        IO;
    }
}
