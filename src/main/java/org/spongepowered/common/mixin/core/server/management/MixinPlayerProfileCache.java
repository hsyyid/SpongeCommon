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
package org.spongepowered.common.mixin.core.server.management;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.GameProfileCache;
import org.spongepowered.api.util.GuavaCollectors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.common.interfaces.server.management.IMixinPlayerProfileCacheEntry;
import org.spongepowered.common.profile.callback.MapProfileLookupCallback;
import org.spongepowered.common.profile.callback.SingleProfileLookupCallback;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

@Mixin(PlayerProfileCache.class)
public abstract class MixinPlayerProfileCache implements GameProfileCache {

    @Shadow private Map<String, IMixinPlayerProfileCacheEntry> usernameToProfileEntryMap;
    @Shadow private Map<UUID, IMixinPlayerProfileCacheEntry> uuidToProfileEntryMap;
    @Shadow private LinkedList<com.mojang.authlib.GameProfile> gameProfiles;
    @Shadow abstract void addEntry(com.mojang.authlib.GameProfile profile, @Nullable Date expiry);
    @Nullable @Shadow public abstract com.mojang.authlib.GameProfile getProfileByUUID(UUID uniqueId);
    @Shadow public abstract void save();

    @Override
    public boolean add(GameProfile profile, boolean overwrite, @Nullable Date expiry) {
        checkNotNull(profile, "profile");

        // Don't attempt to overwrite entries if we aren't requested to do so
        if (this.uuidToProfileEntryMap.containsKey(profile.getUniqueId()) && !overwrite) {
            return false;
        }

        this.addEntry((com.mojang.authlib.GameProfile) profile, expiry);

        return true;
    }

    @Override
    public Optional<GameProfile> getById(UUID uniqueId) {
        return Optional.ofNullable((GameProfile) this.getProfileByUUID(checkNotNull(uniqueId, "unique id")));
    }

    @Override
    public Map<UUID, Optional<GameProfile>> getByIds(Iterable<UUID> uniqueIds) {
        checkNotNull(uniqueIds, "unique ids");

        Map<UUID, Optional<GameProfile>> result = Maps.newHashMap();

        for (UUID uniqueId : uniqueIds) {
            result.put(uniqueId, Optional.ofNullable((GameProfile) this.getProfileByUUID(uniqueId)));
        }

        return result.isEmpty() ? ImmutableMap.of() : ImmutableMap.copyOf(result);
    }

