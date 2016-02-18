package milk.entitymanager.task;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import milk.entitymanager.EntityManager;

import java.util.ArrayList;
import java.util.List;

public class AutoClearTask implements Runnable{

    @Override
    public void run(){
        EntityManager owner = (EntityManager) Server.getInstance().getPluginManager().getPlugin("EntityManager");
        List list = owner.getData("autoclear.entitites", new ArrayList<String>(){{
            add("Projectile");
            add("DroppedItem");
        }});
        Server.getInstance().getLevels().forEach((id, level) -> {
            for(Entity entity : level.getEntities()){
                if(list.contains(entity.getClass().getSimpleName())){
                    entity.close();
                }
            }
        });
    }

}
