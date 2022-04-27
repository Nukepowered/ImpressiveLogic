package info.nukepowered.impressivelogic.common.logic.network;

import info.nukepowered.impressivelogic.common.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class NetworkRegistry {

    public static final Marker NETWORK_MARKER = MarkerFactory.getMarker("LogicNetwork");

    /**
     * World registry name to network mapping
     */
    private final ConcurrentMap<ResourceLocation, ConcurrentMap<BlockPos, Network>> REGISTRY;

    NetworkRegistry() {
        this.REGISTRY = new ConcurrentHashMap<>();
    }

    @SubscribeEvent
    public void onWorldLoading(final WorldEvent.Load event) {
        final var level = (Level) event.getWorld();
        final var dimension = level.dimension().location();
        final var networks = this.getWorldRegistry(dimension);

        NetworkUtils.readNetworks(level, networks::put);
        REGISTRY.put(dimension, networks);
    }

    @SubscribeEvent
    public void onWorldUnload(final WorldEvent.Unload event) {
        final var dimension = ((Level) event.getWorld()).dimension().location();
        var networks = this.getWorldRegistry(dimension);
        NetworkUtils.writeNetworks(dimension, new HashSet<>(networks.values()));
    }

    /**
     * Will register new network mapping for selected dimension
     * Network Part Pos -> Network
     *
     * @param dimensionId
     * @param netPosition position of network first network element
     * @return new network object
     */
    public Network registerNewNetwork(ResourceLocation dimensionId, BlockPos netPosition) {
        var worldRegistry = this.getWorldRegistry(dimensionId);
        var network = new Network();
        worldRegistry.put(netPosition, network);
        REGISTRY.putIfAbsent(dimensionId, worldRegistry);
        return network;
    }

    /**
     * Associates given part position with network in chosen dimension
     *
     * @param dimensionId
     * @param network
     * @param partPosition
     */
    public void registerPartMapping(ResourceLocation dimensionId, Network network, BlockPos partPosition) {
        var worldRegistry = this.getWorldRegistry(dimensionId);
        worldRegistry.put(partPosition, network);
        REGISTRY.putIfAbsent(dimensionId, worldRegistry);
    }

    public void updateMappings(ResourceLocation dimensionId, Network network, Collection<BlockPos> positions) {
        var worldRegistry = this.getWorldRegistry(dimensionId);
        for (var pos : positions) {
            worldRegistry.replace(pos, network);
        }
    }

    /**
     * Removing network association of selected part position
     *
     * @param dimensionId
     * @param partPosition
     */
    public void unregisterPartMapping(ResourceLocation dimensionId, BlockPos partPosition) {
        var networks = this.getWorldRegistry(dimensionId);
        if (networks != null) {
            networks.remove(partPosition);
        }
    }

    public Optional<Network> findNetwork(ResourceLocation dimension, BlockPos pos) {
        return Optional.ofNullable(REGISTRY.get(dimension))
            .map(map -> map.get(pos));
    }

    public Set<Network> getNetworksForLevel(ResourceLocation dimension) {
        return Optional.ofNullable(REGISTRY.get(dimension))
            .map(Map::values)
            .map(Set::copyOf)
            .orElseGet(HashSet::new);
    }

    private ConcurrentMap<BlockPos, Network> getWorldRegistry(ResourceLocation dimensionId) {
        return REGISTRY.getOrDefault(dimensionId, new ConcurrentHashMap<>());
    }
}
