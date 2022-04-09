package info.nukepowered.impressivelogic.common.registry;

import info.nukepowered.impressivelogic.ImpressiveLogic;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ImpressiveLogicTabs {

    public static final CreativeModeTab MAIN = createNewTab(() -> BlockRegistry.NETWORK_CABLE, "main");

    private static CreativeModeTab createNewTab(Supplier<?> icon, String name) {
        return new CreativeModeTab(CreativeModeTab.getGroupCountSafe(), ImpressiveLogic.MODID + "." + name) {

            private ItemStack fromRegistryObject(RegistryObject<?> registry) {
                var obj = registry.get();
                return obj instanceof ItemLike itemLike ? new ItemStack(itemLike) : ItemStack.EMPTY;
            }

            @Override
            public ItemStack makeIcon() {
                var obj = icon.get();
                return obj instanceof ItemStack stack ? stack :
                        obj instanceof RegistryObject<?> reg ? fromRegistryObject(reg) : ItemStack.EMPTY;
            }
        };
    }
}
