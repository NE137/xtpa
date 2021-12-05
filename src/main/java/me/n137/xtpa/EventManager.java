package me.n137.xtpa;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventManager implements Listener {
    public Xtpa plugin;

    EventManager(Xtpa p) {this.plugin = p;}

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        this.plugin.tpaRequests.remove(event.getPlayer().getUniqueId());
    }
}
