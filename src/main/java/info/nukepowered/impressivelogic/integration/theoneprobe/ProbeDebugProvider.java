package info.nukepowered.impressivelogic.integration.theoneprobe;

import info.nukepowered.impressivelogic.ImpressiveLogic;
import info.nukepowered.impressivelogic.api.logic.INetworkPart;
import info.nukepowered.impressivelogic.common.logic.network.LogicNetManager;
import mcjty.theoneprobe.api.*;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class ProbeDebugProvider implements IProbeInfoProvider {

    private final ResourceLocation ID = new ResourceLocation(ImpressiveLogic.MODID, "debug");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo probeInfo, Player player, Level level, BlockState blockState, IProbeHitData hitData) {
        if (probeMode == ProbeMode.DEBUG && blockState.getBlock() instanceof INetworkPart) {
            final var components = new ArrayList<Component>();
            final var opt = LogicNetManager.findNetwork(level, hitData.getPos());

            if (opt.isPresent()) {
                var network = opt.get();
                var eopt = network.findEntity(hitData.getPos());
                if (eopt.isPresent()) {
                    var entity = eopt.get();
                    entity.getPart().provideNetworkDebug(components, network, eopt.get());
                }
            }

            components.forEach(probeInfo::mcText);
        }
    }
}
