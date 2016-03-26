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
package org.spongepowered.common.mixin.core.tileentity;

import net.minecraft.tileentity.TileEntityBrewingStand;
import org.spongepowered.api.block.tileentity.carrier.BrewingStand;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.common.data.util.DataQueries;
import org.spongepowered.common.interfaces.data.IMixinCustomNameable;

@NonnullByDefault
@Mixin(TileEntityBrewingStand.class)
public abstract class MixinTileEntityBrewingStand extends MixinTileEntityLockable implements BrewingStand, IMixinCustomNameable {

    @Shadow private String customName;

    @Override
    public void sendDataToContainer(DataView dataView) {
        dataView.set(DataQueries.BLOCK_ENTITY_BREWING_TIME, this.getField(0));
        if (this.customName != null) {
            dataView.set(DataQueries.BLOCK_ENTITY_CUSTOM_NAME, this.customName);
        }
    }

    @Override
    public void setCustomDisplayName(String customName) {
        ((TileEntityBrewingStand) (Object) this).setName(customName);
    }

}
