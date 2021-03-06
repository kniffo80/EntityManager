# EntityManager
  
Author(제작자): **[SW-Team](https://github.com/SW-Team)**  
  
자매품(PMMP): [EntityManager-PMMP](https://github.com/milk0417/EntityManager)
  
EntityManager is a plugin for managing entities(Mob, Animal), literally.  
This plug-in requires [PureEntities](https://github.com/SW-Team/PureEntities) that support Entity
    
EntityManager는 말 그대로 Entity(Mob, Animal)를 관리하는 플러그인을 의미합니다.  
이 플러그인은 Entity를 Nukkit에서 구현시켜주는 [PureEntities](https://github.com/SW-Team/PureEntities) 플러그인이 필요합니다.
  
### YAML data
  * config.yml
``` yml
entity:
  not-spawn: [] #스폰을 막을 엔티티 목록
  explodeMode: false #엔티티 폭발모드(none, onlyEntity, cancelled)
autoclear:
  turn-on: true #엔티티 자동 제거 여부
  tick: 6000 #엔티티 제거 주기(20 = 1second)
  entities: [Projectile, DroppedItem] #제거될 엔티티 목록
```
  
### Commands(명령어)
| Command | Permission | Description |
| ----- | :---------: | :----------: |
| `/entitymanager (check|remove|spawn)` | `entitymanager.command`| None |
| `/entitymanager check (Level="")`| `entitymanager.command.check` | Check the number of entities(If blank, it is set as a default Level)|
| `/entitymanager remove (Level="")`| `entitymanager.command.remove` | Remove all entities in Level(If blank, it is set as a default Level) |
| `/entitymanager spawn (type) (x="") (y="") (z="") (Level="")` | `entitymanager.command.spawn`| literally(If blank, it is set as a Player)|
