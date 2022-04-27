package info.nukepowered.impressivelogic.api.logic;

import info.nukepowered.impressivelogic.common.logic.network.Network;
import info.nukepowered.impressivelogic.common.logic.network.Network.Entity;
import info.nukepowered.impressivelogic.common.util.ComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.List;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public interface INetworkPart {

    /**
     * List of text to display on One Probe / Debug Item
     */
    default void provideNetworkDebug(List<Component> components, Network network, Entity<?> part) {
        components.add(new TextComponent("Network Information")
            .withStyle(ChatFormatting.YELLOW));

        components.add(ComponentUtils.property("Size", network.getEntities().size()));
        components.add(ComponentUtils.property("Inputs", network.getInputs().size()));
        components.add(ComponentUtils.property("Has Graph", network.getConnections() != null)); // TODO
    }

    /**
     * @param level
     * @param pos   of this part
     * @param from
     */
    default boolean acceptConnection(Level level, BlockPos pos, Direction from) {
        return getConnectableSides(level, pos).contains(from);
    }

    /**
     * Override this method if this block a container of actual part
     */
    default INetworkPart getPart(Level level, BlockPos pos) {
        return this;
    }

    /**
     * Will search for networks on these sides.
     *
     * @param level
     * @param pos
     * @return iterable of directions is supported for network connections
     */
    Collection<Direction> getConnectableSides(Level level, BlockPos pos);

    PartType getPartType();

    enum PartType {
        /**
         * Should be instance of {@link INetworkCable}
         */
        CONNECTOR,
        STATEFUL,
        STATELESS,
        IO
    }
}
