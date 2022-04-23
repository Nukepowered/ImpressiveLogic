package info.nukepowered.impressivelogic.common.logic.network.execution.tasks;

import info.nukepowered.impressivelogic.common.logic.network.Network;

import static info.nukepowered.impressivelogic.ImpressiveLogic.LOGGER;

/*
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
        LOGGER.debug("Network state update task for {}", suspect);
    }
}
