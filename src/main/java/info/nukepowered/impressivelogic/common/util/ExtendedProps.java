package info.nukepowered.impressivelogic.common.util;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class ExtendedProps extends Properties {

    public final Map<String, Object> extraData = new HashMap<>();
    protected ExtendedProps(Material material) {
        super(material, material.getColor());
    }

    public static ExtendedProps of(Material Material) {
        return new ExtendedProps(Material);
    }

    public ExtendedProps extraData(String key, Object value) {
        extraData.put(key, value);
        return this;
    }
}
