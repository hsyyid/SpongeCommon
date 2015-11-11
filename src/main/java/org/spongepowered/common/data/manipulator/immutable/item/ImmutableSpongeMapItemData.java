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
package org.spongepowered.common.data.manipulator.immutable.item;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableMapItemData;
import org.spongepowered.api.data.manipulator.mutable.item.MapItemData;
import org.spongepowered.api.data.value.immutable.ImmutableListValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.common.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.common.data.manipulator.mutable.item.SpongeMapItemData;
import org.spongepowered.common.data.value.immutable.ImmutableSpongeListValue;
import org.spongepowered.common.data.value.immutable.ImmutableSpongeValue;

import java.util.List;

public class ImmutableSpongeMapItemData extends AbstractImmutableData<ImmutableMapItemData, MapItemData> implements ImmutableMapItemData {

    private final ImmutableValue<Integer> xCenter;
    private final ImmutableValue<Integer> zCenter;
    private final ImmutableValue<Integer> dimensionId;
    private final ImmutableValue<Integer> scale;
    private final ImmutableListValue<String> visiblePlayers;
    private final ImmutableListValue<Byte> colors;

    public ImmutableSpongeMapItemData(int xCenter, int zCenter, int dimensionId, int scale, List<String> visiblePlayers, List<Byte> colors) {
        super(ImmutableMapItemData.class);
        this.xCenter = ImmutableSpongeValue.cachedOf(Keys.MAP_XCENTER, 0, xCenter);
        this.zCenter = ImmutableSpongeValue.cachedOf(Keys.MAP_ZCENTER, 0, zCenter);
        this.dimensionId = ImmutableSpongeValue.cachedOf(Keys.MAP_DIMENSION, 0, dimensionId);
        this.scale = ImmutableSpongeValue.cachedOf(Keys.MAP_SCALE, 0, scale);
        this.visiblePlayers = new ImmutableSpongeListValue<String>(Keys.MAP_VISIBLE_PLAYERS, ImmutableList.copyOf(visiblePlayers));
        this.colors = new ImmutableSpongeListValue<Byte>(Keys.MAP_COLORS, ImmutableList.copyOf(colors));
        this.registerGetters();
    }

    public ImmutableSpongeMapItemData() {
        this(0, 0, 1, 0, Lists.newArrayList(), Lists.newArrayList());
    }

    @Override
    protected void registerGetters() {
        registerFieldGetter(Keys.MAP_SCALE, () -> this.scale);
        registerKeyValue(Keys.MAP_SCALE, this::scale);

        registerFieldGetter(Keys.MAP_DIMENSION, () -> this.dimensionId);
        registerKeyValue(Keys.MAP_DIMENSION, this::dimensionId);

        registerFieldGetter(Keys.MAP_VISIBLE_PLAYERS, () -> this.visiblePlayers);
        registerKeyValue(Keys.MAP_VISIBLE_PLAYERS, this::visiblePlayers);

        registerFieldGetter(Keys.MAP_XCENTER, () -> this.xCenter);
        registerKeyValue(Keys.MAP_XCENTER, this::xCenter);

        registerFieldGetter(Keys.MAP_ZCENTER, () -> this.zCenter);
        registerKeyValue(Keys.MAP_ZCENTER, this::zCenter);
        
        registerFieldGetter(Keys.MAP_COLORS, () -> this.colors);
        registerKeyValue(Keys.MAP_COLORS, this::colors);
    }

    @Override
    public MapItemData asMutable() {
        return new SpongeMapItemData();
    }

    @Override
    public int compareTo(ImmutableMapItemData o) {
        return ComparisonChain.start()
                .compare(this.xCenter.get(), o.xCenter().get())
                .compare(this.zCenter.get(), o.zCenter().get())
                .compare(this.scale.get(), o.scale().get())
                .compare(this.dimensionId.get(), o.dimensionId().get())
                .result();
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer()
                .set(Keys.MAP_COLORS, this.colors.get())
                .set(Keys.MAP_DIMENSION, this.dimensionId.get())
                .set(Keys.MAP_SCALE, this.scale.get())
                .set(Keys.MAP_VISIBLE_PLAYERS, this.visiblePlayers.get())
                .set(Keys.MAP_XCENTER, this.xCenter.get())
                .set(Keys.MAP_ZCENTER, this.zCenter.get());
    }

    @Override
    public ImmutableValue<Integer> xCenter() {
        return this.xCenter;
    }

    @Override
    public ImmutableValue<Integer> zCenter() {
        return this.zCenter;
    }

    @Override
    public ImmutableValue<Integer> dimensionId() {
        return this.dimensionId;
    }

    @Override
    public ImmutableValue<Integer> scale() {
        return this.scale;
    }

    @Override
    public ImmutableListValue<String> visiblePlayers() {
        return this.visiblePlayers;
    }

    @Override
    public ImmutableListValue<Byte> colors() {
        return this.colors;
    }
}
