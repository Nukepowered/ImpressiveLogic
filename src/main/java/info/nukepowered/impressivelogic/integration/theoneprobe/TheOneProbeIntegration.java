package info.nukepowered.impressivelogic.integration.theoneprobe;

import info.nukepowered.impressivelogic.integration.ModIntegration;
import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraftforge.fml.InterModComms;

import java.util.function.Function;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class TheOneProbeIntegration implements Runnable {

    @Override
    public void run() {
        Function<ITheOneProbe, Void> setup = this::setup;
        InterModComms.sendTo(ModIntegration.ONE_PROBE_MODID, "getTheOneProbe", () -> setup);
    }

    public Void setup(ITheOneProbe oneProbe) {
        oneProbe.registerProvider(new ProbeDebugProvider());

        return null;
    }
}
