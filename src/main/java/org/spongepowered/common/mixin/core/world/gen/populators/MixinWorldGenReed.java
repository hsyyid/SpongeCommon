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
package org.spongepowered.common.mixin.core.world.gen.populators;

import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.api.util.VariableAmount;
import org.spongepowered.api.world.Chunk;

import java.util.Random;

import net.minecraft.world.gen.feature.WorldGenReed;
import org.spongepowered.api.world.gen.populator.Reeds;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldGenReed.class)
public class MixinWorldGenReed implements Reeds {

    private VariableAmount count;
    private VariableAmount height;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    public void onConstructed(CallbackInfo ci) {
        this.count = VariableAmount.fixed(10);
        this.height = VariableAmount.baseWithRandomAddition(2, 2);
    }

    @Override
    public void populate(Chunk chunk, Random random) {
        World world = (World) chunk.getWorld();
        BlockPos position = new BlockPos(chunk.getBlockMin().getX(), chunk.getBlockMin().getY(), chunk.getBlockMin().getZ());
        int n = this.count.getFlooredAmount(random);
        int x = random.nextInt(16) + 8;
        int z = random.nextInt(16) + 8;
        int height = Math.min(world.getHeight(position.add(x, 0, z)).getY() * 2, 255);
        int y = height <= 1? 0 : random.nextInt(height);
        position = position.add(x, y, z);
        generate(world, random, position);
    }

    @Overwrite
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        int n = this.count.getFlooredAmount(rand);
        for (int i = 0; i < n; ++i) {
            BlockPos blockpos1 = position.add(rand.nextInt(4) - rand.nextInt(4), 0, rand.nextInt(4) - rand.nextInt(4));

            if (worldIn.isAirBlock(blockpos1)) {
                BlockPos blockpos2 = blockpos1.down();

                if (worldIn.getBlockState(blockpos2.west()).getBlock().getMaterial() == Material.water
                        || worldIn.getBlockState(blockpos2.east()).getBlock().getMaterial() == Material.water
                        || worldIn.getBlockState(blockpos2.north()).getBlock().getMaterial() == Material.water
                        || worldIn.getBlockState(blockpos2.south()).getBlock().getMaterial() == Material.water) {
                    int j = this.height.getFlooredAmount(rand);

                    for (int k = 0; k < j; ++k) {
                        if (Blocks.reeds.canBlockStay(worldIn, blockpos1)) {
                            worldIn.setBlockState(blockpos1.up(k), Blocks.reeds.getDefaultState(), 2);
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public VariableAmount getReedsPerChunk() {
        return this.count;
    }

    @Override
    public void setReedsPerChunk(VariableAmount count) {
        this.count = count;
    }

    @Override
    public VariableAmount getReedHeight() {
        return this.height;
    }

    @Override
    public void setReedHeight(VariableAmount height) {
        this.height = height;
    }

}
