package info.nukepowered.impressivelogic.common.logic.network.execution;

import info.nukepowered.impressivelogic.common.logic.network.Network;
import info.nukepowered.impressivelogic.common.logic.network.Network.Entity;
import net.minecraft.core.BlockPos;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Comparator;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public abstract class AbstractNetworkTask implements Runnable {

    public static final Marker COMPILE_MARKER = MarkerFactory.getMarker("NET_COMPILE");

    protected final Network suspect;
    protected final NavigableMap<BlockPos, Entity<?>> entities;

    public AbstractNetworkTask(Network suspect) {
        this.suspect = suspect;
        this.entities = new TreeMap<>(Comparator.naturalOrder());
    }

    protected abstract void execute();

    @Override
    public final void run() {
        suspect.getEntities().forEach(e -> entities.put(e.getLocation(), e));
        this.execute();
    }
}
