package milk.entitymanager;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.*;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.event.entity.EntitySpawnEvent;
import cn.nukkit.event.entity.ExplosionPrimeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import milk.pureentities.PureEntities;
import milk.pureentities.entity.*;
import milk.entitymanager.task.AutoClearTask;
import milk.entitymanager.util.Utils;

import java.io.File;
import java.util.*;

public class EntityManager extends PluginBase implements Listener{

    private static LinkedHashMap<String, Object> data;
    private static LinkedHashMap<String, Object> drops;

    public static void clear(){
        clear(new Class[]{BaseEntity.class});
    }

    public static void clear(Class[] type){
        clear(type, Server.getInstance().getDefaultLevel());
    }

    public static void clear(Class[] type, String levelName){
        clear(type, Server.getInstance().getLevelByName(levelName));
    }

    public static void clear(Class[] type, Level level){
        if(level == null){
            return;
        }

        for(Entity entity : level.getEntities()){
            for(Class clazz : type){
                if(clazz.isInstance(entity)){
                    entity.close();
                }
            }
        }
    }

    public static void addEntityDropItem(String name, int id, int minCount, int maxCount){
        addEntityDropItem(name, Item.get(id), minCount, maxCount);
    }

    public static void addEntityDropItem(String name, int id, int damage, int minCount, int maxCount){
        addEntityDropItem(name, Item.get(id, damage), minCount, maxCount);
    }

    @SuppressWarnings("unchecked")
    public static void addEntityDropItem(String name, Item item, int minCount, int maxCount){
        ArrayList<Object> list;
        if(!drops.containsKey(name) || !(drops.get(name) instanceof ArrayList)){
            list = new ArrayList<>();
            drops.put(name, list);
        }else{
            list = (ArrayList<Object>) drops.get(name);
        }

        for(Object l : list.toArray(new Object[list.size()])){
            if(!(l instanceof ArrayList)){
                continue;
            }

            ArrayList<Object> list1 = (ArrayList<Object>) l;
            if(list1.get(0).equals(item.getId()) && list1.get(1).equals(item.getDamage())){
                list1.set(2, minCount + "," + maxCount);
                return;
            }
        }

        list.add(new ArrayList<Object>(){{
            add(item.getId());
            add(item.getDamage());
            add(minCount + "," + maxCount);
        }});
    }

    public static void resetEntityDropItem(String name){
        if(!drops.containsKey(name) || !(drops.get(name) instanceof ArrayList)){
            return;
        }
        drops.remove(name);
    }

    @SuppressWarnings("unchecked")
    public static void removeEntityDropItem(String name, Item item){
        if(!drops.containsKey(name) || !(drops.get(name) instanceof ArrayList)){
            return;
        }

        ArrayList<Object> list = (ArrayList<Object>) drops.get(name);
        for(int i = 0; i < list.size(); i++){
            if(!(list.get(i) instanceof ArrayList)){
                continue;
            }

            ArrayList<Object> list1 = (ArrayList<Object>) list.get(i);
            if(list1.get(0).equals(item.getId()) && list1.get(1).equals(item.getDamage())){
                list1.remove(i);
                return;
            }
        }
    }

