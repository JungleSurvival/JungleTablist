package pl.wolny.jungletablist;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerObject{
    private double hajs;
    private String playerName;
    private OfflinePlayer player;
    private String id;
    private int deaths;
    private int ping;

    public PlayerObject(double hajs, String playerName, String id, int deaths, int pingnew){
        this.hajs = hajs;
        this.playerName = playerName;
        this.id = id;
        this.deaths = deaths;
        this.ping = pingnew;
    }

    double getHajs(){ return hajs; }
    String getName(){ return playerName; }
    String getId(){ return id; }
    int getDeaths(){return deaths;}
    int getPing(){
        return ping;
    }

}
