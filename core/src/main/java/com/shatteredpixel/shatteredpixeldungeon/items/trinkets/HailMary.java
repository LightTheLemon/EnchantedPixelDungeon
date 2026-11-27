package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class HailMary extends Trinket {

    {
        image = ItemSpriteSheet.HAIL_MARY;
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
                    Messages.decimalFormat("#.##", 100*((damageMultiplier(buffedLvl()))-1f)),
                    Messages.decimalFormat("#.##", 100*(revivalChance(buffedLvl()))));
        } else {
            return Messages.get(this,
                    "typical_stats_desc",
                    Messages.decimalFormat("#.##", 100*((damageMultiplier(buffedLvl()))-1f)),
                    Messages.decimalFormat("#.##", 100*(revivalChance(buffedLvl()))));
        }
    }

    public static float revivalChance() {
        return revivalChance(trinketLevel(HailMary.class));
    }

    public static float revivalChance(int level) {
        if (level == -1){
            return 0;
        } else {
            return 0.3f + 0.15f*level;
        }
    }

    public static float damageMultiplier() {
        return damageMultiplier(trinketLevel(HailMary.class));
    }

    public static float damageMultiplier(int level) {
        if (level == -1){
            return 1;
        } else {
            return 1.03f + 0.03f*level;
        }
    }

}
