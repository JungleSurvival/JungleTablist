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
    public static JungleTabList main;
    public static Plugin getMain() {
        return main;
    }
    private static Economy econ = null;
    public static Economy getEconomy() {
        return econ;
    }
    protected boolean trueping = false;
    private static String skinvalue = "eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0=";
    private static String skinsignature = "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoGDYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w08U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDEJwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzEnfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhwq19/GxARg63ISGE8CKw=";

    @Override
    public void onEnable() {
        // Plugin startup logic
        if(!setupEconomy()){
            for (int i = 0; i < 6; i++) {
                System.out.println("Can not set economy - shuting down");

            }
            Bukkit.getPluginManager().disablePlugin(this);
        }
        main = this;
        ConfigMenager.genConfig();
        trueping = this.getConfig().getBoolean("trueping");
        Bukkit.getPluginManager().registerEvents(this, this);
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
        p.setPlayerListHeader(this.getConfig().getString("brand"));
        GenerateTabList generateTabList = new GenerateTabList();
        TablistMenager tablistMenager = new TablistMenager();
        generateTabList.gen(e.getPlayer(), trueping, skinvalue, skinsignature, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
        new BukkitRunnable()
        {
            public void run()
            {
                generateTabList.gen(p, trueping, skinvalue, skinsignature, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME);
            }
        }.runTaskTimerAsynchronously(this, 100, 100);
    }
    @EventHandler
    public void QuitEvent(PlayerQuitEvent e) throws IOException {
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
