package info.nukepowered.impressivelogic.common.util;

/*
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
@FunctionalInterface
public interface TriConsumer<A, B, C> {

    void accept(A a, B b, C c);
}
