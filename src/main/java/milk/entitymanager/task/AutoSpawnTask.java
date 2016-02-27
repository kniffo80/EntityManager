package milk.entitymanager.task;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.NukkitMath;
import milk.entitymanager.EntityManager;
import milk.entitymanager.util.Utils;
import milk.pureentities.PureEntities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AutoSpawnTask implements Runnable{

    public void run(){
        EntityManager owner = (EntityManager) Server.getInstance().getPluginManager().getPlugin("EntityManager");
        final int[] rand = {1, 4};
        try{
            String[] rs = owner.getData("autospawn.rand", "1/4").split("/");
            rand[0] = Integer.parseInt(rs[0]);
            rand[1] = Integer.parseInt(rs[1]);
        }catch(Exception ignore){}

        Server.getInstance().getOnlinePlayers().forEach((id, player) -> {
            if(Utils.rand(1, rand[1]) > rand[0]){
                return;
            }

            List list = null;
            int time = player.getLevel().getTime() % Level.TIME_FULL;
            if(Utils.rand()){
                list = owner.getData("autospawn.entities.animal", new ArrayList<String>(){{
                    add("Cow");
                    add("Pig");
                    add("Sheep");
                    add("Chicken");
                    add("Slime");
                    add("Ocelot");
                    add("Rabbit");
                }});
            }else if((time < Level.TIME_NIGHT || time > Level.TIME_SUNRISE)){
                list = owner.getData("autospawn.entities.monster", new ArrayList<String>(){{
                    add("Zombie");
                    add("Creeper");
                    add("Skeleton");
                    add("Spider");
                    add("PigZombie");
                    add("Enderman");
                }});
            }

            if(list == null || list.size() < 1){
                return;
            }

            int radius = owner.getData("autospawn.radius", 25);
            Position pos = new Position((int) player.x + 0.5 + Utils.rand(-radius, radius), 0, (int) player.z + 0.5 + Utils.rand(-radius, radius), player.level);
            pos.y = pos.level.getHighestBlockAt(NukkitMath.floorDouble(pos.x), NukkitMath.floorDouble(pos.z)) + 1;
            PureEntities.create(list.get(Utils.rand(0, list.size() - 1)), pos);
        });
    }

}