    public void onEnable(){
        if(this.getServer().getPluginManager().getPlugin("PureEntities") == null){
            Utils.logInfo("PureEntities 플러그인이 존재하지 않습니다, PureEntities 플러그인을 적용해주세요");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.saveDefaultConfig();
        if(this.getConfig().exists("spawn")){
            this.saveResource("config.yml", true);
            this.reloadConfig();
            Utils.logInfo("\"config.yml\"파일이 새로 업데이트 되었습니다.(파일을 확인후 서버를 다시 열어주세요)");
        }

        data = (LinkedHashMap<String, Object>) this.getConfig().getAll();
        drops = (LinkedHashMap<String, Object>) new Config(new File(this.getDataFolder(), "drops.yml"), Config.YAML).getAll();

        if(this.getData("autoclear.turn-on", true)){
            this.getServer().getScheduler().scheduleRepeatingTask(new AutoClearTask(), this.getData("autoclear.tick", 6000));
        }

        this.getServer().getPluginManager().registerEvents(this, this);
        Utils.logInfo("Plugin has been enabled");
    }

    public void onDisable(){
        Config drops = new Config(new File(this.getDataFolder(), "drops.yml"), Config.YAML);
        drops.setAll(EntityManager.drops);
        drops.save();

        Utils.logInfo("Plugin has been disable");
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key, T defaultValue){
        try{
            String[] vars = key.split(".");
            if(vars.length < 1){
                return defaultValue;
            }

            String base = vars[0];
            if(!data.containsKey(base)){
                return defaultValue;
            }

            if(!(data.get(base) instanceof Map)){
                return (T) data.get(base);
            }
            Object nbase = data.get(base);

            int index = 0;
            while(++index < vars.length){
                String baseKey = vars[index];
                if(!(data.get(baseKey) instanceof Map)){
                    return (T) data.get(baseKey);
                }
                nbase = data.get(baseKey);
            }
            return (T) nbase;
        }catch(Exception e){
            return defaultValue;
        }
    }

    @EventHandler
    public void ExplosionPrimeEvent(ExplosionPrimeEvent ev){
        switch(this.getData("entity.explodeMode", "none")){
            case "entity":
            case "onlyEntity":
                ev.setBlockBreaking(false);
                break;
            case "none":
                ev.setForce(0);
                ev.setBlockBreaking(false);
            case "cancel":
            case "cancelled":
                ev.setCancelled();
                break;
        }
    }

    public void onEntityCreateEvent(EntitySpawnEvent ev){
        Entity entity = ev.getEntity();
        ArrayList<String> list = this.getData("entity.not-spawn", new ArrayList<>());
        if(list.contains(entity.getClass().getSimpleName())){
            entity.close();
        }
    }

    @EventHandler
    @SuppressWarnings("unchecked")
    public void EntityDeathEvent(EntityDeathEvent ev){
        Entity entity = ev.getEntity();
        Class<? extends Entity> clazz = entity.getClass();
        if(!(drops.get(clazz.getSimpleName()) instanceof List)){
            return;
        }

        ArrayList<Item> items = new ArrayList<>();
        List<Object> drop = (List) drops.get(clazz.getSimpleName());
        drop.forEach(k -> {
            try{
                if(!(k instanceof List)){
                    return;
                }

                List data = (List) k;
                if(data.size() < 3){
                    return;
                }

                String[] cs = data.get(2).toString().split(",");
                int[] count = new int[]{Integer.parseInt(cs[0]), Integer.parseInt(cs[1])};
                Item item = Item.get((int) data.get(0), (int) data.get(1));
                item.setCount(Utils.rand(count[0], count[1]));
                items.add(item);
            }catch(Exception ignore){}
        });
        ev.setDrops(items.toArray(new Item[items.size()]));
    }

    public boolean onCommand(CommandSender i, Command cmd, String label, String[] sub){
        String output = "[EntityManager]";
        switch(sub.length > 0 ? sub[0] : ""){
            case "remove":
                if(!i.hasPermission("entitymanager.command.remove")){
                    i.sendMessage(TextFormat.RED + "명령어를 사용할 권한이 없습니다");
                    //i.sendMessage(TextFormat.RED + "You do not have permission to use this command");
                    return true;
                }
                Level level;
                if(sub.length > 1){
                    level = this.getServer().getLevelByName(sub[1]);
                }else{
                    level = i instanceof Player ? ((Player) i).getLevel() : null;
                }

                clear(new Class[]{BaseEntity.class, EntityProjectile.class, EntityItem.class}, level);
                output += "소환된 모든 엔티티를 제거했습니다";
                //output += "All spawned entities were removed";
                break;
            case "check":
                if(!i.hasPermission("entitymanager.command.check")){
                    i.sendMessage(TextFormat.RED + "명령어를 사용할 권한이 없습니다");
                    //i.sendMessage(TextFormat.RED + "You do not have permission to use this command");
                    return true;
                }
                int human = 0;
                int living = 0;
                int item = 0;
                int hanging = 0;
                int projectile = 0;
                int other = 0;
                Level lv;
                if(sub.length > 1){
                    lv = this.getServer().getLevelByName(sub[1]);
                }else{
                    lv = i instanceof Player ? ((Player) i).getLevel() : this.getServer().getDefaultLevel();
                }
                for(Entity ent : lv.getEntities()){
                    if(ent instanceof EntityHuman){
                        human++;
                    }else if(ent instanceof EntityLiving){
                        living++;
                    }else if(ent instanceof EntityItem){
                        item++;
                    }else if(ent instanceof EntityHanging){
                        hanging++;
                    }else if(ent instanceof EntityProjectile){
                        projectile++;
                    }else{
                        other++;
                    }
                }
                String k = "--- 월드 " + lv.getName() + " 에 있는 모든 엔티티---\n";
                //String k = "--- All entities in Level " + level.getName() + " ---\n";
                k += TextFormat.YELLOW + "Human: %s\n";
                k += TextFormat.YELLOW + "Living: %s\n";
                k += TextFormat.YELLOW + "Item: %s\n";
                k += TextFormat.YELLOW + "Hanging: %s\n";
                k += TextFormat.YELLOW + "Projectile: %s\n";
                k += TextFormat.YELLOW + "Other: %s\n";
                output = String.format(k, human, living, item, hanging, projectile, other);
                break;
            case "create":
                if(!i.hasPermission("entitymanager.command.create")){
                    i.sendMessage(TextFormat.RED + "명령어를 사용할 권한이 없습니다");
                    //i.sendMessage(TextFormat.RED + "You do not have permission to use this command");
                    return true;
                }

                int type1 = -1;
                String type2 = sub.length > 1 ? sub[1] : "";
                try{
                    type1 = Integer.parseInt(type2);
                }catch(Exception ignore){}

                if(type1 == -1 || type2.length() < 1){
                    output += "존재하지 않는 엔티티에요";
                    //output += "Entity's name is incorrect";
                    break;
                }

                Position pos = null;
                if(sub.length > 4){
                    Level lk = null;
                    if(sub.length > 5){
                        lk = this.getServer().getLevelByName(sub[5]);
                    }else if(i instanceof Player){
                        lk = ((Player) i).getLevel();
                    }

                    if(lk == null){
                        lk = this.getServer().getDefaultLevel();
                    }

                    pos = new Position(Double.parseDouble(sub[2]), Double.parseDouble(sub[3]), Double.parseDouble(sub[4]), lk);
                }else if(i instanceof Player){
                    pos = ((Player) i).getPosition();
                }

                if(pos == null){
                    output += "사용법: /" + label + " create <id/name> (x) (y) (z) (level)";
                    //output += "usage: /label create <id/name> (x) (y) (z) (level)";
                    break;
                }

                Entity ent;
                if((ent = PureEntities.create(type1, pos)) == null){
                    if((ent = PureEntities.create(type2, pos)) == null){
                        output += "존재하지 않는 엔티티에요";
                        break;
                    }
                }
                ent.spawnToAll();
                break;
            default:
                output += "사용법: /" + label + " <remove/check/create>";
                //output += "usage: /label <remove/check/create>";
                break;
        }

        i.sendMessage(output);
        return true;
    }

}