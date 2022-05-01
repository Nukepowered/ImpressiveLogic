package info.nukepowered.impressivelogic.common.logic.network.execution.tasks;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import info.nukepowered.impressivelogic.api.logic.INetworkPart.PartType;
import info.nukepowered.impressivelogic.common.logic.network.Network;
import info.nukepowered.impressivelogic.common.logic.network.Network.Entity;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import static info.nukepowered.impressivelogic.ImpressiveLogic.LOGGER;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class NetworkUpdateCompileTask extends AbstractNetworkUpdateTask {

    public NetworkUpdateCompileTask(Network suspect, @Nullable Entity<?> cause) {
        super(suspect, cause);
    }

    @Override
    public void update() {
        LOGGER.debug(COMPILE_MARKER, "Network COMPILE execution for {}", suspect);
        // Init of entities mapping, and filtering input nodes
        Map<Entity<?>, Queue<Direction>> inputs = suspect.getInputs().stream()
            .collect(Collectors.toMap(Function.identity(), e -> new LinkedList<>(e.getConnections())));
        this.compileGraph(inputs);
    }

    /**
     * Will assemble raw graph of network entity connections
     */
    private void compileGraph(Map<Entity<?>, Queue<Direction>> nodes) {
        do {
            for (var entry : nodes.entrySet()) {
                var connectedNodes = this.findConnectedNodes(entry.getKey());
                if (!connectedNodes.isEmpty()) {
                    this.createDirectedEdges(entry.getKey(), connectedNodes.keySet());
                }

                nodes = connectedNodes.entrySet().stream()
                    .filter(this::checkNodesEntry)
                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
            }
        } while (!nodes.isEmpty());
    }

    private boolean checkNodesEntry(Entry<Entity<?>, Queue<Direction>> entry) {
        return !entry.getValue().isEmpty() && this.doNodePassThrough(entry.getKey());
    }
}
