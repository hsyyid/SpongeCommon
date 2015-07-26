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

import net.minecraft.block.BlockPumpkin;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import org.spongepowered.api.util.VariableAmount;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.gen.populator.Pumpkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(WorldGenPumpkin.class)
public class MixinWorldGenPumpkin implements Pumpkin {

    private VariableAmount count;
    private double chance;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    public void onConstructed(CallbackInfo ci) {
        this.count = VariableAmount.fixed(10);
        this.chance = 0.1;
    }

    @Override
    public void populate(Chunk chunk, Random random) {
        if (random.nextDouble() < this.chance) {
            World world = (World) chunk.getWorld();
            int x = chunk.getBlockMin().getX() + 8 + random.nextInt(16);
            int z = chunk.getBlockMin().getZ() + 8 + random.nextInt(16);
            int height = world.getHeight(new BlockPos(x, 0, z)).getY();
            int y = chunk.getBlockMin().getY() + height < 1 ? 0 : random.nextInt(height * 2);
            generate(world, random, new BlockPos(x, y, z));
        }
    }

    @Overwrite
    public boolean generate(World worldIn, Random rand, BlockPos p_180709_3_)
    {
        int n = this.count.getFlooredAmount(rand);
        for (int i = 0; i < n; ++i)
        {
            BlockPos blockpos1 =
                    p_180709_3_.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos1) && worldIn.getBlockState(blockpos1.down()).getBlock() == Blocks.grass
                    && Blocks.pumpkin.canPlaceBlockAt(worldIn, blockpos1))
            {
                worldIn.setBlockState(blockpos1,
                        Blocks.pumpkin.getDefaultState().withProperty(BlockPumpkin.FACING, EnumFacing.Plane.HORIZONTAL.random(rand)), 2);
            }
        }

        return true;
    }

    @Override
    public VariableAmount getPumpkinsPerChunk() {
        return this.count;
    }

    @Override
    public void setPumpkinsPerChunk(VariableAmount count) {
        this.count = count;
    }

    @Override
    public double getPumpkinChance() {
        return this.chance;
    }

    @Override
    public void setPumpkinChance(double p) {
        this.chance = p;
    }

}
