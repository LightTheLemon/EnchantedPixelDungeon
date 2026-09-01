package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class AmuletShard extends Trinket {

    {
        image = ItemSpriteSheet.AMULET_SHARD;
    }

    @Override
    protected int upgradeEnergyCost() {
        //6 -> 8(14) -> 10(24) -> 12(36)
        return 6+2*level();
    }

    @Override
    public String statsDesc() {
        if (isIdentified()){
            return Messages.get(this,
                    "stats_desc",
                    (int)(100*(1f - enemySpawnMultiplier(buffedLvl()))));
        } else {
            return Messages.get(this, "typical_stats_desc",
                    (int)(100*(1f - enemySpawnMultiplier(0))));
        }
    }

    public static float enemySpawnMultiplier(){
        return enemySpawnMultiplier(trinketLevel(AmuletShard.class));
    }

    public static float enemySpawnMultiplier( int level ){
        if (level == -1){
            return 1f;
        } else {
            return 0.95f - 0.05f*level;
        }
    }

}
