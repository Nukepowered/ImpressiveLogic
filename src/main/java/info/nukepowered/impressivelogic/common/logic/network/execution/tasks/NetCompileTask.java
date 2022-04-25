package info.nukepowered.impressivelogic.common.logic.network.execution.tasks;

import com.google.common.graph.GraphBuilder;
import info.nukepowered.impressivelogic.api.logic.io.INetworkInput;
import info.nukepowered.impressivelogic.api.logic.io.INetworkOutput;
import info.nukepowered.impressivelogic.common.logic.network.Network;

import info.nukepowered.impressivelogic.common.logic.network.Network.Entity;
import net.minecraft.core.BlockPos;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;

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
        var gbuilder = GraphBuilder.directed().<Entity>immutable();

        var entities = new TreeSet<>(Entity.COMPARATOR);
        var inputs = new LinkedList<Entity>();
        var outputs = new LinkedList<Entity>();
        var logic = new LinkedList<Entity>();
        entities.addAll(suspect.getEntities());

        // Sorting nodes
        for (var entity : entities) {
            switch (entity.getType()) {
                case IO: {
                    var part = entity.getPart();
                    if (part instanceof INetworkInput<?>) {
                        outputs.add(entity);
                    }
                    if (part instanceof INetworkOutput<?>) {
                        inputs.add(entity);
                    }
                }
                case STATEFUL:
                case STATELESS:
                    logic.add(entity);
            }
        }

        var completed = new HashSet<BlockPos>();
        while (!inputs.isEmpty()) {
            var entity = inputs.pop();


//            entity.part().


        }

        LOGGER.info("entities " + entities);
        LOGGER.info("inputs " + inputs);
        LOGGER.info("outputs " + outputs);
        LOGGER.info("logic " + logic);


        suspect.setConnections(gbuilder.build());
    }
}
