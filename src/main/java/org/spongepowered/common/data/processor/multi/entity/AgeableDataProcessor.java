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
package org.spongepowered.common.data.processor.multi.entity;

import static org.spongepowered.common.data.util.DataUtil.getData;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityAgeable;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableAgeableData;
import org.spongepowered.api.data.manipulator.mutable.entity.AgeableData;
import org.spongepowered.common.data.manipulator.mutable.entity.SpongeAgeableData;
import org.spongepowered.common.data.processor.common.AbstractEntityDataProcessor;

import java.util.Map;
import java.util.Optional;

public class AgeableDataProcessor extends AbstractEntityDataProcessor<EntityAgeable, AgeableData, ImmutableAgeableData> {

    public AgeableDataProcessor() {
        super(EntityAgeable.class);
    }

    @Override
    protected AgeableData createManipulator() {
        return new SpongeAgeableData(0, false, true);
    }

    @Override
    protected boolean doesDataExist(EntityAgeable entity) {
        return true;
    }

    @Override
    protected boolean set(EntityAgeable entity, Map<Key<?>, Object> keyValues) {
        int age = (Integer) keyValues.get(Keys.AGE);
        boolean baby = (Boolean) keyValues.get(Keys.BABY);
        boolean adult = (Boolean) keyValues.get(Keys.ADULT);

        if (baby) {
            entity.setGrowingAge(-10);
        } else {
            entity.setGrowingAge(1);
        }

        entity.setGrowingAge(age);
        return true;
    }

    @Override
    protected Map<Key<?>, ?> getValues(EntityAgeable entity) {
        final int age = entity.getGrowingAge();
        final boolean baby = entity.isChild();
        final boolean adult = !entity.isChild();
        return ImmutableMap.<Key<?>, Object>of(Keys.AGE, age, Keys.BABY, baby, Keys.ADULT, adult);
    }

    @Override
    public Optional<AgeableData> fill(DataContainer container, AgeableData ageableData) {
        if (!container.contains(Keys.AGE.getQuery()) || !container.contains(Keys.BABY.getQuery()) || !container.contains(Keys.ADULT.getQuery())) {
            return Optional.empty();
        }
        ageableData.set(Keys.AGE, getData(container, Keys.AGE));
        ageableData.set(Keys.BABY, getData(container, Keys.BABY));
        ageableData.set(Keys.ADULT, getData(container, Keys.ADULT));
        return Optional.of(ageableData);
    }

    @Override
    public DataTransactionResult remove(DataHolder dataHolder) {
        return DataTransactionResult.failNoData();
    }
}
