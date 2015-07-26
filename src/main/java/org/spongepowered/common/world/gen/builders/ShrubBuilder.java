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
package org.spongepowered.common.world.gen.builders;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.block.BlockTallGrass;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import org.spongepowered.api.data.type.ShrubType;
import org.spongepowered.api.util.VariableAmount;
import org.spongepowered.api.util.weighted.WeightedCollection;
import org.spongepowered.api.util.weighted.WeightedObject;
import org.spongepowered.api.world.gen.populator.Shrub;
import org.spongepowered.api.world.gen.populator.Shrub.Builder;

import java.util.Collection;

public class ShrubBuilder implements Shrub.Builder {

    private VariableAmount count;
    private WeightedCollection<WeightedObject<ShrubType>> types;

    public ShrubBuilder() {
        reset();
    }

    @Override
    public Builder perChunk(VariableAmount count) {
        this.count = checkNotNull(count, "count");
        return this;
    }

    @Override
    public Builder types(WeightedObject<ShrubType>... types) {
        this.types.clear();
        for (WeightedObject<ShrubType> type : types) {
            if (type != null && type.get() != null) {
                this.types.add(type);
            }
        }
        return this;
    }

    @Override
    public Builder types(Collection<WeightedObject<ShrubType>> types) {
        this.types.clear();
        for (WeightedObject<ShrubType> type : types) {
            if (type != null && type.get() != null) {
                this.types.add(type);
            }
        }
        return this;
    }

    @Override
    public Builder reset() {
        if (this.types == null) {
            this.types = new WeightedCollection<WeightedObject<ShrubType>>();
        } else {
            this.types.clear();
        }
        this.count = VariableAmount.fixed(128);
        return this;
    }

    @Override
    public Shrub build() throws IllegalStateException {
        Shrub pop = (Shrub) new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS);
        pop.getType().clear();
        pop.getType().addAll(this.types);
        pop.setShrubsPerChunk(this.count);
        return pop;
    }

}
