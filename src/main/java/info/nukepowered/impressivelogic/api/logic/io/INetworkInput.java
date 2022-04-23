package info.nukepowered.impressivelogic.api.logic.io;

import info.nukepowered.impressivelogic.api.logic.INetworkPart;

/*
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public interface INetworkInput<T> extends INetworkPart {

    T getState();
}
