package net.teamuni.f5armorstand;


import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;

public final class F5ArmorStand extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("F5ArmorStand Enable. made by fade"); /* 플러그인 켜질때 실행되는 문구 */
        ArmorStandView(); /* ArmorStandView 함수 호출 */
    }

    public void ArmorStandView(PlayerMoveEvent e) {
        if(Bukkit.getServer().getOnlinePlayers().size() != 0) {
            for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                Location loc = p.getLocation();
                ArmorStand as = p.getWorld().spawn(loc, ArmorStand.class);
                as.setGravity(false);
                as.setVisible(false);
                as.setCustomNameVisible(false);
                as.setHelmet(new ItemStack(Material.GHAST_TEAR));
                if(p.isOnline()) {
                    as.teleport(loc.add(0, 1, 0));
                }
                else {
                    as.remove();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("F5ArmorStand Enable. made by fade"); /* 플러그인이 꺼질때 실행되는 문구 */
    }
}
