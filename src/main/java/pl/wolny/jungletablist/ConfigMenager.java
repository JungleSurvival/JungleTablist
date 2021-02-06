package pl.wolny.jungletablist;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigMenager {
    public static void genConfig(){
        Plugin plugin = JungleTabList.getMain();
        FileConfiguration configuration = plugin.getConfig();
        configuration.addDefault("brand", "§cNAZWA TWOJEGO SERWERA");
        configuration.addDefault("trueping", true);
        configuration.options().copyDefaults(true);
        plugin.saveConfig();
        plugin.reloadConfig();
    }
}
