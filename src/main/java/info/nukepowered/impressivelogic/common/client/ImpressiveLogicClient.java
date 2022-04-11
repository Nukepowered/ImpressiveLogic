package info.nukepowered.impressivelogic.common.client;

import info.nukepowered.impressivelogic.common.registry.BlockRegistry;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class ImpressiveLogicClient {

    @SubscribeEvent
    public static void setupClient(final FMLClientSetupEvent event) {
        /*
          In this method we will set render type for blocks if it not renders correctly.
         */

        registerRenderType(BlockRegistry.NETWORK_CABLE, RenderType.translucent()); // Allow transparency for render
    }

    private static void registerRenderType(RegistryObject<? extends Block> block, RenderType renderType) {
        ItemBlockRenderTypes.setRenderLayer(block.get(), renderType);
    }
}
