package info.nukepowered.impressivelogic;

import info.nukepowered.impressivelogic.common.client.ImpressiveLogicClient;
import info.nukepowered.impressivelogic.common.logic.network.LogicNetManager;
import info.nukepowered.impressivelogic.common.logic.network.execution.NetworkExecutionManager;
import info.nukepowered.impressivelogic.common.registry.BlockRegistry;
import info.nukepowered.impressivelogic.common.registry.ItemRegistry;

import info.nukepowered.impressivelogic.common.registry.TileEntityRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
@Mod(ImpressiveLogic.MODID)
public class ImpressiveLogic {

	public static final String MODID = "impressivelogic";
	public static final Logger LOGGER = LoggerFactory.getLogger(ImpressiveLogic.class);
	public static final Marker COMMON_MARKER = MarkerFactory.getMarker("COMMON");

	public ImpressiveLogic() {
		final var fmlBus = FMLJavaModLoadingContext.get().getModEventBus();

		fmlBus.addListener(this::setupCommon);
		if (FMLEnvironment.dist.isClient()) {
			fmlBus.register(ImpressiveLogicClient.class);
		}

		MinecraftForge.EVENT_BUS.register(LogicNetManager.class);
		MinecraftForge.EVENT_BUS.register(NetworkExecutionManager.class);

		BlockRegistry.init();
		ItemRegistry.init();
		TileEntityRegistry.init();
	}

	private void setupCommon(final FMLCommonSetupEvent event) {

	}
}
