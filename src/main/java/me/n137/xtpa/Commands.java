package me.n137.xtpa;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Commands {
    private Xtpa plugin;
    Commands(Xtpa p) {this.plugin = p;}

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        switch (cmd.getName().toLowerCase()) {
            case "tpa":
                if (!(args.length == 1)) {
                    sender.sendMessage(this.plugin.pluginPrefix + "§cCorrect usage: /tpa <username>");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[0]);
                Player init = (Player) sender;
                if (target == null) {
                    sender.sendMessage(this.plugin.pluginPrefix + "§cThe player" + args[0] + " is not online!");
                    return true;
                }

                if (this.plugin.tpaRequests.containsKey(target.getUniqueId())) {
                    init.sendMessage(this.plugin.pluginPrefix + " §7" + target.getName() + "§7 has already received a tpa request. Please try again later.");
                    return true;
                }

                if (this.plugin.tpaDisabled.containsKey(target.getUniqueId())) {
                    init.sendMessage(this.plugin.pluginPrefix + " §7" + target.getName() + "§c is not accepting tpa requests.");
                    return true;
                }

                TextComponent textMessage = composeTPAMessage(init);
                target.spigot().sendMessage(textMessage);
                this.plugin.addToMap(target.getUniqueId(), init.getUniqueId());
                init.sendMessage(this.plugin.pluginPrefix + "§aYou have sent a tpa request to " + target.getName() +".\n§7They have "+this.plugin.tpaTime+" seconds to accept it.");

                return true;
            case "tpaccept":
                Player init2 = (Player) sender;
                if (!(this.plugin.tpaRequests.containsKey(init2.getUniqueId()))) {
                    init2.sendMessage(this.plugin.pluginPrefix + "§cYou currently have no pending tpa requests.");
                    return true;
                }

                Player target2 = Bukkit.getPlayer(this.plugin.tpaRequests.get(init2.getUniqueId()));
                if (target2 == null) {
                    this.plugin.tpaRequests.remove(init2.getUniqueId());
                    init2.sendMessage(this.plugin.pluginPrefix + "§cThe person that was trying to teleport to you has logged off.");
                    return true;
                }
                init2.sendMessage(this.plugin.pluginPrefix + "§aTeleport request accepted, teleporting...");
                target2.sendMessage(this.plugin.pluginPrefix + "§a" +init2.getName() + "§a has accepted your tpa request.");

                target2.playSound(target2.getLocation(), Sound.ENTITY_SHULKER_TELEPORT, 100, 1);
                init2.playSound(target2.getLocation(), Sound.ENTITY_SHULKER_TELEPORT, 100, 1);

                target2.teleport(init2.getLocation());
                this.plugin.tpaRequests.remove(init2.getUniqueId());

                return true;
            case "tpdeny":
                Player init3 = (Player) sender;
                if (!(this.plugin.tpaRequests.containsKey(init3.getUniqueId()))) {
                    init3.sendMessage(this.plugin.pluginPrefix + "§cYou currently have no pending tpa requests.");
                    return true;
                }

                Player target3 = Bukkit.getPlayer(this.plugin.tpaRequests.get(init3.getUniqueId()));
                if (target3 == null) {
                    this.plugin.tpaRequests.remove(init3.getUniqueId());
                    init3.sendMessage(this.plugin.pluginPrefix + "§cThe person that was trying to teleport to you has logged off.");
                    return true;
                }
                init3.sendMessage(this.plugin.pluginPrefix + "§aTeleport request denied.");
                target3.sendMessage(this.plugin.pluginPrefix + "§c" + init3.getName() + "§c has denied your tpa request.");
                target3.playSound(target3.getLocation(), Sound.ENTITY_WANDERING_TRADER_NO, 100, 1);
                init3.playSound(target3.getLocation(), Sound.ENTITY_WANDERING_TRADER_NO, 100, 1);

                this.plugin.tpaRequests.remove(init3.getUniqueId());
                return true;
            // debug commands
            case "tptoggle":
                Player player4 = (Player) sender;
                if ((!this.plugin.tpaDisabled.containsKey(player4.getUniqueId()))) {
                    this.plugin.tpaDisabled.put(player4.getUniqueId(), true);
                    sender.sendMessage(this.plugin.pluginPrefix + "§cYou will no longer receive TPA requests");
                } else {
                    this.plugin.tpaDisabled.remove(player4.getUniqueId());
                    sender.sendMessage(this.plugin.pluginPrefix + "§aYou will now receive TPA requests");
                }
                return true;
            case "tpclear":
                if (sender.isOp()) {
                    int requests = this.plugin.tpaRequests.size();
                    this.plugin.tpaRequests.clear();
                    sender.sendMessage(this.plugin.pluginPrefix + "§cCleared §e" + requests + "§c pending requests.");
                } else { sender.sendMessage(this.plugin.pluginPrefix + "§cNo permission.");}
                return true;
            case "tpview":
                if (sender.isOp()) {
                    sender.sendMessage("§a To <-- From");
                    for (UUID key : this.plugin.tpaRequests.keySet()) {
                        sender.sendMessage("§7- " + key.toString() + " : " + this.plugin.tpaRequests.get(key));
                    }
                } else { sender.sendMessage(this.plugin.pluginPrefix + "§cNo permission.");}
                return true;
        }
     return true;
    }

    public TextComponent composeTPAMessage(Player sender){
        TextComponent message;

        message = new TextComponent(this.plugin.pluginPrefix + "§a" + sender.getName() + "§7 is trying to teleport to you§a\n" + this.plugin.pluginPrefix + "§7Would you like to ");


        TextComponent accept = new TextComponent("§a[Accept]§7");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));

        TextComponent or = new TextComponent("§7 or ");

        TextComponent decline = new TextComponent("§c[Deny]§7");
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));

        TextComponent message1 = new TextComponent("§7 this request?");
        message.addExtra(accept);
        message.addExtra(or);
        message.addExtra(decline);
        message.addExtra(message1);

        return message;
    }
}
