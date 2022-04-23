package info.nukepowered.impressivelogic.api.logic.io;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public interface INetworkIO<T> extends INetworkInput<T> {

    void setState(T state);
}
