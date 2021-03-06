package pl.wolny.jungletablist;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.lang.reflect.Array;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

public class GenerateTabList {

    public void gen(Player p, boolean trueping, String skinvalue, String skinsignature, PacketPlayOutPlayerInfo.EnumPlayerInfoAction type) {
        if (p != null) {
            p.setPlayerListFooter("§7Twoje saldo wynosi: §r§a$" + (int)JungleTabList.getEconomy().getBalance(p));
            TablistMenager menager = new TablistMenager();
            menager.modifyTablist(p, "§a§lGracze §f" + JungleTabList.getMain().getServer().getOnlinePlayers().size() + "§8/§7" + JungleTabList.getMain().getServer().getMaxPlayers(), "00", 9999, skinvalue, skinsignature, type);
            menager.modifyTablist(p, " ", "01", 9999, skinvalue, skinsignature, type);
            File f = new File("plugins/JungleTabList/playerdata.yml");
            YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(f);
            List<String> YamlUsers = yamlFile.getStringList("users");
            //List<String> SortYamlUsers = YamlUsers.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            //System.out.println(YamlUsers.toString());
            int i = 2;
            int outoflimit = 0;
            boolean isOutOfLimit = false;
            List<PlayerObject> Queue = new ArrayList<>();
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
                    GameProfile playerProfile = ((CraftPlayer) Bukkit.getPlayer(str)).getHandle().getProfile();
                    Collection<Property> textures = playerProfile.getProperties().get("textures");
                    String signature = null;
                    String value = null;
                    for (Property property: textures) {
                        signature = property.getSignature();
                        value = property.getValue();
                    }
                    if(!trueping){
                        PlayerObjectList.add(new PlayerObject((int)JungleTabList.getEconomy().getBalance(Bukkit.getPlayer(str)), "§3" + (int)JungleTabList.getEconomy().getBalance(Bukkit.getPlayer(str)) +  " §a" + str, "0", 0, 9999, new String[]{value, signature}));
                    }else{
                        //System.out.println("pong=true, ping=" + ((CraftPlayer)Bukkit.getPlayer(str)).getHandle().ping);
                        PlayerObjectList.add(new PlayerObject((int)JungleTabList.getEconomy().getBalance(Bukkit.getPlayer(str)), "§3" + (int)JungleTabList.getEconomy().getBalance(Bukkit.getPlayer(str)) +  " §a" + str, "0", 0, ((CraftPlayer)Bukkit.getPlayer(str)).getHandle().ping, new String[]{value, signature}));
                    }

                    //System.out.println(i);
                }else {
                    OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(str);
                    if(player != null){
                        Queue.add(new PlayerObject(JungleTabList.getEconomy().getBalance(player), "§7" + (int)JungleTabList.getEconomy().getBalance(player) + " §8" + str, "0", 0, 0, new String[]{"", ""}));
                    }
                }
            }
            PlayerObjectList.sort(Comparator.comparingDouble(PlayerObject::getHajs).reversed());
            for (PlayerObject object: PlayerObjectList) {
                if (i < 10) {
                    //System.out.println(object.getPing());
                    //System.out.println(object.getName() + " ONLINE");
                    menager.modifyTablist(p, object.getName(), "0" + i, object.getPing(), object.getskin()[0], object.getskin()[1],  type);
                    i++;
                }else {
                    //System.out.println(object.getName() + " ONLINE");
                    menager.modifyTablist(p, object.getName(), String.valueOf(i), object.getPing(), object.getskin()[0], object.getskin()[1],  type);
                    i++;
                }

                //System.out.println("!!! " + object.getName() + " " + object.getHajs());
            }
            Queue.sort(Comparator.comparingDouble(PlayerObject::getHajs).reversed());
            //System.out.println(Queue.toString());
            for (PlayerObject str: Queue) {
                if(i>38){
                    isOutOfLimit = true;
                    outoflimit++;
                    //System.out.println(isOutOfLimit);
                    continue;
                }
                //
                if (i < 10) {
                    menager.modifyTablist(p, str.getName(), "0" + i, 9999, skinvalue, skinsignature, type);
                    //System.out.println(i);
                    i++;
                } else {
                    menager.modifyTablist(p, str.getName(), String.valueOf(i), 9999, skinvalue, skinsignature, type);
                    //System.out.println(i);
                    i++;
                }
            }
            if(isOutOfLimit) {
                menager.modifyTablist(p, "... I " + outoflimit + " jeszcze ...", String.valueOf(i), 9999, skinvalue, skinsignature, type);
                i++;
            }
            while (i < 60){
                menager.modifyTablist(p, " ", String.valueOf(i), 9999, skinvalue, skinsignature,  type);
                i++;
            }
            menager.modifyTablist(p, "§a§lŚmierci", String.valueOf(i), 9999, skinvalue, skinsignature,  type);
            i++;
            menager.modifyTablist(p, " ", String.valueOf(i), 9999, skinvalue, skinsignature, type);
            i++;
            List<PlayerObject> deathList = new ArrayList<>();
            for (String str: YamlUsers) {
                if(Bukkit.getPlayer(str) != null && Bukkit.getPlayer(str).isOnline()){
                    deathList.add(new PlayerObject(0, "§3" + Bukkit.getPlayer(str).getStatistic(Statistic.DEATHS) + " §7" + str, String.valueOf(i), Bukkit.getPlayer(str).getStatistic(Statistic.DEATHS), 9999, new String[]{skinvalue, skinsignature}));
                }else {
                    OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(str);
                    if(player != null){
                        deathList.add(new PlayerObject(0, "§3" + player.getStatistic(Statistic.DEATHS) + " §7" + str, String.valueOf(i), player .getStatistic(Statistic.DEATHS), 9999, new String[]{skinvalue, skinsignature}));
                    }
                }
            }
            deathList.sort(Comparator.comparingDouble(PlayerObject::getDeaths).reversed());
            for (PlayerObject object: deathList) {
                if(i>79){
                    break;
                }
                //System.out.println(Arrays.toString(object.getskin()));
                menager.modifyTablist(p, object.getName(), String.valueOf(i), 9999, object.getskin()[0], object.getskin()[1],  type);
                i++;
            }
            while (i != 80){
                menager.modifyTablist(p, " ", String.valueOf(i), 9999, skinvalue, skinsignature, type);
                i++;
            }
        }
    }
}