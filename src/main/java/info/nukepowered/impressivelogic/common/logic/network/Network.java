package info.nukepowered.impressivelogic.common.logic.network;

import com.google.common.collect.MapMaker;
import info.nukepowered.impressivelogic.api.logic.INetworkPart;
import info.nukepowered.impressivelogic.common.logic.network.execution.NetworkExecutionManager;
import info.nukepowered.impressivelogic.common.logic.network.execution.tasks.NetStateUpdateTask;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.LevelAccessor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static info.nukepowered.impressivelogic.ImpressiveLogic.LOGGER;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class Network {

    private final ConcurrentMap<BlockPos, INetworkPart> entities = new MapMaker()
            .weakValues()
            .makeMap();

    /**
     * Will trigger network to check logic state and update outputs
     */
    public void markDirty() {
        NetworkExecutionManager.instance()
            .submit(new NetStateUpdateTask(this));
    }

    public boolean registerPart(BlockPos pos, INetworkPart part) {
        if (entities.put(pos, part) != null) {
            LOGGER.error(NetworkRegistry.NETWORK_MARKER,
                    "Re-registering of part detected, it is an error!", new Exception());
        }

        return true;
    }

    public void unregisterPart(BlockPos pos) {
        entities.remove(pos);
    }

    public void merge(Network other) {
        this.entities.putAll(other.entities);
    }

    public Network split(Collection<BlockPos> parts) {
        var newNet = new Network();
        entities.entrySet().stream()
                .filter(e -> parts.contains(e.getKey()))
                .forEach(e -> newNet.entities.put(e.getKey(), e.getValue()));
        entities.keySet().removeAll(parts);
        return newNet;
    }

    public Set<BlockPos> getEntities() {
        return Collections.unmodifiableSet(entities.keySet());
    }

    public Optional<INetworkPart> findEntity(BlockPos pos) {
        return Optional.ofNullable(entities.get(pos));
    }

    public boolean isEmpty() {
        return this.entities.isEmpty();
    }

    public CompoundTag writeToNBT() {
        final var compound = new CompoundTag();

        {
            var list = new ListTag();
            for (var pos : this.getEntities()) {
                list.add(NbtUtils.writeBlockPos(pos));
            }

            compound.put("entities", list);
        }

        return compound;
    }

    public void readFromNBT(CompoundTag compound, LevelAccessor accessor) {
        for (var tag : compound.getList("entities", Tag.TAG_COMPOUND)) {
            final var pos = NbtUtils.readBlockPos((CompoundTag) tag);
            var block = accessor.getBlockState(pos).getBlock();
            if (block instanceof INetworkPart part) {
                this.entities.put(pos, part);
            }
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("entities", entities)
                .build();
    }
}
