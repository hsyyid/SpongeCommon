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
package org.spongepowered.common.scheduler;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Functional;
import org.spongepowered.common.SpongeImpl;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpongeScheduler implements Scheduler {

    private static final SpongeScheduler INSTANCE = new SpongeScheduler();
    public static final int TICK_DURATION_MS = 50;
    public static final long TICK_DURATION_NS = TimeUnit.NANOSECONDS.convert(TICK_DURATION_MS, TimeUnit.MILLISECONDS);

    private final AsyncScheduler asyncScheduler;
    private final SyncScheduler syncScheduler;

    private SpongeScheduler() {
        this.asyncScheduler = new AsyncScheduler();
        this.syncScheduler = new SyncScheduler();
    }

    public static SpongeScheduler getInstance() {
        return INSTANCE;
    }

    @Override
    public Task.Builder createTaskBuilder() {
        return new SpongeTaskBuilder();
    }

    @Override
    public Optional<Task> getTaskById(UUID id) {
        Optional<Task> optTask = this.syncScheduler.getTask(id);
        if (optTask.isPresent()) {
            return optTask;
        }
        return this.asyncScheduler.getTask(id);
    }

    @Override
    public Set<Task> getTasksByName(String pattern) {
        Pattern searchPattern = Pattern.compile(checkNotNull(pattern, "pattern"));
        Set<Task> matchingTasks = this.getScheduledTasks();

        Iterator<Task> it = matchingTasks.iterator();
        while (it.hasNext()) {
            Matcher matcher = searchPattern.matcher(it.next().getName());
            if (!matcher.matches()) {
                it.remove();
            }
        }

        return matchingTasks;
    }

    @Override
    public Set<Task> getScheduledTasks() {
        Set<Task> allTasks = Sets.newHashSet();
        allTasks.addAll(this.asyncScheduler.getScheduledTasks());
        allTasks.addAll(this.syncScheduler.getScheduledTasks());
        return allTasks;
    }

    @Override
    public Set<Task> getScheduledTasks(boolean async) {
        if (async) {
            return this.asyncScheduler.getScheduledTasks();
        } else {
            return this.syncScheduler.getScheduledTasks();
        }
    }

    @Override
    public Set<Task> getScheduledTasks(Object plugin) {
        String testOwnerId = checkPluginInstance(plugin).getId();

        Set<Task> allTasks = this.getScheduledTasks();
        Iterator<Task> it = allTasks.iterator();

        while (it.hasNext()) {
            String taskOwnerId = it.next().getOwner().getId();
            if (!testOwnerId.equals(taskOwnerId)) {
                it.remove();
            }
        }

        return allTasks;
    }

    @Override
    public int getPreferredTickInterval() {
        return TICK_DURATION_MS;
    }

    @Override
    public SpongeExecutorService createSyncExecutor(Object plugin) {
        return new TaskExecutorService(() -> createTaskBuilder(), this.syncScheduler, checkPluginInstance(plugin));
    }

    @Override
    public SpongeExecutorService createAsyncExecutor(Object plugin) {
        return new TaskExecutorService(() -> createTaskBuilder().async(), this.asyncScheduler, checkPluginInstance(plugin));
    }

    /**
     * Check the object is a plugin instance.
     *
     * @param plugin The plugin to check
     * @return The plugin container of the plugin instance
     * @throws NullPointerException If the passed in plugin instance is null
     * @throws IllegalArgumentException If the object is not a plugin instance
     */
    static PluginContainer checkPluginInstance(Object plugin) {
        Optional<PluginContainer> optPlugin = SpongeImpl.getGame().getPluginManager().fromInstance(checkNotNull(plugin, "plugin"));
        checkArgument(optPlugin.isPresent(), "Provided object is not a plugin instance");
        return optPlugin.get();
    }

    private SchedulerBase getDelegate(Task task) {
        if (task.isAsynchronous()) {
            return this.asyncScheduler;
        } else {
            return this.syncScheduler;
        }
    }

    private SchedulerBase getDelegate(ScheduledTask.TaskSynchronicity syncType) {
        if (syncType == ScheduledTask.TaskSynchronicity.ASYNCHRONOUS) {
            return this.asyncScheduler;
        } else {
            return this.syncScheduler;
        }
    }

    String getNameFor(PluginContainer plugin, ScheduledTask.TaskSynchronicity syncType) {
        return getDelegate(syncType).nextName(plugin);
    }

    void submit(ScheduledTask task) {
        getDelegate(task).addTask(task);
    }

    /**
     * Ticks the synchronous scheduler.
     */
    public void tickSyncScheduler() {
        this.syncScheduler.tick();
    }

    public <T> CompletableFuture<T> submitAsyncTask(Callable<T> callable) {
        return Functional.asyncFailableFuture(callable, this.asyncScheduler.getExecutor());
    }
}
