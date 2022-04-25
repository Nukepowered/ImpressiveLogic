package info.nukepowered.impressivelogic.common.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

/*
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class ComponentUtils {

    public static Component property(String key, Object value) {
        return property(key, value, 1);
    }

    public static Component property(String key, Object value, int padding) {
        var spaces = new String(new char[padding]).replace('\0', ' ');
        var val = value == null ? "null" : value.toString();
        return new TextComponent(String.format("%s%s: ", spaces, key)).withStyle(ChatFormatting.GRAY)
            .append(new TextComponent(val).withStyle(ChatFormatting.WHITE));
    }
}
