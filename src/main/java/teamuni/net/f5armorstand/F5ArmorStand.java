package teamuni.net.f5armorstand;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Vector3f;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
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
            createArmorStand(player);
            updateArmorStand(player);
        }
    }
    @EventHandler
    public void onChangeDimension(PlayerChangedWorldEvent event) {//차원(지옥문을 통해 네더 월드로 가거나, 엔드 차원문을 통해 엔드 월드를 가거나 등.) 이동시 아머스탠드를 만들고 업데이트를함.
        Player player = event.getPlayer();
        if (!player.hasPermission("armorstand.noshow")) {
            createArmorStand(player);
            updateArmorStand(player);
        }
    }
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (event.getTo() == null) return;
        if (event.getFrom().getWorld() == null) return;
        if (event.getFrom().getWorld().equals(event.getTo().getWorld())) return;
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            if (!player.hasPermission("armorstand.noshow")) {
                createArmorStand(player);
                updateArmorStand(player);
            }
        });
    }
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("armorstand.noshow")) { //플레이어 이동시
            updateArmorStand(player); //플레이어가 움직일때 아머스탠드 업데이트.
        }
    }
    private void createArmorStand(Player player) {
        EntityArmorStand armorStand = new EntityArmorStand(EntityTypes.c, ((CraftWorld) player.getWorld()).getHandle()); //새로운 아머스탠드 생성
        EntityPlayer playerConnect = ((CraftPlayer) player).getHandle(); //변수 생성
        Entity armorStand2 = armorStand.getBukkitEntity(); //NMS entity를 Bukkit entity로 변환
        Location loc = player.getLocation();
        armorStand.a(loc.getX(), loc.getY(), loc.getZ()); //아머스탠드의 좌표 설정
        armorStand.j(true); //아머스탠드 투명하게 설정
        armorStand.t(true); //아머스탠드의 히트박스 제거
        armorStand.a(true); //소형 아머스탠드로 설정
        armorStands.put(player.getUniqueId(), armorStand);
        playerConnect.b.a( //플레이어 네트워크 연결에
                new PacketPlayOutSpawnEntity(armorStand) //아머스탠드 엔티티 스폰 패킷 전송
        );
        player.addPassenger(armorStand2);
        playerConnect.b.a( //플레이어 네트워크 연결에
                new PacketPlayOutEntityEquipment(armorStand.ae(), Collections.singletonList(new Pair<>(EnumItemSlot.f, Items.pC.P_()))) //아머스탠드 헤드에 가스트 눈물 장착 패킷 전송
        );
        playerConnect.b.a( //플레이어 네트워크 연결에
                new PacketPlayOutEntityMetadata(armorStand.ae(), armorStand.ai(), false) //메타데이터 업데이트 패킷 전송
        );
    }
    private void updateArmorStand(Player player) {
        EntityArmorStand armorStand = armorStands.get(player.getUniqueId());
        EntityPlayer playerConnect = ((CraftPlayer) player).getHandle(); //변수 생성
        if (armorStand == null) return;
        Location loc = player.getLocation();
        armorStand.a(loc.getX(), loc.getY(), loc.getZ());
        armorStand.o(loc.getYaw());
        armorStand.p(loc.getPitch());
        armorStand.a(new Vector3f(player.getLocation().getPitch(), 0, 0)); //아머스탠드 머리부분 벡터 설정
        playerConnect.b.a( //플레이어 네트워크 연결에
                new PacketPlayOutEntityMetadata(armorStand.ae(), armorStand.ai(), false) //메타데이터 업데이트 패킷 전송
        );
        playerConnect.b.a(
                new PacketPlayOutEntityHeadRotation(armorStand, (byte) MathHelper.d((loc.getYaw() * 256.0f)/ 360.0f))
        );
    }
}