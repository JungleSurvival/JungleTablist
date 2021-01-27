package pl.wolny.jungletablist;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Comparator;

public class Komparator implements Comparator<PlayerObject> {

    @Override
    public int compare(PlayerObject ob1, PlayerObject ob2) {
        //use instanceof to verify the references are indeed of the type in question
        if(ob1.getHajs() > ob2.getHajs()){
            return 1;
        }else {
            return -1;
        }
    }
    Comparator<PlayerObject> compareById = Comparator.comparing(PlayerObject::getHajs);
}
