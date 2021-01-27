package pl.wolny.jungletablist;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.UUID;

import static net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY;


public class AddToTabList{
    public static String appendDigit(int number) {
        if(number < 10){
            return String.valueOf(number) + number;
        }else {
            return String.valueOf(number);
        }
    }
    public void addToTab(Player p, String name, String id) {
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
        PacketPlayOutPlayerInfo pack = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
        PlayerConnection connection = ((CraftPlayer)p.getPlayer()).getHandle().playerConnection;
        connection.sendPacket(pack);
        PacketPlayOutPlayerInfo ping = new PacketPlayOutPlayerInfo(UPDATE_LATENCY, entityPlayer);
        connection.sendPacket(ping);
        System.out.println(id + " " + name + " " +  entity.getUniqueId());
    }
}
