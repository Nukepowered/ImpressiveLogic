package info.nukepowered.impressivelogic.common.util;

import info.nukepowered.impressivelogic.common.logic.network.Network;
import info.nukepowered.impressivelogic.common.logic.network.NetworkRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.nio.file.StandardOpenOption.*;
import static info.nukepowered.impressivelogic.ImpressiveLogic.LOGGER;

/*
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class NetworkUtils {

    public static final LevelResource DATA_PATH = new LevelResource("data/impressivelogic");

    public static void writeNetworks(ResourceLocation dimension, Collection<Network> networks) {
        final var networkList = new ListTag();
        var path = getPath();

        networks.stream()
                .map(Network::writeToNBT)
                .forEach(networkList::add);
        try {
            var tag = createOrReadFile(path);
            try (var output = Files.newOutputStream(path.resolve("networks.dat"), WRITE, TRUNCATE_EXISTING)) {
                tag.put(dimension.toString(), networkList);
                NbtIo.writeCompressed(tag, output);
            }
        } catch (Exception e) {
            LOGGER.error(NetworkRegistry.NETWORK_MARKER, "Unable to save networks!", e);
        }
    }

    public static void readNetworks(LevelAccessor accessor, BiConsumer<BlockPos, Network> registrar) {
        var path = getPath().resolve("networks.dat");
        var dimension = ((Level) accessor).dimension().location();
        if (Files.notExists(path)) {
            return;
        }

        try (var input = Files.newInputStream(path, READ)) {
            final var compound = NbtIo.readCompressed(input);
            for (var tag : compound.getList(dimension.toString(), Tag.TAG_COMPOUND)) {
                var network = new Network();
                network.readFromNBT((CompoundTag) tag, accessor);
                for (var entity : network.getEntityLocations()) {
                    registrar.accept(entity, network);
                }
            }
        } catch (Exception e) {
            LOGGER.error(NetworkRegistry.NETWORK_MARKER, "Unable to read saved networks!", e);
        }
    }

    public static Set<Direction> getConnectedDirections(Network network, BlockPos pos, Collection<Direction> connectableSides) {
        var sides = new HashSet<>(connectableSides);
        var result = new HashSet<Direction>();

        for (var side : sides) {
            if (network.findEntity(pos.relative(side)).isPresent()) {
                result.add(side);
            }
        }

        return result;
    }

    public static Level getLevel(ResourceLocation dimensionId) {
        var server = ServerLifecycleHooks.getCurrentServer();
        var resourceKey = server.levelKeys().stream()
                .filter(key -> key.getRegistryName().equals(dimensionId))
                .findFirst();
        return server.getLevel(resourceKey.get());
    }

    public static String blockPosToString(BlockPos pos) {
        return String.format("[%s, %s, %s]", pos.getX(), pos.getY(), pos.getZ());
    }

    private static CompoundTag createOrReadFile(Path path) throws IOException {
        var compound = new CompoundTag();

        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }

        path = path.resolve("networks.dat");

        if (Files.notExists(path)) {
            Files.createFile(path);
        } else {
            try (var input = Files.newInputStream(path)) {
                compound = NbtIo.readCompressed(input);
            } catch (Exception e) {}
        }

        return compound;
    }

    private static Path getPath() {
        return ServerLifecycleHooks.getCurrentServer()
                .getWorldPath(DATA_PATH);
    }
}
