package pl.wolny.jungletablist;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public class AddToTabList {
    public void addToTab(Player p, String name, int id, Boolean registerTeam, String TeamName, String playername) {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld)p.getWorld()).getHandle();
        GameProfile profile = new GameProfile(UUID.randomUUID(), playername);
        //p.setScoreboard(sb);
        PlayerInteractManager interactManager = new PlayerInteractManager(nmsWorld);
        EntityPlayer entityPlayer = new EntityPlayer(nmsServer, nmsWorld, profile, interactManager);
        Player entity = entityPlayer.getBukkitEntity();
        Scoreboard sb = p.getScoreboard();
        if(registerTeam) {
            Team team = sb.registerNewTeam(TeamName);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < id+1; i++) {
                stringBuilder.append("§");
            }
            stringBuilder.append("r");
            String prefix = stringBuilder.toString();
            System.out.println(prefix);
            entity.setPlayerListName(prefix + name);
            entity.setCustomName(name);
            entity.setDisplayName(name);
            team.setPrefix(prefix);
            team.addEntry(String.valueOf(entity.getUniqueId()));

            p.setScoreboard(sb);
            PacketPlayOutPlayerInfo pack = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
            PlayerConnection connection = ((CraftPlayer)p.getPlayer()).getHandle().playerConnection;
            connection.sendPacket(pack);
        }else {
            Team team = sb.getTeam(String.valueOf(id));
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < id+1; i++) {
                stringBuilder.append("§");
            }
            stringBuilder.append("r");
            String prefix = stringBuilder.toString();
            System.out.println(prefix);
            entity.setPlayerListName(prefix + name);
            team.setPrefix(prefix);
            team.addEntry(String.valueOf(entity.getUniqueId()));
            p.setScoreboard(sb);
            PacketPlayOutPlayerInfo pack = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
            PlayerConnection connection = ((CraftPlayer)p.getPlayer()).getHandle().playerConnection;
            connection.sendPacket(pack);
        }
    }
}
