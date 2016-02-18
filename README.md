# EntityManager
  
Author(제작자): **[SW-Team](https://github.com/SW-Team)**  
  
자매품(PMMP): [EntityManager-PMMP](https://github.com/milk0417/EntityManager)
  
**[NOTICE] This plug-in is not perfect, the entity may move abnormally (It was written in Java8)**
  
EntityManager is a plugin for managing entities, literally.  
Most entities are moving around, and jumps if needed.  
  
EntityManager also has simple API for developers,  
such as **clear()** or **create()**.  
  
See documentation page for details.  
  
**[알림] 이 플러그인은 완벽하지 않으며 엔티티가 비정상적으로 움직일 수 있습니다 (Java8로 작성되었습니다)**  
  
엔티티매니저는 말 그대로 엔티티를 관리하는 플러그인을 의미합니다.  
많은 엔티티들은 주위를 돌아다니거나 뛰어다닙니다.  

엔티티매니저는 또한 개발자 여러분을 위해  
**clear()** 또는 **create()** 와 같은 간단한 API가 제공됩니다.  
  
자세한 사항은 아래를 보시기 바랍니다

### YAML data
  * config.yml
``` yml
entity:
  explodeMode: false #엔티티 폭발모드(none, onlyEntity)
autospawn:
  turn-on: true #자동 소환 활성화 여부
  rand: "1/4" #엔티티 스폰 확률
  radius: 25 #몹이 스폰될 최대 반경
  tick: 100 #스폰 주기
  entities:
    animal: [Cow, Pig, Sheep, Chicken, Slime, Wolf, Ocelot, Rabbit] #소환될 동물 목록
    monster: [Zombie, Creeper, Skeleton, Spider, PigZombie, Enderman] #소환될 몬스터 목록
autoclear:
  turn-on: true #엔티티 자동 제거 여부
  tick: 6000 #엔티티 제거 주기
  entities: [Projectile, DroppedItem] #제거될 엔티티 목록
```
  * drops.yml
    * TODO
  
### Commands(명령어)
  * /entitymanager
    * usage: /entitymanager (check|remove|spawn)
    * permission: entitymanager.command
  * /entitymanager check
    * usage: /entitymanager check (Level="")
    * permission: entitymanager.command.check
    * description: Check the number of entities(If blank, it is set as a default Level)
  * /entitymanager remove
    * usage: /entitymanager remove (Level="")
    * permission: entitymanager.command.remove
    * description: Remove all entities in Level(If blank, it is set as a default Level)
  * /entitymanager spawn:
    * usage: /entitymanager spawn (type) (x="") (y="") (z="") (Level="")
    * permission: entitymanager.command.spawn
    * description: literally(If blank, it is set as a Player)
```