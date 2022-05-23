package net.teamuni.f5armorstand;


import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public final class F5ArmorStand extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("F5ArmorStand Enable. made by fade"); /* 플러그인 켜질때 실행되는 문구 */
        ArmorStandView(); /* ArmorStandView 함수 호출 */
    }

    @Override
    public void ArmorStandView();{     /* 아머스탠드 관련 함수, 플레이어 뒤에 있는 아머스탠드를 다루는 함수입니다! */
    }


    @Override
    public void onDisable() {
        Bukkit.getLogger().info("F5ArmorStand Enable. made by fade"); /* 플러그인이 꺼질때 실행되는 문구 */
    }
}
