package info.nukepowered.impressivelogic.common.logic.network;

import info.nukepowered.impressivelogic.api.logic.INetworkPart;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class LogicNetworkRegistry {

    public static final Marker NETWORK_MARKER = MarkerFactory.getMarker("LogicNetwork");

    /**
     * World registry name to network mapping
     */
    private static Map<ResourceLocation, Map<BlockPos, Network>> REGISTRY;

    @SubscribeEvent
    public void onServerAboutToStop(final ServerStoppedEvent event) {
        REGISTRY = null;
    }

    @SubscribeEvent
    public void onServerAboutToStart(final ServerAboutToStartEvent event) {
        REGISTRY = new HashMap<>();
    }

    @SubscribeEvent
    public void onWorldLoading(final WorldEvent.Load event) {
        final var level = (Level) event.getWorld();
        final var dimension = level.dimension().location();
        final var networks = REGISTRY.getOrDefault(dimension, new HashMap<>());

        NetworkIO.readNetworks(level, networks::put);
        REGISTRY.put(dimension, networks);
    }

    @SubscribeEvent
    public void onWorldUnload(final WorldEvent.Unload event) {
        final var dimension = ((Level) event.getWorld()).dimension().location();
        var networks = REGISTRY.getOrDefault(dimension, new HashMap<>());
        NetworkIO.writeNetworks(dimension, networks.values());
    }

    public static void register(ResourceLocation dimensionId, BlockPos pos, INetworkPart part) {
        var map = REGISTRY.getOrDefault(dimensionId, new HashMap<>());
        var network = map.getOrDefault(pos, new Network());
        network.registerPart(pos, part);
        map.put(pos, network);
        REGISTRY.putIfAbsent(dimensionId, map);
    }

    public static void unregister(ResourceLocation dimensionId, BlockPos pos) {
        var networks = REGISTRY.get(dimensionId);
        if (networks != null) {
            var network = networks.remove(pos);
            if (network != null) {
                network.unregisterPart(pos);
            }
        }
    }

    @Nullable
    public static Network getNetwork(ResourceLocation dimension, BlockPos pos) {
        return Optional.ofNullable(REGISTRY.get(dimension))
                .map(map -> map.get(pos))
                .orElse(null);
    }

    public static Set<Network> getNetworksForLevel(ResourceLocation dimension) {
        return Optional.ofNullable(REGISTRY.get(dimension))
                .map(Map::values)
                .map(Set::copyOf)
                .orElseGet(HashSet::new);
    }
}
