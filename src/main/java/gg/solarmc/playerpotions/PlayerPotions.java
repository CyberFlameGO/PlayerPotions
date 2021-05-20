package gg.solarmc.playerpotions;

import org.bukkit.command.defaults.ReloadCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerPotions extends JavaPlugin {
    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        if (this.getConfig().getBoolean("enabled")) {
            List<PotionEffectType> effects = getPotionEffects();
            this.getServer().getPluginManager().registerEvents(new PlayerDeath(effects), this);
            this.getServer().getPluginCommand("ppotion").setExecutor(new PlayerPotionCommand(this));
        }
    }

    public List<PotionEffectType> getPotionEffects() {
        return this.getConfig().getStringList("potion")
                .stream().map(PotionEffectType::getByName)
                .collect(Collectors.toList());
    }

    @Override
    public void onDisable() {

    }
}
