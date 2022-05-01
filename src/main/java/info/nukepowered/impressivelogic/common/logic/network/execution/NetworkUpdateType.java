package info.nukepowered.impressivelogic.common.logic.network.execution;

import info.nukepowered.impressivelogic.common.logic.network.Network;
import info.nukepowered.impressivelogic.common.logic.network.Network.Entity;
import info.nukepowered.impressivelogic.common.logic.network.execution.tasks.*;

import java.util.function.BiFunction;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public enum NetworkUpdateType {

    COMPILE(NetworkUpdateCompileTask::new),
    ADD_NODE(NetworkUpdateAddTask::new),
    MERGE(NetworkUpdateMergeTask::new),
    REMOVE_NODE(NetworkUpdateRemoveTask::new);

    private final BiFunction<Network, Entity<?>, ? extends AbstractNetworkUpdateTask> factory;

    NetworkUpdateType(BiFunction<Network, Entity<?>, ? extends AbstractNetworkUpdateTask> factory) {
        this.factory = factory;
    }

    public AbstractNetworkUpdateTask createTask(Network network, Entity<?> cause) {
        return this.factory.apply(network, cause);
    }
}
