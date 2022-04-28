package info.nukepowered.impressivelogic.common.logic.network.execution.tasks;

import com.google.common.graph.ImmutableGraph;
import info.nukepowered.impressivelogic.api.logic.INetworkPart.PartType;
import info.nukepowered.impressivelogic.common.logic.network.Network;
import info.nukepowered.impressivelogic.common.logic.network.Network.Entity;
import net.minecraft.core.Direction;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

import java.util.*;
import java.util.function.Supplier;

import static info.nukepowered.impressivelogic.ImpressiveLogic.LOGGER;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class NetworkUpdateAddTask extends AbstractNetworkUpdateTask {

    public NetworkUpdateAddTask(Network suspect, @Nullable Entity<?> cause) {
        super(suspect, cause);
    }

    @Override
    public void execute() {
        LOGGER.debug(COMPILE_MARKER, "Network ADD_NODE execution for {}, cause {}", suspect, cause);

        if (this.cause.getType() != PartType.CONNECTOR)  {
            var connectedNodes = this.findConnectedNodes(this.cause).keySet();
            // Just add new connections from new node, do not update graph fully
            this.createDirectedEdges(this.cause, connectedNodes);
        }

        this.suspect.setConnections(ImmutableGraph.copyOf(graph));
    }


}
