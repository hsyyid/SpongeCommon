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
package org.spongepowered.common.data.manipulator.mutable.item;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableMapItemData;
import org.spongepowered.api.data.manipulator.mutable.item.MapItemData;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.common.data.manipulator.immutable.item.ImmutableSpongeMapItemData;
import org.spongepowered.common.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.common.data.value.mutable.SpongeListValue;
import org.spongepowered.common.data.value.mutable.SpongeValue;

import java.util.List;

public class SpongeMapItemData extends AbstractData<MapItemData, ImmutableMapItemData> implements MapItemData {

    private int xCenter;
    private int zCenter;
    private int dimensionId;
    private int scale;
    private List<String> visiblePlayers;
    private List<Byte> colors;
    
    public SpongeMapItemData() {
        this(0, 0, 0, 1, Lists.newArrayList(), Lists.newArrayList());
    }

    public SpongeMapItemData(int xCenter, int zCenter, int dimensionId, int scale, List<String> visiblePlayers, List<Byte> colors) {
        super(MapItemData.class);
        this.xCenter = xCenter;
        this.zCenter = zCenter;
        this.dimensionId = dimensionId;
        this.scale = scale;
        this.visiblePlayers = visiblePlayers;
        this.colors = colors;
        this.registerGettersAndSetters();
    }

    @Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(Keys.MAP_SCALE, () -> this.scale);
        registerFieldSetter(Keys.MAP_SCALE, this::setScale);
        registerKeyValue(Keys.MAP_SCALE, this::scale);

        registerFieldGetter(Keys.MAP_DIMENSION, () -> this.dimensionId);
        registerFieldSetter(Keys.MAP_DIMENSION, this::setDimensionId);
        registerKeyValue(Keys.MAP_DIMENSION, this::dimensionId);

        registerFieldGetter(Keys.MAP_VISIBLE_PLAYERS, () -> this.visiblePlayers);
        registerFieldSetter(Keys.MAP_VISIBLE_PLAYERS, this::setVisiblePlayers);
        registerKeyValue(Keys.MAP_VISIBLE_PLAYERS, this::visiblePlayers);

        registerFieldGetter(Keys.MAP_XCENTER, () -> this.xCenter);
        registerFieldSetter(Keys.MAP_XCENTER, this::setXCenter);
        registerKeyValue(Keys.MAP_XCENTER, this::xCenter);

        registerFieldGetter(Keys.MAP_ZCENTER, () -> this.zCenter);
        registerFieldSetter(Keys.MAP_ZCENTER, this::setZCenter);
        registerKeyValue(Keys.MAP_ZCENTER, this::zCenter);
        
        registerFieldGetter(Keys.MAP_COLORS, () -> this.colors);
        registerFieldSetter(Keys.MAP_COLORS, this::setColors);
        registerKeyValue(Keys.MAP_COLORS, this::colors);
    }

    @Override
    public MapItemData copy() {
        return new SpongeMapItemData(this.xCenter, this.zCenter, this.dimensionId, this.scale, this.visiblePlayers, this.colors);
    }

    @Override
    public ImmutableMapItemData asImmutable() {
        return new ImmutableSpongeMapItemData(this.xCenter, this.zCenter, this.dimensionId, this.scale, this.visiblePlayers, this.colors);
    }

    @Override
    public int compareTo(MapItemData o) {
        return ComparisonChain.start()
                .compare(this.xCenter, o.xCenter().get().intValue())
                .compare(this.zCenter, o.zCenter().get().intValue())
                .compare(this.dimensionId, o.dimensionId().get().intValue())
                .compare(this.scale, o.scale().get().intValue())
                .result();
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer()
                .set(Keys.MAP_SCALE, this.scale)
                .set(Keys.MAP_DIMENSION, this.dimensionId)
                .set(Keys.MAP_VISIBLE_PLAYERS, this.visiblePlayers)
                .set(Keys.MAP_XCENTER, this.xCenter)
                .set(Keys.MAP_ZCENTER, this.zCenter)
                .set(Keys.MAP_COLORS, this.colors);
    }

    @Override
    public Value<Integer> xCenter() {
        return new SpongeValue<Integer>(Keys.MAP_XCENTER, this.xCenter);
    }
    
    public void setXCenter(int xCenter) {
        this.xCenter = xCenter;
    }

    @Override
    public Value<Integer> zCenter() {
        return new SpongeValue<Integer>(Keys.MAP_XCENTER, this.zCenter);
    }
    
    public void setZCenter(int zCenter) {
        this.zCenter = zCenter;
    }

    @Override
    public Value<Integer> dimensionId() {
        return new SpongeValue<Integer>(Keys.MAP_DIMENSION, this.dimensionId);
    }

    public void setDimensionId(int dimensionId) {
        this.dimensionId = dimensionId;
    }
    
    @Override
    public Value<Integer> scale() {
        return new SpongeValue<Integer>(Keys.MAP_SCALE, this.scale);
    }
    
    public void setScale(int scale) {
        this.scale = scale;
    }

    @Override
    public ListValue<String> visiblePlayers() {
        return new SpongeListValue<String>(Keys.MAP_VISIBLE_PLAYERS, this.visiblePlayers);
    }
    
    public void setVisiblePlayers(List<String> visiblePlayers) {
        this.visiblePlayers = visiblePlayers;
    }

    @Override
    public ListValue<Byte> colors() {
        return new SpongeListValue<Byte>(Keys.MAP_COLORS, this.colors);
    }
    
    public void setColors(List<Byte> colors) {
        this.colors = colors;
    }
}
