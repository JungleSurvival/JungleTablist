package pl.wolny.jungletablist;

import com.mojang.authlib.GameProfile;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public final class JungleTabList extends JavaPlugin implements Listener {
    public Scoreboard sb;
    public Team t_usr;
    public Team t_player;
    private final Object[] profileCache = new Object[80];
    private static Constructor<?> gameProfileConstructor;
    private static final String UUID_PATTERN = "00000000-0000-%s-0000-000000000000";
    private static Constructor<?> PLAYER_INFO_CONSTRUCTOR;
    public static JungleTabList main;
    public static Plugin getMain() {
        return main;
    }
    private static Economy econ = null;
    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        if(!setupEconomy()){
            for (int i = 0; i < 6; i++) {
                System.out.println("Can not set economy - shuting down");
            }
        }
        main = this;
        Bukkit.getPluginManager().registerEvents(this, this);
        sb = Bukkit.getScoreboardManager().getMainScoreboard();
        for(Team team2 : sb.getTeams()) {
            team2.unregister();
        }

    }

    @Override
    public void onDisable() {
    }
    @EventHandler
    public void JoinEvent(PlayerJoinEvent e) throws IOException {
        sb = e.getPlayer().getScoreboard();
        for(Team team2 : sb.getTeams()) {
            team2.unregister();
        }
        Player p = e.getPlayer();
        sb = p.getScoreboard();
        Team players = sb.registerNewTeam("z_3");
        players.addEntry(e.getPlayer().getName());
        p.setScoreboard(sb);
        File f = new File("plugins/JungleTabList/playerdata.yml");
        YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(f);
        List<String> YamlUsers = yamlFile.getStringList("users");
        if(!(YamlUsers.contains(p.getName()))){
            YamlUsers.add(e.getPlayer().getName());
            yamlFile.set("users", YamlUsers);
        }
        yamlFile.save(f);
        GenerateTabList generateTabList = new GenerateTabList();
        UpdateTablist updateTablist = new UpdateTablist();
        generateTabList.gen(e.getPlayer());
        new BukkitRunnable()
        {
            public void run()
            {
                updateTablist.update(p);
            }
        }.runTaskTimerAsynchronously(this, 100, 100);
    }
    @EventHandler
    public void QuitEvent(PlayerQuitEvent e) throws IOException {
        File f = new File("plugins/JungleTabList/playerdata.yml");
        YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(f);
        yamlFile.set(e.getPlayer().getName() + ".online", "false");
        yamlFile.save(f);
        for (Team sb: e.getPlayer().getScoreboard().getTeams()) {
            sb.unregister();
        }
        e.getPlayer().setScoreboard(sb);
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
