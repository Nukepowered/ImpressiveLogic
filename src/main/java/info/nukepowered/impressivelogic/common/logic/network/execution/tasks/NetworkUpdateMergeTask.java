package info.nukepowered.impressivelogic.common.logic.network.execution.tasks;

import com.google.common.collect.Sets;
import info.nukepowered.impressivelogic.api.logic.INetworkPart.PartType;
import info.nukepowered.impressivelogic.common.logic.network.Network;
import javax.annotation.Nullable;

import java.util.Set;

import static info.nukepowered.impressivelogic.ImpressiveLogic.LOGGER;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class NetworkUpdateMergeTask extends AbstractNetworkUpdateTask {

    public NetworkUpdateMergeTask(Network suspect, @Nullable Network.Entity<?> cause) {
        super(suspect, cause);
    }

    @Override
    protected void update() {
        LOGGER.debug(COMPILE_MARKER, "Network MERGE execution for {}, cause {}", suspect, cause);
        var connectedNodes = Sets.newHashSet(this.findConnectedNodes(this.cause).keySet());

        if (this.cause.getType() == PartType.CONNECTOR) {
            // For each of entity create connections
            for (var entity : Set.copyOf(connectedNodes)) {
                connectedNodes.remove(entity);
                this.createDirectedEdges(entity, connectedNodes);
                connectedNodes.add(entity);
            }
        } else {
            this.createDirectedEdges(this.cause, connectedNodes);
        }

        LOGGER.info(this.graph.toString());
    }
}
