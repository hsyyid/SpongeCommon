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

import net.minecraft.world.World;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.gen.feature.WorldGenMelon;
import org.spongepowered.api.util.VariableAmount;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.gen.populator.Melons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(WorldGenMelon.class)
public class MixinWorldGenMelon implements Melons {
    
    private VariableAmount count;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    public void onConstructed(CallbackInfo ci) {
        this.count = VariableAmount.fixed(10);
    }

    @Override
    public void populate(Chunk chunk, Random random) {
        World world = (World) chunk.getWorld();
        BlockPos position = new BlockPos(chunk.getBlockMin().getX(), chunk.getBlockMin().getY(), chunk.getBlockMin().getZ());
        int n = this.count.getFlooredAmount(random);
        int x = random.nextInt(16) + 8;
        int z = random.nextInt(16) + 8;
        int y = random.nextInt(Math.min(world.getHeight(position.add(x, 0, z)).getY() * 2, 255));
        position = position.add(x, y, z);
        for (int i = 0; i < n; ++i)
        {
            BlockPos blockpos1 = position.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));

            if (Blocks.melon_block.canPlaceBlockAt(world, blockpos1) && world.getBlockState(blockpos1.down()).getBlock() == Blocks.grass)
            {
                world.setBlockState(blockpos1, Blocks.melon_block.getDefaultState(), 2);
            }
        }
    }

    @Override
    public VariableAmount getMelonsPerChunk() {
        return this.count;
    }

    @Override
    public void setMelonsPerChunk(VariableAmount count) {
        this.count = count;
    }

}
