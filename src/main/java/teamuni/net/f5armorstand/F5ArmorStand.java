package teamuni.net.f5armorstand;

import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.ArrayList;

public final class F5ArmorStand extends JavaPlugin implements Listener {
    private List<EntityArmorStand> armorStands = new ArrayList<>();

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("F5ArmorStand Enable. made by fade"); /* 플러그인 켜질때 실행되는 문구 */
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void ArmorStandView(PlayerMoveEvent e) {
            for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
                Location loc = p.getLocation();
                ArmorStand as = p.getWorld().spawn(loc, ArmorStand.class);
                as.setGravity(false);
                as.setVisible(false);
                as.setCustomNameVisible(false);
                as.getEquipment().setHelmet(new ItemStack(Material.GHAST_TEAR));
                as.teleport(loc.add(0, 1, 0));
                as.remove();
            }
        }



    @Override
    public void onDisable() {
        Bukkit.getLogger().info("F5ArmorStand Enable. made by fade"); /* 플러그인이 꺼질때 실행되는 문구 */
    }
}
