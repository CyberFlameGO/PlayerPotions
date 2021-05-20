package gg.solarmc.playerpotions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public record PlayerPotionCommand(PlayerPotions plugin) implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("playerpotion.reload")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to reload this Plugin");
                return true;
            }

            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Reload Complete!");
        }
        return true;
    }
}
