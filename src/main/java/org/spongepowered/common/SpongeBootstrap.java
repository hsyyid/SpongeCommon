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
package org.spongepowered.common;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.rcon.RconService;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.service.whitelist.WhitelistService;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.common.command.SpongeCommand;
import org.spongepowered.common.command.SpongeHelpCommand;
import org.spongepowered.common.service.ban.SpongeBanService;
import org.spongepowered.common.service.pagination.SpongePaginationService;
import org.spongepowered.common.service.rcon.MinecraftRconService;
import org.spongepowered.common.service.sql.SqlServiceImpl;
import org.spongepowered.common.service.user.SpongeUserStorageService;
import org.spongepowered.common.service.whitelist.SpongeWhitelistService;
import org.spongepowered.common.text.action.SpongeCallbackHolder;

/**
 * Used to setup the ecosystem.
 */
@NonnullByDefault
public final class SpongeBootstrap {

    public static void initializeServices() {
        registerService(SqlService.class, new SqlServiceImpl());
        registerService(PaginationService.class, new SpongePaginationService());
        if (SpongeImpl.getGame().getPlatform().getType() == Platform.Type.SERVER) {
            registerService(RconService.class, new MinecraftRconService((DedicatedServer) MinecraftServer.getServer()));
        }
        registerService(UserStorageService.class, new SpongeUserStorageService());
        registerService(BanService.class, new SpongeBanService());
        registerService(WhitelistService.class, new SpongeWhitelistService());
        SpongeInternalListeners.getInstance().registerServiceCallback(PermissionService.class, input -> SpongeImpl.getGame().getServer().getConsole().getContainingCollection());
    }

    public static void initializeCommands() {
        Sponge.getCommandManager().register(SpongeImpl.getPlugin(), SpongeCommand.getCommand(), "sponge", "sp");
        Sponge.getCommandManager().register(SpongeImpl.getPlugin(), SpongeHelpCommand.create(), "help", "?");
        Sponge.getCommandManager().register(SpongeImpl.getPlugin(), SpongeCallbackHolder.getInstance().createCommand(), SpongeCallbackHolder.CALLBACK_COMMAND);
    }

    private static <T> void registerService(Class<T> serviceClass, T serviceImpl) {
        SpongeImpl.getGame().getServiceManager().setProvider(SpongeImpl.getPlugin(), serviceClass, serviceImpl);
    }
}
