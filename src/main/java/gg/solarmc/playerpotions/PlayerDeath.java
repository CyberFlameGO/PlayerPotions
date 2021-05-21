package gg.solarmc.playerpotions;

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
    private final List<PotionEffectType> includedEffects;

    public PlayerDeath(List<PotionEffectType> includedEffects) {
        this.includedEffects = includedEffects;
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
        potionMeta.setDisplayName(String.format("%s's Effect", player.getDisplayName()));

        Color color = effects.get(0).getType().getColor();
        potionMeta.setColor(color);
        effects.forEach(it -> potionMeta.addCustomEffect(it.withColor(color), true));

        playerPotion.setItemMeta(potionMeta);
        Location location = player.getLocation();
        location.getWorld().dropItemNaturally(location, playerPotion);
    }
}
