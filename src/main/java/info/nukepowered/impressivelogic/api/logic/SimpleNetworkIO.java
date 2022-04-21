package info.nukepowered.impressivelogic.api.logic;

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
