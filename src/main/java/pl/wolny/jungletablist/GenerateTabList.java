package pl.wolny.jungletablist;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.lang.reflect.Array;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

public class GenerateTabList {

    public void gen(Player p) {
        if (p != null) {
            AddToTabList menager = new AddToTabList();
            menager.addToTab(p, "§a§lGracze §f" + JungleTabList.getMain().getServer().getOnlinePlayers().size() + "§8/§7" + JungleTabList.getMain().getServer().getMaxPlayers(), "00");
            menager.addToTab(p, " ", "01");
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
                    menager.addToTab(p, object.getName(), "0" + i);
                    i++;
                }else {
                    menager.addToTab(p, object.getName(), String.valueOf(i));
                    i++;
                }

                System.out.println("!!! " + object.getName() + " " + object.getHajs());
            }
            java.util.Collections.sort(Queue);
            for (String str: Queue) {
                if(i>38){
                    isOutOfLimit = true;
                    outoflimit++;
                    //System.out.println(isOutOfLimit);
                    continue;
                }
                //
                if (i < 10) {
                    menager.addToTab(p, str, "0" + i);
                    //System.out.println(i);
                    i++;
                } else {
                    menager.addToTab(p, str, String.valueOf(i));
                    //System.out.println(i);
                    i++;
                }
            }
            if(isOutOfLimit) {
                menager.addToTab(p, "... I " + outoflimit + " jeszcze ...", String.valueOf(i));
                i++;
            }
            while (i < 60){
                menager.addToTab(p, " ", String.valueOf(i));
                i++;
            }
            menager.addToTab(p, "§a§lŚmierci", String.valueOf(i));
            i++;
            menager.addToTab(p, " ", String.valueOf(i));
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
                menager.addToTab(p, object.getName(), String.valueOf(i));
                i++;
            }
            while (i != 80){
                menager.addToTab(p, " ", String.valueOf(i));
                i++;
            }
        }
    }
}