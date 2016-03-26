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
package org.spongepowered.common.mixin.core.entity.passive;

import static com.google.common.base.Preconditions.checkState;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.village.MerchantRecipeList;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.Profession;
import org.spongepowered.api.entity.living.Humanoid;
import org.spongepowered.api.entity.living.Villager;
import org.spongepowered.api.item.merchant.TradeOffer;
import org.spongepowered.api.item.merchant.TradeOfferGenerator;
import org.spongepowered.api.item.merchant.VillagerRegistry;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.common.entity.EntityUtil;
import org.spongepowered.common.entity.SpongeCareer;
import org.spongepowered.common.entity.SpongeEntityMeta;
import org.spongepowered.common.interfaces.entity.IMixinVillager;
import org.spongepowered.common.mixin.core.entity.MixinEntityAgeable;
import org.spongepowered.common.registry.SpongeVillagerRegistry;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

@Mixin(EntityVillager.class)
@Implements(@Interface(iface = Villager.class, prefix = "villager$"))
public abstract class MixinEntityVillager extends MixinEntityAgeable implements Villager, IMixinVillager {

    @Shadow private boolean isPlaying;
    @Shadow private int careerId;
    @Shadow private int careerLevel;
    @Shadow @Nullable private MerchantRecipeList buyingList;

    @Shadow public abstract int getProfession();
    @Shadow public abstract void setProfession(int professionId);
    @Shadow public abstract void setCustomer(EntityPlayer player);
    @Shadow public abstract boolean shadow$isTrading();
    @Shadow public abstract EntityPlayer shadow$getCustomer();
    @Shadow public abstract MerchantRecipeList getRecipes(EntityPlayer player);

    private Profession profession;

    @Inject(method = "setProfession(I)V", at = @At("RETURN"))
    public void onSetProfession(int professionId, CallbackInfo ci) {
        this.profession = EntityUtil.validateProfession(professionId);
    }

    @Override
    public boolean isPlaying() {
        return this.isPlaying;
    }

    @Override
    public void setPlaying(boolean playing) {
        this.isPlaying = playing;
    }

    @Intrinsic
    public boolean villager$isTrading() {
        return shadow$isTrading();
    }

    @Override
    public Career getCareer() {
        List<Career> careers = (List<Career>) this.profession.getCareers();
        if (this.careerId == 0 || this.careerId > careers.size()) {
            this.careerId = new Random().nextInt(careers.size()) + 1;
        }
        this.getRecipes(null);
        return careers.get(this.careerId - 1);
    }

    @Override
    public void setCareer(Career career) {
        setProfession(((SpongeEntityMeta) career.getProfession()).type);
        this.buyingList = null;
        this.careerId = ((SpongeCareer) career).type + 1;
        this.careerLevel = 1;
        this.getRecipes(null);
    }

    @Override
    public Optional<Humanoid> getCustomer() {
        return Optional.ofNullable((Humanoid) this.shadow$getCustomer());
    }

    /**
     * @author gabizou - January 13th, 2016
     *
     * This overwrites the current method using the multi-dimension array with
     * our {@link VillagerRegistry} to handle career levels and registrations
     * for {@link TradeOfferGenerator}s. Note that this takes over entirely
     * whatever vanilla does, but this allows for maximum customization for
     * plugins to handle gracefully.
     */
    @SuppressWarnings("unchecked")
    @Overwrite
    public void populateBuyingList() {
        // Sponge
        List<Career> careers = (List<Career>) this.profession.getCareers();

        // EntityVillager.ITradeList[][][] aentityvillager$itradelist = DEFAULT_TRADE_LIST_MAP[this.getProfession()];

        if (this.careerId != 0 && this.careerLevel != 0) {
            ++this.careerLevel;
        } else {
            // Sponge change aentityvillager$itradelist to use this.profession.getCareers()
            this.careerId = this.rand.nextInt(careers.size()) + 1;
            this.careerLevel = 1;
        }

        if (this.buyingList == null) {
            this.buyingList = new MerchantRecipeList();
        }

        // Sponge start - use our own registry stuffs
        checkState(this.careerId <= careers.size(), "The villager career id is out of bounds fo the available Careers! Found: " + this.careerId
                                                    + " when the current maximum is: " + careers.size());
        final Career careerId = careers.get(this.careerId - 1);
        SpongeVillagerRegistry.getInstance().populateOffers((List<TradeOffer>) (List<?>) this.buyingList, careerId, this.careerLevel, this.rand);
        // Sponge end
    }

    @Override
    public void setCustomer(@Nullable Humanoid humanoid) {
        this.setCustomer((EntityPlayer) humanoid);
    }

}
