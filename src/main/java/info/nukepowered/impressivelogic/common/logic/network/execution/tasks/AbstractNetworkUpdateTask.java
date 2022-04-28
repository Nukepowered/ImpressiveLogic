package info.nukepowered.impressivelogic.common.logic.network.execution.tasks;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import info.nukepowered.impressivelogic.api.logic.INetworkPart.PartType;
import info.nukepowered.impressivelogic.common.logic.network.Network;
import info.nukepowered.impressivelogic.common.logic.network.Network.Entity;
import info.nukepowered.impressivelogic.common.logic.network.execution.AbstractNetworkTask;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public abstract class AbstractNetworkUpdateTask extends AbstractNetworkTask {

    protected final MutableGraph<Entity<?>> graph;
    @Nullable
    protected final Entity<?> cause;

    public AbstractNetworkUpdateTask(Network suspect, @Nullable  Entity<?> cause) {
        super(suspect);
        this.cause = cause;
        this.graph = suspect.getConnections() == null ?
            GraphBuilder.directed().build() :
            Graphs.copyOf(suspect.getConnections());
    }

    /**
     * @param entity node to check
     * @return return true if signal can be passed through (cable, or logical gate with output)
     */
    protected boolean doNodePassThrough(Entity<?> entity) {
        return entity.getType() != PartType.IO; // TODO check if node has outputs
    }

    /**
     * Searches for any connected nodes (expect wires) from entity specified
     * @param from
     */
    protected Map<Entity<?>, Queue<Direction>> findConnectedNodes(Entity<?> from) {
        var result = new HashMap<Entity<?>, Queue<Direction>>();

        Deque<Pair<Direction, Queue<Direction>>> moveStack = new LinkedList<>();
        Queue<Direction> sides = new LinkedList<>(from.getConnections());
        var visited = new HashSet<BlockPos>();
        var pos = from.getLocation().mutable();

        visited.add(pos);

        do {
            if (!moveStack.isEmpty()) {
                var pair = moveStack.pollLast();
                sides = pair.getValue();
                pos.move(pair.getKey());
            }

            while (!sides.isEmpty()) {
                var side = sides.poll();
                pos.move(side);

                if (!visited.contains(pos)) {
                    visited.add(pos);
                    var node = entities.get(pos);
                    var nodeSides = new LinkedList<>(node.getConnections());
                    nodeSides.remove(side.getOpposite());

                    if (node.getType() == PartType.CONNECTOR) {
                        if (!nodeSides.isEmpty()) {
                            moveStack.add(Pair.of(side.getOpposite(), sides));
                            sides = nodeSides;
                            continue;
                        }
                    } else {
                        result.put(node, sides);
                    }
                }

                pos.move(side.getOpposite());
            }
        } while (!moveStack.isEmpty());

        return result;
    }

    /**
     * Will create edges in graph from entity to rest of them according to inputs/outputs
     * @param from
     * @param rest
     */
    protected void createDirectedEdges(Entity<?> from, Set<Entity<?>> rest) {
        // TODO raw impl, need to check inputs/outputs
        graph.addNode(from);
        rest.forEach(graph::addNode);

        if (from.isInputType()) {
            for (var entity : rest) {
                if (entity.isOutputType()) {
                    graph.putEdge(from, entity);
                }
            }
        } else if (from.isOutputType()) {
            for (var entity : rest) {
                if (entity.isInputType()) {
                    graph.putEdge(entity, from);
                }
            }
        }
    }
}
