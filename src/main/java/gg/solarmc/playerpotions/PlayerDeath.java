package gg.solarmc.playerpotions;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerDeath implements Listener {
    private final PlayerPotions plugin;
    private final List<PotionEffectType> includedEffects;

    public PlayerDeath(PlayerPotions plugin) {
        this.plugin = plugin;
        this.includedEffects = plugin.getPotionEffects();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        List<PotionEffect> effects = player.getActivePotionEffects().stream()
                .filter(it -> includedEffects.contains(it.getType()))
                .sorted(Comparator.comparingInt(it -> includedEffects.indexOf(it.getType())))
                .collect(Collectors.toList());

        if (effects.isEmpty())
            return;

        ItemStack playerPotion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) playerPotion.getItemMeta();

        final PotionEffectType topPotionType = effects.get(0).getType();
        final String potionDisplayName = plugin.getConfig().getString("displayName")
                .replace("{name}", player.getName())
                .replace("{displayName}", player.getDisplayName())
                .replace("{potionColor}", getColor(topPotionType).toString());

        potionMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', potionDisplayName));

        Color color = topPotionType.getColor();
        potionMeta.setColor(color);
        effects.forEach(it -> potionMeta.addCustomEffect(it.withColor(color), true));

        playerPotion.setItemMeta(potionMeta);
        Location location = player.getLocation();
        location.getWorld().dropItemNaturally(location, playerPotion);
    }

    @SuppressWarnings("deprecation")
    private ChatColor getColor(PotionEffectType effect) {
        return switch (effect.getId()) {
            case 1 -> ChatColor.AQUA;
            case 12 -> ChatColor.GOLD;
            case 5 -> ChatColor.RED;
            default -> ChatColor.WHITE;
        };
    }
}
