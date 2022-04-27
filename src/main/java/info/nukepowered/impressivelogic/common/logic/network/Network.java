package info.nukepowered.impressivelogic.common.logic.network;

import com.google.common.graph.Graph;
import info.nukepowered.impressivelogic.api.logic.INetworkPart;
import info.nukepowered.impressivelogic.api.logic.INetworkPart.PartType;
import info.nukepowered.impressivelogic.api.logic.io.INetworkInput;
import info.nukepowered.impressivelogic.common.logic.network.execution.NetworkExecutionManager;
import info.nukepowered.impressivelogic.common.logic.network.execution.tasks.NetStateUpdateTask;
import info.nukepowered.impressivelogic.common.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static info.nukepowered.impressivelogic.ImpressiveLogic.LOGGER;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class Network {

    private Set<Entity<?>> entities = ConcurrentHashMap.newKeySet();
    private Set<Entity<INetworkInput<?>>> inputs = new HashSet<>();
    private Graph<Entity<?>> connections;

    /**
     * Will trigger network to check logic state and update outputs
     */
    public void markDirty() {
        NetworkExecutionManager.instance()
            .submit(new NetStateUpdateTask(this));
    }

    public <T extends INetworkPart> Entity<T> registerPart(BlockPos pos, T part) {
        final var entity = new Entity<>(pos, part);
        if (!entities.add(entity)) {
            LOGGER.error(NetworkRegistry.NETWORK_MARKER,
                "Re-registering of part detected, it is an error!", new Exception());
            return null;
        } else if (entity.isInputType()) {
            this.inputs.add((Entity<INetworkInput<?>>) entity);
        }

        return entity;
    }

    public void unregisterPart(BlockPos pos) {
        var opt = this.findEntity(pos);
        if (opt.isPresent()) {
            var entity = opt.get();
            // Get all connected before blocks mapping: pos - direction from
            var toUpdate = entity.connections.stream()
                .map(dir -> Pair.of(pos.relative(dir), dir.getOpposite()))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
            entities.remove(entity);
            if (entity.isInputType()) {
                inputs.remove(entity);
            }

            // Get all entities connected before and remove connection
            this.entities.stream()
                .filter(e -> toUpdate.containsKey(e.location))
                .map(e -> Pair.of(e, toUpdate.get(e.location)))
                .forEach(p -> p.getKey().connections.remove(p.getValue()));
        }
    }

    public void merge(Network other) {
        var tEntities = this.entities.stream()
            .collect(Collectors.toMap(Entity::getLocation, Function.identity()));
        var oEntities = other.entities.stream()
            .collect(Collectors.toMap(Entity::getLocation, Function.identity()));


        for (var entry : oEntities.entrySet()) {
            var oEntity = entry.getValue();
            var tEntity = tEntities.get(entry.getKey());
            if (tEntity != null) {
                oEntity.connections.addAll(tEntity.connections);
            }

            tEntities.put(entry.getKey(), oEntity);
        }


        this.entities.clear();
        tEntities.values().forEach(this.entities::add);

        this.entities.addAll(other.entities);
        this.inputs.addAll(other.inputs);
    }

    public Network split(Collection<BlockPos> parts) {
        var newNet = new Network();
        entities.stream()
            .filter(e -> parts.contains(e.location))
            .forEach(e -> {
                newNet.entities.add(e);
                if (e.isInputType()) {
                    newNet.inputs.add((Entity<INetworkInput<?>>) e);
                }
            });

        entities.removeAll(parts); // different types, but since it is HASH set, it will work anyway.
        inputs.removeAll(parts);
        return newNet;
    }

    public Set<BlockPos> getEntityLocations() {
        return this.entities.stream()
            .map(Entity::getLocation)
            .collect(Collectors.toSet());
    }

    /**
     * @return set of new entities according to entity map. To get actual network structure get graph first {@link #getConnections()}
     */
    public Set<Entity<?>> getEntities() {
        return Set.copyOf(this.entities);
    }

    public Set<Entity<INetworkInput<?>>> getInputs() {
        return Set.copyOf(this.inputs);
    }

    public Optional<Entity<?>> findEntity(BlockPos pos) {
        return this.entities.stream()
            .filter(e -> e.location.equals(pos))
            .findFirst();
    }

    public Graph<Entity<?>> getConnections() {
        return this.connections;
    }

    public void setConnections(Graph<Entity<?>> connections) {
        this.connections = connections;
    }

    public boolean isEmpty() {
        return this.entities.isEmpty();
    }

    public CompoundTag writeToNBT() {
        final var compound = new CompoundTag();
        final var entities = new ListTag();

        for (var entity : this.entities) {
            var entityData = new CompoundTag();

            {
                var stream = new ByteArrayOutputStream();
                entity.connections.stream()
                    .mapToInt(Direction::ordinal)
                    .forEach(stream::write);

                entityData.put("pos", NbtUtils.writeBlockPos(entity.location));
                entityData.putByteArray("dirs", stream.toByteArray());
            }

            entities.add(entityData);
        }

        compound.put("entities", entities);
        return compound;
    }

    public void readFromNBT(CompoundTag compound, LevelAccessor accessor) {
        for (var tag : compound.getList("entities", Tag.TAG_COMPOUND)) {
            final var entity = (CompoundTag) tag;
            {
                var location = NbtUtils.readBlockPos((CompoundTag) entity.get("pos"));
                var connections = new HashSet<Direction>();
                for (byte b : entity.getByteArray("dirs")) {
                    connections.add(Direction.values()[b]);
                }

                var block = accessor.getBlockState(location).getBlock();
                if (block instanceof INetworkPart part) {
                    var result = new Entity(location, part.getPart((Level) accessor, location));
                    result.connections.addAll(connections);
                    this.entities.add(result);
                    if (result.isInputType()) {
                        this.inputs.add(result);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("entities", entities)
            .build();
    }

    /**
     * Immutable network entry
     */
    public static class Entity<T extends INetworkPart> {

        public static final Comparator<Entity<?>> COMPARATOR = Comparator.comparing(Entity::getLocation);

        private final BlockPos location;
        private final WeakReference<T> partRef;
        private final Set<Direction> connections = new HashSet<>();

        public Entity(BlockPos pos, T part) {
            this.location = pos;
            this.partRef = new WeakReference<>(part);
        }

        public Set<Direction> getConnections() {
            return this.connections;
        }

        public BlockPos getLocation() {
            return this.location;
        }

        public T getPart() {
            return this.partRef.get();
        }

        public PartType getType() {
            return this.getPart().getPartType();
        }

        public boolean isInputType() {
            var part = this.getPart();
            return part.getPartType() == PartType.IO && part instanceof INetworkInput<?>;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Entity entity && this.location.equals(entity.location);
        }

        @Override
        public int hashCode() {
            return this.location.hashCode();
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
                .append("position", NetworkUtils.blockPosToString(this.location))
                .append("type", this.getType())
                .append("connections", this.connections)
                .build();
        }
    }
}
