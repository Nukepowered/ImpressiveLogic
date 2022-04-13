package info.nukepowered.impressivelogic.common.logic.network;

import info.nukepowered.impressivelogic.api.logic.INetworkPart;
import info.nukepowered.impressivelogic.common.logic.network.execution.NetworkExecutionManager;
import info.nukepowered.impressivelogic.common.logic.network.execution.tasks.NetCompileTask;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class LogicNetManager {

    private static NetworkRegistry NETWORKS;

    @SubscribeEvent
    public static void onServerAboutToStop(final ServerStoppedEvent event) {
        MinecraftForge.EVENT_BUS.unregister(NETWORKS);
        NETWORKS = null;
    }

    @SubscribeEvent
    public static void onServerAboutToStart(final ServerAboutToStartEvent event) {
        MinecraftForge.EVENT_BUS.register(NETWORKS = new NetworkRegistry());
    }

    public static Network registerNewNetwork(Level level, BlockPos partPos, INetworkPart part) {
        final var network = NETWORKS.registerNewNetwork(level.dimension().location(), partPos);
        network.registerPart(partPos, part);
        return network;
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
                        NETWORKS.registerPartMapping(level.dimension().location(), network, partPos);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static void mergeNetworks(Level level, Collection<Network> networks) {
        var queue = new ArrayDeque<>(networks);
        var first = queue.poll();

        for (var net : queue) {
            first.merge(net);
            NETWORKS.updateMappings(level.dimension().location(), first, net.getEntities());
        }

        NetworkExecutionManager.instance().submit(new NetCompileTask(first));
    }

    public static void removeFromNetwork(Level level, BlockPos pos) {
        var opt = findNetwork(level, pos);
        if (opt.isPresent()) {
            var network = opt.get();
            NETWORKS.unregisterPartMapping(level.dimension().location(), pos);
            network.unregisterPart(pos);
        }
    }

    public static void validateNetwork(Level level, BlockPos updatedBlock, Direction updatedFrom) {
        var currentNetwork = findNetwork(level, updatedBlock).get();
        var part = currentNetwork.findEntity(updatedBlock).get();
        final var initSides = new HashSet<>(part.getConnectableSides(level, updatedBlock));

        initSides.remove(updatedFrom);

        Deque<Pair<Direction, Queue<Direction>>> moveQueue = new LinkedList<>();
        Queue<Direction> sides = new LinkedList<>(initSides);
        var parts = new HashSet<BlockPos>();
        var visited = new HashSet<BlockPos>();
        var currentPos = new MutableBlockPos().set(updatedBlock);

        parts.add(updatedBlock);

        main:
        while (true) {
            while (!sides.isEmpty()) {
                final var side = sides.poll();
                currentPos.move(side);
                if (!visited.contains(currentPos)) {
                    var opt = currentNetwork.findEntity(currentPos);
                    if (opt.isPresent()) {
                        parts.add(currentPos.immutable());
                        moveQueue.add(Pair.of(side.getOpposite(), sides));
                        sides = new LinkedList<>(opt.get().getConnectableSides(level, currentPos));
                        sides.remove(side.getOpposite());

                        continue main;
                    }
                }

                currentPos.move(side.getOpposite());
                visited.add(currentPos);
            }
            if (moveQueue.isEmpty()) {
                break;
            } else {
                var pair = moveQueue.pollLast();
                sides = pair.getValue();
                currentPos.move(pair.getKey());
            }
        }

        if (!parts.containsAll(currentNetwork.getEntities())) {
            var newNetwork = currentNetwork.split(parts);
            NETWORKS.updateMappings(level.dimension().location(), newNetwork, parts);
        }
    }

    public static Optional<Network> findNetwork(Level level, BlockPos pos) {
        return NETWORKS.findNetwork(level.dimension().location(), pos);
    }

    public static NetworkRegistry getRegistry() {
        return NETWORKS;
    }
}
