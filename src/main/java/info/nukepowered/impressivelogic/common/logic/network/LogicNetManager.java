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
        // Recompile will not being scheduled since it is new net with only one member
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

    /**
     * Removes part from registry, and from network
     *
     * This method is not validate state, will just delete part from registry.
     * If validation is required, call {@link #validateNetwork(Level, BlockPos, Direction)} right after
     * @see info.nukepowered.impressivelogic.common.block.AbstractNetworkBlock
     *
     * @param level
     * @param pos
     */
    public static void removeFromNetwork(Level level, BlockPos pos) {
        var opt = findNetwork(level, pos);
        if (opt.isPresent()) {
            var network = opt.get();
            NETWORKS.unregisterPartMapping(level.dimension().location(), pos);
            network.unregisterPart(pos);
        }
    }

    /**
     * Will check network integrity, in case of network changes will schedule recompile.
     *
     * Should be called on neighbour update, in case of part was removed
     *
     * @param level
     * @param updatedBlock block that calling this validation
     * @param updatedFrom Direction update triggered from (will not check this direction)
     */
    public static void validateNetwork(Level level, BlockPos updatedBlock, Direction updatedFrom) {
        // If throws NPE here - you are using this method wrong
        // It should be called only if Part of network notice update around
        var currentNetwork = findNetwork(level, updatedBlock).get();
        var part = currentNetwork.findEntity(updatedBlock).get();
        final var initSides = new HashSet<>(part.getConnectableSides(level, updatedBlock));

        // Will not check for side update came from, block removal expected
        initSides.remove(updatedFrom);

        // Direction to come back - Sides not visited
        Deque<Pair<Direction, Queue<Direction>>> moveStack = new LinkedList<>();
        Queue<Direction> sides = new LinkedList<>(initSides);
        var parts = new HashSet<BlockPos>(); // found parts
        var visited = new HashSet<BlockPos>(); // visited blocks
        var currentPos = new MutableBlockPos().set(updatedBlock);

        // Current block must be part of this network
        parts.add(updatedBlock);

        main:
        while (true) {
            while (!sides.isEmpty()) {
                final var side = sides.poll();
                currentPos.move(side);
                if (!visited.contains(currentPos)) {
                    var opt = currentNetwork.findEntity(currentPos);
                    // If network part is still there, and it's accepting connection - add
                    if (opt.isPresent() && opt.get().acceptConnection(level, currentPos, side.getOpposite())) {
                        parts.add(currentPos.immutable());
                        moveStack.add(Pair.of(side.getOpposite(), sides)); // Need to check it neighbours as well
                        sides = new LinkedList<>(opt.get().getConnectableSides(level, currentPos));
                        sides.remove(side.getOpposite()); // Do not check side we came from

                        continue main;
                    }
                }

                // Since we did not find anything, come back
                currentPos.move(side.getOpposite());
                visited.add(currentPos);
            }
            if (moveStack.isEmpty()) {
                break;
            } else {
                // Come to old position
                var pair = moveStack.pollLast();
                sides = pair.getValue();
                currentPos.move(pair.getKey());
            }
        }

        // In case network changed, pull all parts from network and create new
        if (!parts.containsAll(currentNetwork.getEntities())) {
            var newNetwork = currentNetwork.split(parts);
            NETWORKS.updateMappings(level.dimension().location(), newNetwork, parts);
            NetworkExecutionManager.instance().submit(new NetCompileTask(newNetwork));
        }
    }

    public static Optional<Network> findNetwork(Level level, BlockPos pos) {
        return NETWORKS.findNetwork(level.dimension().location(), pos);
    }

    public static NetworkRegistry getRegistry() {
        return NETWORKS;
    }
}
