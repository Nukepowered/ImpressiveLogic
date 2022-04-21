package info.nukepowered.impressivelogic.api.logic;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public interface INetworkIO<T> extends INetworkPart {

    T getState();

    void setState(T state);
}
