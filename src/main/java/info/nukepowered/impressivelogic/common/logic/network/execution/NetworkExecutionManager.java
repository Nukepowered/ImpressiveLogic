package info.nukepowered.impressivelogic.common.logic.network.execution;

import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class NetworkExecutionManager {

    private static NetworkExecutionManager INSTANCE;

    private final ExecutorService logicExecutor;

    private NetworkExecutionManager() {
        this.logicExecutor = Executors.newSingleThreadExecutor();
    }

    public void submit(Runnable task) {
        logicExecutor.submit(task);
    }

    public static NetworkExecutionManager instance() {
        return INSTANCE;
    }

    @SubscribeEvent
    public static void onServerStart(final ServerStartingEvent event) {
        if (INSTANCE != null) {
            throw new IllegalStateException("NetworkExecutor is initialized, is it second call?");
        } else {
            INSTANCE = new NetworkExecutionManager();
        }
    }

    @SubscribeEvent
    public static void onServerStop(final ServerStoppingEvent event) {
        INSTANCE.logicExecutor.shutdown();

        try {
            if (!INSTANCE.logicExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
                INSTANCE.logicExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            INSTANCE.logicExecutor.shutdownNow();
        }

        INSTANCE = null;
    }
}
