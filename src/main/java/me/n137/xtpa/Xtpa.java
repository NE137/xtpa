package me.n137.xtpa;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class Xtpa extends JavaPlugin {

    private Commands commands = null;
    public boolean debug = false;
    public String pluginPrefix = "§8[§c§lTPA§8] §8- ";
    public int tpaTime = 15;

    public HashMap<UUID, UUID> tpaRequests = new HashMap<>(); // target - init
    public HashMap<UUID, Boolean> tpaDisabled = new HashMap<>();
    public HashMap<UUID, HashMap<String, Location>> homeList = new HashMap<>();

    public Xtpa() {}

    public void addToMap(UUID target, UUID init) {
        this.tpaRequests.put(target, init);
        BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                tpaRequests.remove(target);
            }
        }, tpaTime*20L);
    }


    @Override
    public void onEnable() {
        // Plugin startup logic
        this.commands = new Commands(this);
        this.getServer().getPluginManager().registerEvents(new EventManager(this), this);
    }

    @Override
    public void onDisable() {
        this.tpaRequests.clear();
        // Plugin shutdown logic
    }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return this.commands.onCommand(sender, command, label, args);
    }

}
