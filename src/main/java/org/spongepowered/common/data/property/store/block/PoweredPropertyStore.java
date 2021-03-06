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
package org.spongepowered.common.data.property.store.block;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.spongepowered.api.data.property.block.PoweredProperty;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.common.data.property.store.common.AbstractSpongePropertyStore;
import org.spongepowered.common.util.VecHelper;

import java.util.Optional;

public class PoweredPropertyStore extends AbstractSpongePropertyStore<PoweredProperty> {

    private static final PoweredProperty TRUE = new PoweredProperty(true);
    private static final PoweredProperty FALSE = new PoweredProperty(false);

    @Override
    public Optional<PoweredProperty> getFor(Location<World> location) {
        final BlockPos pos = VecHelper.toBlockPos(location);
        final net.minecraft.world.World world = (net.minecraft.world.World) location.getExtent();
        return Optional.of(world.isBlockPowered(pos) ? TRUE : FALSE);
    }

    @Override
    public Optional<PoweredProperty> getFor(Location<World> location, Direction direction) {
        final net.minecraft.world.World world = (net.minecraft.world.World) location.getExtent();
        final EnumFacing facing = toEnumFacing(direction);
        final boolean powered = world.getStrongPower(VecHelper.toBlockPos(location).offset(facing), facing) > 0;
        return Optional.of(powered ? TRUE : FALSE);
    }
}
