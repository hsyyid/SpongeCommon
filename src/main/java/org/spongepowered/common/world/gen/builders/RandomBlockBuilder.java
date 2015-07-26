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

import com.google.common.base.Predicate;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.util.VariableAmount;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.gen.populator.RandomBlock;
import org.spongepowered.api.world.gen.populator.RandomBlock.Builder;
import org.spongepowered.common.world.gen.populators.RandomBlockPopulator;

public class RandomBlockBuilder implements RandomBlock.Builder {

    private BlockState block;
    private VariableAmount count;
    private VariableAmount height;
    private Predicate<Location> target;

    public RandomBlockBuilder() {
        reset();
    }

    @Override
    public Builder block(BlockState block) {
        this.block = checkNotNull(block, "block");
        return this;
    }

    @Override
    public Builder perChunk(VariableAmount count) {
        this.count = checkNotNull(count, "count");
        return this;
    }

    @Override
    public Builder placementTarget(Predicate<Location> target) {
        this.target = checkNotNull(target, "target");
        return this;
    }

    @Override
    public Builder height(VariableAmount height) {

        return this;
    }

    @Override
    public Builder reset() {
        this.count = VariableAmount.fixed(64);
        this.height = VariableAmount.baseWithRandomAddition(0, 128);
        this.target = RandomBlockPopulator.CAVE_LIQUIDS;
        this.block = BlockTypes.WATER.getDefaultState();
        return this;
    }

    @Override
    public RandomBlock build() throws IllegalStateException {
        RandomBlock pop = new RandomBlockPopulator(this.block, this.count, this.height, this.target);
        return pop;
    }

}
