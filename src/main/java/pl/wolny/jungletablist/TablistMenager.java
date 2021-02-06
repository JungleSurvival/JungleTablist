package pl.wolny.jungletablist;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

import static net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY;

public class TablistMenager {
    public void modifyTablist(Player p, String name, String id, int ping ,PacketPlayOutPlayerInfo.EnumPlayerInfoAction type) {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld)p.getWorld()).getHandle();
        String UUID_PATTERN = "00000000-0000-%s-0000-000000000000";
        GameProfile profile = new GameProfile(UUID.fromString(String.format(UUID_PATTERN, id)), "");
        PlayerInteractManager interactManager = new PlayerInteractManager(nmsWorld);
        EntityPlayer entityPlayer = new EntityPlayer(nmsServer, nmsWorld, profile, interactManager);
        CraftPlayer entity = entityPlayer.getBukkitEntity();
        entity.setPlayerListName(name);
        entity.setGameMode(GameMode.ADVENTURE);
        entity.getHandle().ping = ping;
        PacketPlayOutPlayerInfo pack = new PacketPlayOutPlayerInfo(type, entityPlayer);
        PlayerConnection connection = ((CraftPlayer)p.getPlayer()).getHandle().playerConnection;
        connection.sendPacket(pack);
        PacketPlayOutPlayerInfo pingPacket = new PacketPlayOutPlayerInfo(UPDATE_LATENCY, entityPlayer);
        connection.sendPacket(pingPacket);
        //System.out.println(id + " " + name + " " +  entity.getUniqueId());
    }
    public void removeFromTab(Player p, String id) {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld)p.getWorld()).getHandle();
        String UUID_PATTERN = "00000000-0000-%s-0000-000000000000";
        GameProfile profile = new GameProfile(UUID.fromString(String.format(UUID_PATTERN, id)), "");
        PlayerInteractManager interactManager = new PlayerInteractManager(nmsWorld);
        EntityPlayer entityPlayer = new EntityPlayer(nmsServer, nmsWorld, profile, interactManager);
        CraftPlayer entity = entityPlayer.getBukkitEntity();
        entity.setGameMode(GameMode.ADVENTURE);
        PacketPlayOutPlayerInfo pack = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer);
        PlayerConnection connection = ((CraftPlayer)p.getPlayer()).getHandle().playerConnection;
        connection.sendPacket(pack);
    }
    public void clear(Player p){
        int i = 0;
        while (i != 80){
            if (i < 10) {
                removeFromTab(p, "0" + i);
                i++;
            }else {
                removeFromTab(p, String.valueOf(i));
                i++;
            }
        }
    }
}
