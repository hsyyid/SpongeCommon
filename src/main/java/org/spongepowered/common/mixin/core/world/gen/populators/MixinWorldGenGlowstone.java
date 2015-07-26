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

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.spongepowered.api.util.VariableAmount;
import org.spongepowered.api.world.Chunk;

import java.util.Random;

import net.minecraft.world.gen.feature.WorldGenGlowStone1;
import org.spongepowered.api.world.gen.populator.Glowstone;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldGenGlowStone1.class)
public class MixinWorldGenGlowstone implements Glowstone {

    private VariableAmount height;
    private VariableAmount count;
    private VariableAmount attempts;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    public void onConstructed(CallbackInfo ci) {
        this.count = VariableAmount.baseWithRandomAddition(1, 10);
        this.attempts = VariableAmount.fixed(1500);
        this.height = VariableAmount.baseWithRandomAddition(0, 12);
    }

    @Override
    public void populate(Chunk chunk, Random random) {
        BlockPos position = new BlockPos(chunk.getBlockMin().getX(), chunk.getBlockMin().getY(), chunk.getBlockMin().getZ());
        World world = (World) chunk.getWorld();
        int n = this.count.getFlooredAmount(random);
        int x, y, z;

        for (int i = 0; i < n; i++) {
            x = random.nextInt(16) + 8;
            z = random.nextInt(16) + 8;
            y = random.nextInt(120) + 4;
            generate(world, random, position.add(x, y, z));
        }
    }

    @Overwrite
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        if (!worldIn.isAirBlock(position))
        {
            return false;
        }
        else if (worldIn.getBlockState(position.up()).getBlock() != Blocks.netherrack)
        {
            return false;
        }
        else
        {
            worldIn.setBlockState(position, Blocks.glowstone.getDefaultState(), 2);
            int a = this.attempts.getFlooredAmount(rand);
            for (int i = 0; i < a; ++i)
            {
                BlockPos blockpos1 =
                        position.add(rand.nextInt(8) - rand.nextInt(8), this.height.getFlooredAmount(rand), rand.nextInt(8) - rand.nextInt(8));

                if (worldIn.getBlockState(blockpos1).getBlock().getMaterial() == Material.air)
                {
                    int j = 0;
                    EnumFacing[] aenumfacing = EnumFacing.values();
                    int k = aenumfacing.length;

                    for (int l = 0; l < k; ++l)
                    {
                        EnumFacing enumfacing = aenumfacing[l];

                        if (worldIn.getBlockState(blockpos1.offset(enumfacing)).getBlock() == Blocks.glowstone)
                        {
                            ++j;
                        }

                        if (j > 1)
                        {
                            break;
                        }
                    }

                    if (j == 1)
                    {
                        worldIn.setBlockState(blockpos1, Blocks.glowstone.getDefaultState(), 2);
                    }
                }
            }

            return true;
        }
    }

    @Override
    public VariableAmount getClustersPerChunk() {
        return this.count;
    }

    @Override
    public void setClustersPerChunk(VariableAmount count) {
        this.count = count;
    }

    @Override
    public VariableAmount getAttemptsPerCluster() {
        return this.attempts;
    }

    @Override
    public void setAttemptsPerCluster(VariableAmount attempts) {
        this.attempts = attempts;
    }

    @Override
    public VariableAmount getHeightRange() {
        return this.height;
    }

    @Override
    public void setHeightRange(VariableAmount height) {
        this.height = height;
    }

}
