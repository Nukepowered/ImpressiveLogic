package info.nukepowered.impressivelogic.api.logic;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public interface INetworkCable extends INetworkPart {

    @Override
    default PartType getType() {
        return PartType.CONNECTOR;
    }
}
