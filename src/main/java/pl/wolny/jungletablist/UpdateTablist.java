package pl.wolny.jungletablist;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME;
import static net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY;

public class UpdateTablist {
    public void updateTab(Player p, String name, String id) {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld)p.getWorld()).getHandle();
        String UUID_PATTERN = "00000000-0000-%s-0000-000000000000";
        GameProfile profile = new GameProfile(UUID.fromString(String.format(UUID_PATTERN, id)), "");
        PlayerInteractManager interactManager = new PlayerInteractManager(nmsWorld);
        EntityPlayer entityPlayer = new EntityPlayer(nmsServer, nmsWorld, profile, interactManager);
        CraftPlayer entity = entityPlayer.getBukkitEntity();
        entity.setPlayerListName(name);
        entity.setGameMode(GameMode.ADVENTURE);
        entity.getHandle().ping = 9999;
        PacketPlayOutPlayerInfo pack = new PacketPlayOutPlayerInfo(UPDATE_DISPLAY_NAME, entityPlayer);
        PlayerConnection connection = ((CraftPlayer)p.getPlayer()).getHandle().playerConnection;
        connection.sendPacket(pack);
        PacketPlayOutPlayerInfo ping = new PacketPlayOutPlayerInfo(UPDATE_LATENCY, entityPlayer);
        connection.sendPacket(ping);
        //System.out.println(id + " " + name + " " +  entity.getUniqueId());
    }
    public void update(Player p){
            gen(p);
        }
    public void gen(Player p) {
        if (p != null) {
            updateTab(p, "§a§lGracze §f" + JungleTabList.getMain().getServer().getOnlinePlayers().size() + "§8/§7" + JungleTabList.getMain().getServer().getMaxPlayers(), "00");
            updateTab(p, " ", "01");
            File f = new File("plugins/JungleTabList/playerdata.yml");
            YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(f);
            List<String> YamlUsers = yamlFile.getStringList("users");
            Komparator komp = new Komparator();
            //List<String> SortYamlUsers = YamlUsers.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            //System.out.println(YamlUsers.toString());
            int i = 2;
            int outoflimit = 0;
            boolean isOutOfLimit = false;
            List<String> Queue = new ArrayList<>();
            List<PlayerObject> PlayerObjectList = new ArrayList<>();
            for (String str: YamlUsers) {
                if(i>38){
                    isOutOfLimit = true;
                    outoflimit++;
                    //System.out.println(isOutOfLimit);
                    continue;
                }
                //
                if(Bukkit.getPlayer(str) != null && Bukkit.getPlayer(str).isOnline()){
                    //menager.addToTab(p, "§3" + (int)JungleTabList.getEconomy().getBalance(Bukkit.getPlayer(str)) +  " §a" + str, "0" + i);
                    PlayerObjectList.add(new PlayerObject((int)JungleTabList.getEconomy().getBalance(Bukkit.getPlayer(str)), "§3" + (int)JungleTabList.getEconomy().getBalance(Bukkit.getPlayer(str)) +  " §a" + str, "0", 0));
                    //System.out.println(i);
                }else {
                    OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(str);
                    if(player != null){
                        Queue.add("§7" + (int)JungleTabList.getEconomy().getBalance(player) + " §8" + str);
                    }
                }
            }
            PlayerObjectList.sort(Comparator.comparingDouble(PlayerObject::getHajs).reversed());
            for (PlayerObject object: PlayerObjectList) {
                if (i < 10) {
                    updateTab(p, object.getName(), "0" + i);
                    i++;
                }else {
                    updateTab(p, object.getName(), String.valueOf(i));
                    i++;
                }

                //tem.out.println("!!! " + object.getName() + " " + object.getHajs());
            }
            java.util.Collections.sort(Queue);
            for (String str: Queue) {
                if(i>38){
                    isOutOfLimit = true;
                    outoflimit++;
                    System.out.println(isOutOfLimit);
                    continue;
                }
                //
                if (i < 10) {
                    updateTab(p, str, "0" + i);
                    //System.out.println(i);
                    i++;
                } else {
                    updateTab(p, str, String.valueOf(i));
                    //System.out.println(i);
                    i++;
                }
            }
            if(isOutOfLimit) {
                updateTab(p, "... I " + outoflimit + " jeszcze ...", String.valueOf(i));
                i++;
            }
            while (i < 60){
                updateTab(p, " ", String.valueOf(i));
                i++;
            }
            updateTab(p, "§a§lŚmierci", String.valueOf(i));
            i++;
            updateTab(p, " ", String.valueOf(i));
            i++;
            List<PlayerObject> deathList = new ArrayList<>();
            for (String str: YamlUsers) {
                if(Bukkit.getPlayer(str) != null && Bukkit.getPlayer(str).isOnline()){
                    deathList.add(new PlayerObject(0, "§3" + Bukkit.getPlayer(str).getStatistic(Statistic.DEATHS) + " §7" + str, String.valueOf(i), Bukkit.getPlayer(str).getStatistic(Statistic.DEATHS)));
                }else {
                    OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(str);
                    if(player != null){
                        deathList.add(new PlayerObject(0, "§3" + player.getStatistic(Statistic.DEATHS) + " §7" + str, String.valueOf(i), player .getStatistic(Statistic.DEATHS)));
                    }
                }
            }
            deathList.sort(Comparator.comparingDouble(PlayerObject::getDeaths).reversed());
            for (PlayerObject object: deathList) {
                if(i>79){
                    break;
                }
                updateTab(p, object.getName(), String.valueOf(i));
                i++;
            }
            while (i != 80){
                updateTab(p, " ", String.valueOf(i));
                i++;
            }
        }
    }
}
