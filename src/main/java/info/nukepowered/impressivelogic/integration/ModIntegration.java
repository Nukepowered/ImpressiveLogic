package info.nukepowered.impressivelogic.integration;

import info.nukepowered.impressivelogic.integration.theoneprobe.TheOneProbeIntegration;
import net.minecraftforge.fml.ModList;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.ArrayList;
import java.util.List;

import static info.nukepowered.impressivelogic.ImpressiveLogic.LOGGER;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public record ModIntegration(String modId, Class<? extends Runnable> clazz) implements Runnable {

    public static final Marker INTEGRATION_MARKER = MarkerFactory.getMarker("MOD_INTEGRATION");
    public static final String ONE_PROBE_MODID = "theoneprobe";

    private static final List<ModIntegration> integrations = new ArrayList<>();

    public static void registerIntegrations() {
        integrations.add(new ModIntegration(ONE_PROBE_MODID, TheOneProbeIntegration.class));
    }

    public static void setup() {
        integrations.forEach(Runnable::run);
    }

    @Override
    public void run() {
        if (ModList.get().isLoaded(modId)) {
            try {
                clazz.getConstructor().newInstance().run();
            } catch (Exception e) {
                LOGGER.error(INTEGRATION_MARKER, "", e);
            }
        }
    }
}
