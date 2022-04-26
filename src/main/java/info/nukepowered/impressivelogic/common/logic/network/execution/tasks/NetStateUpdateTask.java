package info.nukepowered.impressivelogic.common.logic.network.execution.tasks;

import info.nukepowered.impressivelogic.api.logic.INetworkPart.PartType;
import info.nukepowered.impressivelogic.api.logic.io.INetworkOutput;
import info.nukepowered.impressivelogic.common.logic.network.Network;

import static info.nukepowered.impressivelogic.ImpressiveLogic.LOGGER;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class NetStateUpdateTask implements Runnable {

    private final Network suspect;

    public NetStateUpdateTask(Network suspect) {
        this.suspect = suspect;
    }

    @Override
    public void run() {
        final var graph = suspect.getConnections();
        for (var part : suspect.getInputs()) {
            for (var successor : graph.successors(part)) {
                // TODO DRAFT for now, need to rewrite when logic will be applied
                var succPart = successor.getPart();
                if (succPart.getPartType() == PartType.IO && succPart instanceof INetworkOutput output) {
                    output.setState(part.getPart().getState());
                } else {
                    LOGGER.warn("Unsupported type ({}) for state update", successor.getType());
                }
            }
        }
    }
}