    @Override
    public Optional<GameProfile> lookupById(UUID uniqueId) {
        checkNotNull(uniqueId, "unique id");

        com.mojang.authlib.GameProfile profile = this.getServer().getMinecraftSessionService().fillProfileProperties(new com.mojang.authlib.GameProfile(uniqueId, ""), true);
        if (profile != null && profile.getName() != null && !profile.getName().isEmpty()) {
            return Optional.of((GameProfile) profile);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Map<UUID, Optional<GameProfile>> lookupByIds(Iterable<UUID> uniqueIds) {
        checkNotNull(uniqueIds, "unique ids");

        Map<UUID, Optional<GameProfile>> result = Maps.newHashMap();

        MinecraftSessionService service = this.getServer().getMinecraftSessionService();
        for (UUID uniqueId : uniqueIds) {
            com.mojang.authlib.GameProfile profile = service.fillProfileProperties(new com.mojang.authlib.GameProfile(uniqueId, ""), true);
            if (profile != null && profile.getName() != null && !profile.getName().isEmpty()) {
                result.put(uniqueId, Optional.of((GameProfile) profile));
            } else {
                result.put(uniqueId, Optional.empty());
            }
        }

        return result.isEmpty() ? ImmutableMap.of() : ImmutableMap.copyOf(result);
    }

    @Override
    public Optional<GameProfile> getOrLookupById(UUID uniqueId) {
        Optional<GameProfile> profile = this.getById(uniqueId);
        if (profile.isPresent()) {
            return profile;
        } else {
            return this.lookupById(uniqueId);
        }
    }

    @Override
    public Map<UUID, Optional<GameProfile>> getOrLookupByIds(Iterable<UUID> uniqueIds) {
        checkNotNull(uniqueIds, "unique ids");

        Collection<UUID> pending = Sets.newHashSet(uniqueIds);
        Map<UUID, Optional<GameProfile>> result = Maps.newHashMap();

        result.putAll(this.getByIds(pending));
        result.forEach((uniqueId, profile) -> {
            if (profile.isPresent()) {
                pending.remove(uniqueId);
            }
        });
        result.putAll(this.lookupByIds(pending));

        return ImmutableMap.copyOf(result);
    }

    @Override
    public Optional<GameProfile> getByName(String name) {
        return Optional.ofNullable((GameProfile) this.getByNameNoLookup(checkNotNull(name, "name")));
    }

    @Override
    public Map<String, Optional<GameProfile>> getByNames(Iterable<String> names) {
        checkNotNull(names, "names");

        Map<String, Optional<GameProfile>> result = Maps.newHashMap();

        for (String name : names) {
            result.put(name, Optional.ofNullable((GameProfile) this.getByNameNoLookup(name)));
        }

        return result.isEmpty() ? ImmutableMap.of() : ImmutableMap.copyOf(result);
    }

    @Override
    public Optional<GameProfile> lookupByName(String name) {
        SingleProfileLookupCallback callback = new SingleProfileLookupCallback();

        this.getServer().getGameProfileRepository().findProfilesByNames(new String[]{name}, Agent.MINECRAFT, callback);

        return callback.getResult();
    }

    @Override
    public Map<String, Optional<GameProfile>> lookupByNames(Iterable<String> names) {
        checkNotNull(names, "names");

        Map<String, Optional<GameProfile>> result = Maps.newHashMap();

        this.getServer().getGameProfileRepository().findProfilesByNames(Iterables.toArray(names, String.class), Agent.MINECRAFT, new MapProfileLookupCallback(result));

        return result.isEmpty() ? ImmutableMap.of() : ImmutableMap.copyOf(result);
    }

    @Override
    public Optional<GameProfile> getOrLookupByName(String name) {
        Optional<GameProfile> profile = this.getByName(name);
        if (profile.isPresent()) {
            return profile;
        } else {
            return this.lookupByName(name);
        }
    }

    @Override
    public Map<String, Optional<GameProfile>> getOrLookupByNames(Iterable<String> names) {
        checkNotNull(names, "names");

        Collection<String> pending = Sets.newHashSet(names);
        Map<String, Optional<GameProfile>> result = Maps.newHashMap();

        result.putAll(this.getByNames(pending));
        result.forEach((name, profile) -> {
            if (profile.isPresent()) {
                pending.remove(name);
            }
        });
        // lookupByNames can return a map with different keys than the names passes id
        // (in the case where a name it actually capitalized differently). Therefore,
        // lookupByName is used instead here.
        pending.forEach(name -> result.put(name, this.lookupByName(name)));

        return ImmutableMap.copyOf(result);
    }

    @Override
    public Optional<GameProfile> fillProfile(GameProfile profile, boolean signed) {
        checkNotNull(profile, "profile");

        return Optional.ofNullable((GameProfile) this.getServer().getMinecraftSessionService().fillProfileProperties((com.mojang.authlib.GameProfile) profile, signed));
    }

    @Override
    public Collection<GameProfile> getProfiles() {
        return this.usernameToProfileEntryMap.values().stream()
                .map(entry -> (GameProfile) entry.getGameProfile())
                .collect(GuavaCollectors.toImmutableSet());
    }

    @Override
    public Collection<GameProfile> match(String name) {
        final String search = checkNotNull(name, "name").toLowerCase(Locale.ROOT);

        return this.getProfiles().stream()
                .filter(profile -> profile.getName().isPresent())
                .filter(profile -> profile.getName().get().toLowerCase(Locale.ROOT).startsWith(search))
                .collect(GuavaCollectors.toImmutableSet());
    }

    @Redirect(method = "getGameProfile(Lnet/minecraft/server/MinecraftServer;Ljava/lang/String;)Lcom/mojang/authlib/GameProfile;",
            at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/GameProfileRepository;findProfilesByNames([Ljava/lang/String;"
                    + "Lcom/mojang/authlib/Agent;Lcom/mojang/authlib/ProfileLookupCallback;)V"))
    private static void onGetGameProfile(GameProfileRepository repository, String[] names, Agent agent, ProfileLookupCallback callback) {
        GameProfileCache cache = Sponge.getServer().getGameProfileManager().getCache();
        if (cache instanceof PlayerProfileCache) {
            repository.findProfilesByNames(names, agent, callback);
        } else {
            // The method we're redirecting into obtains the resulting GameProfile from
            // the callback here.
            callback.onProfileLookupSucceeded((com.mojang.authlib.GameProfile) cache.getOrLookupByName(names[0]).orElse(null));
        }
    }

    @Nullable
    public com.mojang.authlib.GameProfile getByNameNoLookup(String username) {
        @Nullable IMixinPlayerProfileCacheEntry entry = this.usernameToProfileEntryMap.get(username.toLowerCase(Locale.ROOT));

        if (entry != null && System.currentTimeMillis() >= entry.getExpirationDate().getTime()) {
            com.mojang.authlib.GameProfile profile = entry.getGameProfile();
            this.uuidToProfileEntryMap.remove(profile.getId());
            this.usernameToProfileEntryMap.remove(profile.getName().toLowerCase(Locale.ROOT));
            this.gameProfiles.remove(profile);
            entry = null;
        }

        if (entry != null) {
            com.mojang.authlib.GameProfile profile = entry.getGameProfile();
            this.gameProfiles.remove(profile);
            this.gameProfiles.addFirst(profile);
        }

        this.save();
        return entry == null ? null : entry.getGameProfile();
    }

    private MinecraftServer getServer() {
        return (MinecraftServer) Sponge.getServer();
    }

}
