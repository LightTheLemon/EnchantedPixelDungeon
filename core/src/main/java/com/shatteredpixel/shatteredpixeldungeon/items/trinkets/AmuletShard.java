package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class AmuletShard extends Trinket {

    {
        image = ItemSpriteSheet.AMULET_SHARD;
    }

    @Override
    protected int upgradeEnergyCost() {
        //4 -> 6(10) -> 8(18) -> 10(28)
        return 4+2*level();
    }

    @Override
    public String statsDesc() {
        if (isIdentified()){
            return Messages.get(this,
                    "stats_desc",
                    (int)Math.ceil((100*(1f - enemySpawnMultiplier(buffedLvl())))));
        } else {
            return Messages.get(this, "typical_stats_desc",
                    (int)Math.ceil((100*(1f - enemySpawnMultiplier(0)))));
        }
    }

    @Override
    protected void onThrow( int cell ) {
        super.onThrow( cell );
    }

    public static float enemySpawnMultiplier(){
        return enemySpawnMultiplier(trinketLevel(AmuletShard.class));
    }

    public static float enemySpawnMultiplier( int level ){
        if (level == -1){
            return 1f;
        } else {
            return 0.80f - 0.20f*level;
        }
    }

}
