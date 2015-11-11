/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.data.processor.data.item;

import org.spongepowered.api.world.World;

import org.spongepowered.common.Sponge;
import com.google.common.primitives.Bytes;
import com.google.common.collect.ImmutableMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.MapData;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionBuilder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableMapItemData;
import org.spongepowered.api.data.manipulator.mutable.item.MapItemData;
import org.spongepowered.common.data.manipulator.mutable.item.SpongeMapItemData;
import org.spongepowered.common.data.processor.common.AbstractItemDataProcessor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MapItemDataProcessor extends AbstractItemDataProcessor<MapItemData, ImmutableMapItemData> {

    public MapItemDataProcessor() {
        super(input -> input.getItem().equals(Items.filled_map));
    }

    @Override
    public boolean doesDataExist(ItemStack itemStack) {
        return itemStack.getItem().equals(Items.filled_map);
    }

    @Override
    public boolean set(ItemStack itemStack, Map<Key<?>, Object> keyValues) {
        if (doesDataExist(itemStack)) {
            ItemMap map = (ItemMap) itemStack.getItem();
            MapData mapData = getMapData(map, itemStack);
            if (mapData != null) {
                mapData.colors = Bytes.toArray(((List<Byte>) keyValues.get(Keys.MAP_COLORS)));
                mapData.dimension = (byte) keyValues.get(Keys.MAP_DIMENSION);
                mapData.scale = (byte) keyValues.get(Keys.MAP_SCALE);
                mapData.playersVisibleOnMap = keyValues.get(Keys.MAP_VISIBLE_PLAYERS);
                keyValues.get(Keys.MAP_XCENTER);
                keyValues.get(Keys.MAP_ZCENTER);
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<Key<?>, ?> getValues(ItemStack itemStack) {
        if (doesDataExist(itemStack)) {
            ItemMap map = (ItemMap) itemStack.getItem();
            MapData mapData = getMapData(map, itemStack);
            if (mapData != null) {
                return ImmutableMap.of(Keys.MAP_XCENTER, mapData.xCenter, Keys.MAP_ZCENTER, mapData.zCenter, Keys.MAP_SCALE,
                        ((Byte) mapData.scale).intValue(), Keys.MAP_COLORS, Arrays.asList(mapData.colors), Keys.MAP_DIMENSION, mapData.dimension,
                        Keys.MAP_VISIBLE_PLAYERS, mapData.playersVisibleOnMap);
            }
        }
    }

    private MapData getMapData(ItemMap map, ItemStack itemStack) {
        // TODO: Is there a better way to do this?
        MapData mapData = null;
        for (World world : Sponge.getGame().getServer().getWorlds()) {
            if (map.getMapData(itemStack, (net.minecraft.world.World) world) != null) {
                mapData = map.getMapData(itemStack, (net.minecraft.world.World) world);
            }
        }
        return mapData;
    }

    @Override
    public MapItemData createManipulator() {
        return new SpongeMapItemData();
    }

    @Override
    public Optional<MapItemData> fill(DataContainer container, MapItemData durabilityData) {
        final Optional<Integer> durability = container.getInt(Keys.ITEM_DURABILITY.getQuery());
        final Optional<Boolean> unbreakable = container.getBoolean(Keys.UNBREAKABLE.getQuery());
        if (durability.isPresent() && unbreakable.isPresent()) {
            durabilityData.set(Keys.ITEM_DURABILITY, durability.get());
            durabilityData.set(Keys.UNBREAKABLE, unbreakable.get());
            return Optional.of(durabilityData);
        }
        return Optional.empty();
    }

    @Override
    public DataTransactionResult remove(DataHolder dataHolder) {
        return DataTransactionBuilder.failNoData();
    }
}
