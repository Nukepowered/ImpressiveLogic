package info.nukepowered.impressivelogic.common.util;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Predicate;

/*
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class PredicateEntityType<T extends BlockEntity> extends BlockEntityType<T> {

    private final Predicate<BlockState> validator;

    public PredicateEntityType(BlockEntitySupplier<? extends T> factory, Predicate<BlockState> validator) {
        super(factory, Collections.emptySet(), null);
        this.validator = Objects.requireNonNull(validator);
    }

    @Override
    public boolean isValid(BlockState state) {
        return this.validator.test(state);
    }
}
