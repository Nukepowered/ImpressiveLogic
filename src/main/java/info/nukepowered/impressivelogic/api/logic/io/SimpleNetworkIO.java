package info.nukepowered.impressivelogic.api.logic.io;

/*
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public interface SimpleNetworkIO extends INetworkIO<Boolean> {

    @Override
    Boolean getState();

    @Override
    void setState(Boolean state);
}
