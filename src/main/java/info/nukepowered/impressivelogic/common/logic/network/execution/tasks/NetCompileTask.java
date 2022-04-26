package info.nukepowered.impressivelogic.common.logic.network.execution.tasks;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;

import info.nukepowered.impressivelogic.api.logic.INetworkPart.PartType;
import info.nukepowered.impressivelogic.common.logic.network.Network;
import info.nukepowered.impressivelogic.common.logic.network.Network.Entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static info.nukepowered.impressivelogic.ImpressiveLogic.LOGGER;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class NetCompileTask implements Runnable {

    public static final Marker COMPILE_MARKER = MarkerFactory.getMarker("NET_COMPILE");

    private final Network suspect;
    private final ImmutableGraph.Builder<Entity<?>> gbuilder;
    private final NavigableMap<BlockPos, Entity<?>> entities;

    public NetCompileTask(Network suspect) {
        this.suspect = suspect;
        this.gbuilder = GraphBuilder.directed().<Entity<?>>immutable();
        this.entities = new TreeMap<>(Comparator.naturalOrder());
    }

    @Override
    public void run() {
        LOGGER.debug(COMPILE_MARKER, "Network compile execution for {}", suspect);
        // Init of entities mapping, and filtering input nodes
        Map<Entity<?>, Queue<Direction>> inputs = suspect.getInputs().stream()
            .collect(Collectors.toMap(Function.identity(), e -> new LinkedList<>(e.getConnections())));
        suspect.getEntities().forEach(e -> entities.put(e.getLocation(), e));

        this.compileGraph(inputs);
    }

    /**
     * Will assemble raw graph of network entity connections
     */
    private void compileGraph(Map<Entity<?>, Queue<Direction>> nodes) {
        var newNodes = new HashMap<Entity<?>, Queue<Direction>>();
        while (true) {
            for (var entry : nodes.entrySet()) {
                gbuilder.addNode(entry.getKey());
                this.findEdges(newNodes, entry.getKey(), entry.getValue());
            }

            nodes = newNodes;
            if (newNodes.isEmpty()) {
                break;
            }
        }

        suspect.setConnections(gbuilder.build());
    }

    /**
     * Searches for edges of this node in sides of sidesToCheck
     *
     * @param foundNodes if new nodes was found, will be added to this map with their not checked directions
     * @param entity node start edges from
     * @param sidesToCheck this node not checked sides
     */
    private void findEdges(Map<Entity<?>, Queue<Direction>> foundNodes, Entity<?> entity, Queue<Direction> sidesToCheck) {
        Deque<Pair<Direction, Queue<Direction>>> moveStack = new LinkedList<>();
        MutableBlockPos pos = entity.getLocation().mutable();

        while (true) {
            while (!sidesToCheck.isEmpty()) {
                var side = sidesToCheck.poll();
                pos.move(side);
                var node = entities.get(pos);
                var sides = new LinkedList<>(node.getConnections());
                sides.remove(side.getOpposite());

                if (node.getType() == PartType.CONNECTOR) {
                    if (!sides.isEmpty()) {
                        moveStack.add(Pair.of(side.getOpposite(), sidesToCheck));
                        sidesToCheck = sides;
                        continue;
                    }
                } else {
                    gbuilder.addNode(node);
                    gbuilder.putEdge(entity, node);
                    if (!sides.isEmpty() && this.doNodePassThrough(node)) {
                        foundNodes.put(node, sides);
                    }
                }

                pos.move(side.getOpposite());
            }

            if (moveStack.isEmpty()) {
                break;
            } else {
                var pair = moveStack.pollLast();
                sidesToCheck = pair.getValue();
                pos.move(pair.getKey());
            }
        }
    }

    /**
     * @param entity node to check
     * @return return true if signal can be passed through (cable, or logical gate with output)
     */
    private boolean doNodePassThrough(Entity<?> entity) {
        return entity.getType() != PartType.IO; // TODO check if node has outputs
    }
}
