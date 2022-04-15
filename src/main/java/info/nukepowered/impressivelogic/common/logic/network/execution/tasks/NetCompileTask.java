package info.nukepowered.impressivelogic.common.logic.network.execution.tasks;

import info.nukepowered.impressivelogic.common.logic.network.Network;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import static info.nukepowered.impressivelogic.ImpressiveLogic.LOGGER;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class NetCompileTask implements Runnable {

    public static final Marker COMPILE_MARKER = MarkerFactory.getMarker("NET_COMPILE");

    private final Network suspect;

    public NetCompileTask(Network suspect) {
        this.suspect = suspect;
    }

    @Override
    public void run() {
        LOGGER.debug(COMPILE_MARKER, "Network compile execution for {}", suspect);
    }
}
