package milk.entitymanager.util;

import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;

import java.util.Random;

public class Utils{

    public static int rand(int min, int max){
        if(min == max){
            return max;
        }
        return min + new Random().nextInt(max - min);
    }

    public static boolean rand(){
        return new Random().nextBoolean();
    }

    public static void logInfo(String text){
        Server.getInstance().getLogger().info(TextFormat.GOLD + "[EntityManager]" + text);
    }

}
