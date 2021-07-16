package gg.solarmc.playerpotions;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
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
        final FileConfiguration config = plugin.getConfig();

        ItemStack playerPotion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) playerPotion.getItemMeta();
        PotionEffectType topPotionType = effects.get(0).getType();

        String potionDisplayName = config.getString("displayName")
                .replace("{name}", player.getName())
                .replace("{displayName}", player.getDisplayName())
                .replace("{potionColor}", getColor(topPotionType).toString());

        potionMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', potionDisplayName));

        Color color = topPotionType.getColor();
        potionMeta.setColor(color);
        List<String> lore = new LinkedList<>();
        effects.forEach(it -> {
            potionMeta.addCustomEffect(it.withColor(color), true);
            int amplifier = it.getAmplifier();
            lore.add(
                    ChatColor.translateAlternateColorCodes('&',
                            config.getString("potionlore")
                                    .replace("{name}",
                                            asTitleCase(getPotionType(it.getType()).toString().replaceAll("_", " ")) +
                                                    (amplifier == 0 ? "" : " " + amplifier))
                                    .replace("{duration}", formatDuration(it.getDuration()))
                                    .replace("{color}", getColor(it.getType()).toString()))
            );
        });
        potionMeta.setLore(lore);

        playerPotion.setItemMeta(potionMeta);
        Location location = player.getLocation();
        location.getWorld().dropItemNaturally(location, playerPotion);
    }

    private ChatColor getColor(PotionEffectType effect) {
        return switch (getPotionType(effect)) {
            case SPEED -> ChatColor.AQUA;
            case FIRE_RESISTANCE -> ChatColor.GOLD;
            case STRENGTH -> ChatColor.RED;
            default -> ChatColor.WHITE;
        };
    }

    private PotionType getPotionType(PotionEffectType type) {
        return Arrays.stream(PotionType.values())
                .filter(it -> it.getEffectType() != null)
                .filter(it -> it.getEffectType().equals(type))
                .findFirst()
                .orElse(PotionType.WATER);
    }

    private static String asTitleCase(String s) {
        return Arrays.stream(s.toLowerCase().split(" "))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
    }

    private static String formatDuration(int durationInTicks) {
        int seconds = durationInTicks / 20;

        int sec = seconds % 60;
        return seconds / 60 + ":" + (sec < 10 ? "0" + sec : sec);
    }
}
