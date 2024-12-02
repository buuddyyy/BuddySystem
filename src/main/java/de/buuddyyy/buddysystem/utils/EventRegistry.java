package de.buuddyyy.buddysystem.utils;

import de.buuddyyy.buddysystem.BuddySystemPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.util.function.Consumer;

public final class EventRegistry {

    private final BuddySystemPlugin plugin;

    public EventRegistry(BuddySystemPlugin plugin) {
        this.plugin = plugin;
    }

    public <T extends Event> void registerEvent(Class<T> clazz, Consumer<T> consumer) {
        registerEvent(clazz, EventPriority.NORMAL, consumer);
    }

    public <T extends Event> void registerEvent(Class<T> eventClazz, EventPriority eventPriority, Consumer<T> consumer) {
        Listener listener = new Listener() {};
        EventExecutor executor = (listener1, event) -> {
            if (eventClazz.isInstance(event)) {
                consumer.accept(eventClazz.cast(event));
            }
        };
        Bukkit.getPluginManager().registerEvent(eventClazz, listener, eventPriority, executor, this.plugin);
    }

}
