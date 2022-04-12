package info.nukepowered.impressivelogic.common.logic.network;

import info.nukepowered.impressivelogic.api.logic.INetworkPart;
import info.nukepowered.impressivelogic.common.logic.network.execution.NetworkExecutionManager;
import info.nukepowered.impressivelogic.common.logic.network.execution.tasks.NetCompileTask;
import info.nukepowered.impressivelogic.common.util.NetworkUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.*;

import static info.nukepowered.impressivelogic.ImpressiveLogic.LOGGER;

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

        NetworkUtils.readNetworks(level, networks::put);
        REGISTRY.put(dimension, networks);
    }

    @SubscribeEvent
    public void onWorldUnload(final WorldEvent.Unload event) {
        final var dimension = ((Level) event.getWorld()).dimension().location();
        var networks = REGISTRY.getOrDefault(dimension, new HashMap<>());
        NetworkUtils.writeNetworks(dimension, new HashSet<>(networks.values()));
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

    /**
     * @param network to join
     * @param partPos position of requester part
     * @param from side of part we are trying to access
     * @param part requester part
     * @return true if joined to network
     */
    public static boolean joinNetwork(Level level, Network network, BlockPos partPos, Direction from, INetworkPart part) {
        if (!network.getEntities().contains(partPos)) {
            var netPos = partPos.relative(from);
            var netDir = from.getOpposite();
            var partOptional = network.findEntity(netPos);

            if (partOptional.isPresent()) {
                var netPart = partOptional.get();
                if (netPart.acceptConnection(level, netPos, netDir)) {
                    if (network.registerPart(partPos, part)) {
                        var dimension = level.dimension().location();
                        var map = REGISTRY.getOrDefault(dimension, new HashMap<>());
                        map.put(partPos, network);
                        REGISTRY.put(dimension, map);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static void finishNetworkJoin(Collection<Network> networks) {
        var nets = new ArrayList<>(networks);

        if (nets.size() > 1) {
            // TODO
            LOGGER.info(NETWORK_MARKER, "Network join for more than 1 is not implemented yet {}", networks);
        } else {
            NetworkExecutionManager.instance()
                    .submit(new NetCompileTask(nets.get(0)));
        }
    }

    public static Optional<Network> findNetwork(ResourceLocation dimension, BlockPos pos) {
        return Optional.ofNullable(REGISTRY.get(dimension))
                .map(map -> map.get(pos));
    }

    public static Set<Network> getNetworksForLevel(ResourceLocation dimension) {
        return Optional.ofNullable(REGISTRY.get(dimension))
                .map(Map::values)
                .map(Set::copyOf)
                .orElseGet(HashSet::new);
    }
}
