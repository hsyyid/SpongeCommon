/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
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
package org.spongepowered.common.world.gen.populators;

import com.google.common.base.Predicate;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.util.VariableAmount;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.gen.populator.SeaFloor;

import java.util.Random;

public class SeaFloorPopulator implements SeaFloor {

    public static final Predicate<BlockState> DIRT_AND_GRASS = new Predicate<BlockState>() {

        @Override
        public boolean apply(BlockState input) {
            return input.getType() == BlockTypes.DIRT || input.getType() == BlockTypes.GRASS;
        }

    };

    private BlockState block;
    private VariableAmount radius;
    private VariableAmount count;
    private Predicate<BlockState> check;
    private VariableAmount depth;

    public SeaFloorPopulator() {
        this.block = BlockTypes.SAND.getDefaultState();
        this.radius = VariableAmount.fixed(7);
        this.count = VariableAmount.fixed(3);
        this.check = DIRT_AND_GRASS;
    }

    @Override
    public void populate(Chunk chunk, Random random) {
        World world = (World) chunk.getWorld();
        int n = this.count.getFlooredAmount(random);
        BlockPos position = new BlockPos(chunk.getBlockMin().getX(), chunk.getBlockMin().getY(), chunk.getBlockMin().getZ());
        for (int i = 0; i < n; i++) {
            BlockPos pos = position.add(random.nextInt(16), 0, random.nextInt(16));
            // This method is incorrectly named, it simply gets the top block
            // that blocks movement and isn't leaves
            pos = world.getTopSolidOrLiquidBlock(pos);

            if (world.getBlockState(pos).getBlock().getMaterial() != Material.water) {
                continue;
            }

            int radius = this.radius.getFlooredAmount(random);
            int depth = this.depth.getFlooredAmount(random);

            for (int x = pos.getX() - radius; x <= pos.getX() + radius; ++x)
            {
                int x0 = x - pos.getX();
                for (int z = pos.getZ() - radius; z <= pos.getZ() + radius; ++z)
                {
                    int z0 = z - pos.getZ();

                    if (x0 * x0 + z0 * z0 <= radius * radius)
                    {
                        for (int y = pos.getY() - depth; y <= pos.getY() + depth; ++y)
                        {
                            BlockPos blockpos1 = new BlockPos(x, y, z);

                            if (this.check.apply((BlockState) world.getBlockState(blockpos1)))
                            {
                                world.setBlockState(blockpos1, (IBlockState) this.block, 2);
                            }
                        }
                    }
                }
            }

        }
    }

    @Override
    public BlockState getBlock() {
        return this.block;
    }

    @Override
    public void setBlock(BlockState block) {
        this.block = block;
    }

    @Override
    public VariableAmount getDiscsPerChunk() {
        return this.count;
    }

    @Override
    public void setDiscsPerChunk(VariableAmount count) {
        this.count = count;
    }

    @Override
    public VariableAmount getRadius() {
        return this.radius;
    }

    @Override
    public void setRadius(VariableAmount radius) {
        this.radius = radius;
    }

    @Override
    public Predicate<BlockState> getValidBlocksToReplace() {
        return this.check;
    }

    @Override
    public void setValidBlocksToReplace(Predicate<BlockState> check) {
        this.check = check;
    }

    @Override
    public VariableAmount getDepth() {
        return this.depth;
    }

    @Override
    public void setDepth(VariableAmount depth) {
        this.depth = depth;
    }

}
