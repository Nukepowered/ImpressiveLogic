package info.nukepowered.impressivelogic.common.logic.network.execution.tasks;

import info.nukepowered.impressivelogic.common.logic.network.Network;
import info.nukepowered.impressivelogic.common.logic.network.Network.Entity;
import org.jetbrains.annotations.Nullable;

import static info.nukepowered.impressivelogic.ImpressiveLogic.LOGGER;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class NetworkUpdateRemoveTask extends AbstractNetworkUpdateTask {

    public NetworkUpdateRemoveTask(Network suspect, @Nullable Entity<?> cause) {
        super(suspect, cause);
    }

    @Override
    protected void update() {
        LOGGER.debug(COMPILE_MARKER, "Network REMOVAL execution for {}", suspect);
        this.graph.removeNode(this.cause);
    }
}
