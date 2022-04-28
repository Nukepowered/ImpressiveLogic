package info.nukepowered.impressivelogic.common.logic.network.execution;

import info.nukepowered.impressivelogic.common.logic.network.Network;
import info.nukepowered.impressivelogic.common.logic.network.Network.Entity;
import info.nukepowered.impressivelogic.common.logic.network.execution.tasks.AbstractNetworkUpdateTask;
import info.nukepowered.impressivelogic.common.logic.network.execution.tasks.NetworkUpdateCompileTask;
import info.nukepowered.impressivelogic.common.logic.network.execution.tasks.NetworkUpdateAddTask;

import java.util.function.BiFunction;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public enum NetworkUpdateType {

    COMPILE(NetworkUpdateCompileTask::new),
    ADD_NODE(NetworkUpdateAddTask::new);

    private final BiFunction<Network, Entity<?>, ? extends AbstractNetworkUpdateTask> factory;

    NetworkUpdateType(BiFunction<Network, Entity<?>, ? extends AbstractNetworkUpdateTask> factory) {
        this.factory = factory;
    }

    public AbstractNetworkUpdateTask createTask(Network network, Entity<?> cause) {
        return this.factory.apply(network, cause);
    }
}
