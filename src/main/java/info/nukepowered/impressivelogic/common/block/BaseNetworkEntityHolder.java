package info.nukepowered.impressivelogic.common.block;

import info.nukepowered.impressivelogic.api.logic.INetworkPart;
import info.nukepowered.impressivelogic.common.logic.network.CommonEntityTicker;
import info.nukepowered.impressivelogic.common.logic.network.blockentity.BaseNetworkEntity;
import info.nukepowered.impressivelogic.common.util.ExtendedProps;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.*;

import static info.nukepowered.impressivelogic.ImpressiveLogic.LOGGER;
import static info.nukepowered.impressivelogic.ImpressiveLogic.COMMON_MARKER;

/*
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class BaseNetworkEntityHolder<T extends BaseNetworkEntity> extends AbstractNetworkBlock implements EntityBlock {

    protected final EntityDefinition<T> definition;

    /**
     * To init holder please use {@link Builder}
     * @param definition
     */
    protected BaseNetworkEntityHolder(EntityDefinition<T> definition) {
        super(definition.blockProperties);
        this.definition = definition;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.definition.constructor.create(pos, state);
    }

    @Override
    public PartType getPartType() {
        return this.definition.type;
    }

    @Override
    public INetworkPart getPart(Level level, BlockPos pos) {
        var entity = level.getBlockEntity(pos);
        return entity != null && entity instanceof INetworkPart part ? part : this;
    }

    @Override
    protected BlockState registerDefaultBlockState() {
        var state = this.stateDefinition.any();
        var method = ObfuscationReflectionHelper.findMethod(StateHolder.class, "setValue", Property.class, Comparable.class);

        // used reflection since there is no way to save generics
        for (var entry : getDefinitionsOnInit()) {
            try {
                state = (BlockState) method.invoke(state, entry.getKey(), entry.getValue());
            } catch (Exception e) {
                LOGGER.error(COMMON_MARKER, "Unable to set state default value for block " + this, e);
            }
        }

        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        getDefinitionsOnInit().stream()
            .map(Pair::getKey)
            .forEach(builder::add);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entity) {
        if (definition.tickable) {
            return CommonEntityTicker::tick;
        }

        return null;
    }

    // Thanks for shitty API where is usage of one container for TEs is a pain
    private Set<Pair<Property<?>, ?>> getDefinitionsOnInit() {
        return (Set<Pair<Property<?>, ?>>) ((ExtendedProps) this.properties).extraData.get("states");
    }

    @Override
    public Collection<Direction> getConnectableSides(Level level, BlockPos pos) {
        return getPart(level, pos).getConnectableSides(level, pos);
    }

    /* EQUAL and HashCode will return always same to scroll MCs TE validation */

    @Override
    public int hashCode() {
        return this.getClass().getName().hashCode() ^ 31;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BaseNetworkEntityHolder<?>;
    }

    public static class Builder<T extends BaseNetworkEntity> {

        private BlockEntitySupplier<T> constructor;
        private PartType type;
        private Set<Pair<Property<?>, ?>> states;
        private ExtendedProps blockProperties;
        private boolean tickable = false;

        private Builder() {
            states = new HashSet<>();
        }

        public static <T extends BaseNetworkEntity> Builder<T> of(BlockEntitySupplier<T> constructor) {
            var builder = new Builder<T>();
            builder.constructor = constructor;
            return builder;
        }

        public Builder<T> type(PartType type) {
            this.type = type;
            return this;
        }

        public <E extends Comparable<E>> Builder<T> addState(Property<E> property, E defaultValue) {
            states.add(Pair.of(property, defaultValue));
            return this;
        }

        public Builder<T> blockProperties(ExtendedProps properties) {
            this.blockProperties = properties;
            return this;
        }

        public Builder<T> tickable() {
            this.tickable = true;
            return this;
        }
        public BaseNetworkEntityHolder<T> build() {
            blockProperties.extraData("states", states);
            var definition = new EntityDefinition<T>(constructor, type, states, blockProperties, tickable);
            return new BaseNetworkEntityHolder<>(definition);
        }
    }

    protected record EntityDefinition<T extends BaseNetworkEntity>(
            BlockEntitySupplier<T> constructor,
            PartType type,
            Set<Pair<Property<?>, ?>> states,
            Properties blockProperties,
            boolean tickable
    ) {
        protected EntityDefinition(BlockEntitySupplier<T> constructor, PartType type,
                                   Set<Pair<Property<?>, ?>> states, Properties blockProperties,
                                   boolean tickable) {
            this.constructor = constructor;
            this.type = type;
            this.states = Set.copyOf(states);
            this.blockProperties = blockProperties;
            this.tickable = tickable;
        }
    }
}
