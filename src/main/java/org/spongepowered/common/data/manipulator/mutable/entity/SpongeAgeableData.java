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
package org.spongepowered.common.data.manipulator.mutable.entity;

import com.google.common.collect.ComparisonChain;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableAgeableData;
import org.spongepowered.api.data.manipulator.mutable.entity.AgeableData;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.common.data.manipulator.immutable.entity.ImmutableSpongeAgeableData;
import org.spongepowered.common.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.common.data.value.SpongeValueFactory;
import org.spongepowered.common.data.value.mutable.SpongeValue;

public class SpongeAgeableData extends AbstractData<AgeableData, ImmutableAgeableData> implements AgeableData {

    private int age;
    private boolean baby;
    private boolean adult;

    public SpongeAgeableData(int age, boolean baby, boolean adult) {
        super(AgeableData.class);
        this.age = age;
        this.baby = baby;
        this.adult = adult;
        registerGettersAndSetters();
    }

    @Override
    public AgeableData copy() {
        return new SpongeAgeableData(this.age, this.baby, this.adult);
    }

    @Override
    public ImmutableAgeableData asImmutable() {
        return new ImmutableSpongeAgeableData(this.age, this.baby, this.adult);
    }

    @Override
    public int compareTo(AgeableData o) {
        return ComparisonChain.start()
                .compare(o.age().get().intValue(), this.age)
                .compare(o.baby().get(), this.baby)
                .compare(o.adult().get(), this.adult)
                .result();
    }

    @Override
    public MutableBoundedValue<Integer> age() {
        return SpongeValueFactory.boundedBuilder(Keys.AGE)
                .minimum(Integer.MIN_VALUE)
                .maximum(Integer.MAX_VALUE)
                .defaultValue(0)
                .actualValue(this.age)
                .build();
    }

    @Override
    public Value<Boolean> baby() {
        return new SpongeValue<>(Keys.BABY, this.baby);
    }

    @Override
    public Value<Boolean> adult() {
        return new SpongeValue<>(Keys.ADULT, this.adult);
    }

    @Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(Keys.AGE, SpongeAgeableData.this::getAge);
        registerFieldSetter(Keys.AGE, SpongeAgeableData.this::setAge);
        registerKeyValue(Keys.AGE, SpongeAgeableData.this::age);

        registerFieldGetter(Keys.BABY, SpongeAgeableData.this::isBaby);
        registerFieldSetter(Keys.BABY, SpongeAgeableData.this::setBaby);
        registerKeyValue(Keys.BABY, SpongeAgeableData.this::baby);

        registerFieldGetter(Keys.ADULT, SpongeAgeableData.this::isAdult);
        registerFieldSetter(Keys.ADULT, SpongeAgeableData.this::setAdult);
        registerKeyValue(Keys.ADULT, SpongeAgeableData.this::adult);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public boolean isBaby() {
        return baby;
    }

    public void setBaby(boolean baby) {
        this.baby = baby;
    }
}
