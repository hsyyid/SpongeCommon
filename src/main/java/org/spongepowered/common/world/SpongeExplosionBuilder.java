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
package org.spongepowered.common.world;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.flowpowered.math.vector.Vector3d;
import net.minecraft.entity.Entity;
import org.spongepowered.api.entity.explosive.Explosive;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;
import org.spongepowered.common.interfaces.world.IMixinExplosion;

public class SpongeExplosionBuilder implements Explosion.Builder {

    private World world;
    private Explosive sourceExplosive;
    private float radius;
    private Vector3d origin;
    private boolean canCauseFire;
    private boolean shouldBreakBlocks;
    private boolean shouldSmoke;
    private boolean shouldDamageEntities;

    public SpongeExplosionBuilder() {
        reset();
    }

    @Override
    public Explosion.Builder world(World world) {
        this.world = checkNotNull(world, "world");
        return this;
    }

    @Override
    public Explosion.Builder sourceExplosive(Explosive source) {
        this.sourceExplosive = source;
        return this;
    }

    @Override
    public Explosion.Builder radius(float radius) {
        this.radius = radius;
        return this;
    }

    @Override
    public Explosion.Builder origin(Vector3d origin) {
        this.origin = checkNotNull(origin, "origin");
        return this;
    }

    @Override
    public Explosion.Builder canCauseFire(boolean fire) {
        this.canCauseFire = fire;
        return this;
    }

    @Override
    public Explosion.Builder shouldDamageEntities(boolean damage) {
        this.shouldDamageEntities = damage;
        return this;
    }

    @Override
    public Explosion.Builder shouldPlaySmoke(boolean smoke) {
        this.shouldSmoke = smoke;
        return this;
    }

    @Override
    public Explosion.Builder shouldBreakBlocks(boolean destroy) {
        this.shouldBreakBlocks = destroy;
        return this;
    }

    @Override
    public Explosion.Builder from(Explosion value) {
        this.world = value.getWorld();
        this.sourceExplosive = value.getSourceExplosive().orElse(null);
        this.radius = value.getRadius();
        this.origin = value.getOrigin();
        this.canCauseFire = value.canCauseFire();
        this.shouldBreakBlocks = value.shouldBreakBlocks();
        this.shouldSmoke = value.shouldPlaySmoke();
        this.shouldDamageEntities = value.shouldDamageEntities();
        return this;
    }

    @Override
    public SpongeExplosionBuilder reset() {
        this.world = null;
        this.sourceExplosive = null;
        this.radius = 0;
        this.origin = null;
        this.canCauseFire = false;
        this.shouldBreakBlocks = false;
        this.shouldSmoke = false;
        this.shouldDamageEntities = false;
        return this;
    }

    @Override
    public Explosion build() throws IllegalStateException {
        // TODO Check coordinates and if world is loaded here.
        checkState(this.world != null, "World is null!");
        checkState(this.origin != null, "Origin is null!");

        final net.minecraft.world.Explosion explosion = new net.minecraft.world.Explosion((net.minecraft.world.World) this.world,
                (Entity) this.sourceExplosive, this.origin.getX(), this.origin.getY(), this.origin.getZ(), this.radius,
                this.canCauseFire, this.shouldSmoke);
        ((IMixinExplosion) explosion).setShouldBreakBlocks(this.shouldBreakBlocks);
        ((IMixinExplosion) explosion).setShouldDamageEntities(this.shouldDamageEntities);
        return (Explosion) explosion;
    }
}
