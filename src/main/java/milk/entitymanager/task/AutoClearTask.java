package milk.entitymanager.task;

import cn.nukkit.Server;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.projectile.EntityProjectile;
import milk.entitymanager.EntityManager;
import milk.pureentities.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

public class AutoClearTask implements Runnable{

    @Override
    public void run(){
        EntityManager owner = (EntityManager) Server.getInstance().getPluginManager().getPlugin("EntityManager");
        List<String> list = owner.getData("autoclear.entitites", new ArrayList<String>(){{
            add("EntityProjectile");
            add("EntityItem");
        }});
        int index = 0;
        Class[] type = new Class[list.size()];
        for(String k : list){
            switch(k){
                case "Projectile":
                case "EntityProjectile":
                    type[index++] = EntityProjectile.class;
                    break;
                case "Item":
                case "EntityItem":
                case "DroppedItem":
                    type[index++] = EntityItem.class;
                    break;
                case "BaseEntity":
                    type[index++] = BaseEntity.class;
                    break;
            }
        }
        Server.getInstance().getLevels().forEach((id, level) -> EntityManager.clear(type, level));
    }

}
