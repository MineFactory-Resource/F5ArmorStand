package teamuni.net.f5armorstand;


import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Vector3f;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;


public final class F5ArmorStand extends JavaPlugin implements Listener {
    Map<UUID, EntityArmorStand> armorStands = new HashMap<>(); //아머스탠드 맵

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this); //이벤트 리스너 등록
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!player.hasPermission("armorstand.noshow")) {
                createArmorStand(player);
                updateArmorStand(player);
            }
        });
    }
    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!player.hasPermission("armorstand.noshow")) {//서버 리로드시 아머스탠드가 남아있어 아머스탠드가 2개이상 생기는것을 방지하기위해 기존 아머스탠드 제거
                armorStands.computeIfPresent(player.getUniqueId(), (k, v) -> {
                    ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutEntityDestroy(v.ae()));
                    return null;
                });
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {//플레이어 접속시 아머스탠드를 만들고 업데이트함.
        Player player = event.getPlayer();
        if (!player.hasPermission("armorstand.noshow")) {
            createArmorStand(event.getPlayer());
            updateArmorStand(event.getPlayer());
        }
    }


    @EventHandler
    public void onChangeDimension(PlayerChangedWorldEvent event) {//차원(지옥문을 통해 네더 월드로 가거나, 엔드 차원문을 통해 엔드 월드를 가거나 등.) 이동시 아머스탠드를 만들고 업데이트를함.
        Player player = event.getPlayer();
        if (!player.hasPermission("armorstand.noshow")) {
            createArmorStand(event.getPlayer());
            updateArmorStand(event.getPlayer());
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (!event.getPlayer().hasPermission("armorstand.noshow")) return;
        if (event.getTo() == null) return;
        if (event.getFrom().getWorld() == null) return;
        if (event.getFrom().getWorld().equals(event.getTo().getWorld())) return;
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            createArmorStand(event.getPlayer());
            updateArmorStand(event.getPlayer());
        });
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("armorstand.noshow")) { //플레이어 이동시
            updateArmorStand(event.getPlayer()); //플레이어가 움직일때 아머스탠드 업데이트.
        }
    }


    private void createArmorStand(Player player) {
        EntityArmorStand armorStand = new EntityArmorStand(EntityTypes.c, ((CraftWorld) player.getWorld()).getHandle()); //새로운 아머스탠드 생성
        Location loc = player.getLocation();
        armorStand.a(loc.getX(), loc.getY(), loc.getZ());
        armorStand.j(true); //투명하게 설정
        armorStand.t(true); //히트박스 제거
        armorStands.put(player.getUniqueId(), armorStand);
        ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutSpawnEntity(armorStand));
        ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutEntityEquipment(armorStand.ae(), Collections.singletonList(new Pair<>(EnumItemSlot.f, Items.pC.P_()))));
        ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutEntityMetadata(armorStand.ae(), armorStand.ai(), false));
    }

    private void updateArmorStand(Player player) {
        EntityArmorStand armorStand = armorStands.get(player.getUniqueId());
        if (armorStand == null) return;
        Location loc = player.getLocation();
        armorStand.a(loc.getX(), loc.getY(), loc.getZ());
        armorStand.o(loc.getYaw());
        armorStand.p(loc.getPitch());
        armorStand.a(new Vector3f(player.getLocation().getPitch(), 0, 0));
        ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutEntityMetadata(armorStand.ae(), armorStand.ai(), false));
        ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutEntityTeleport(armorStand));
    }
}