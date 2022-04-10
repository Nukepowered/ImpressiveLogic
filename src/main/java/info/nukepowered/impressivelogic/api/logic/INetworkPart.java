package info.nukepowered.impressivelogic.api.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public interface INetworkPart {

    @Nullable
    default Component provideDebugInformation(Level level, BlockPos pos) {
        return null;
    }

}
