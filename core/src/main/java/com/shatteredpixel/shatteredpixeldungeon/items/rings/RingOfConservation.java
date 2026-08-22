package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RingOfConservation extends Ring {

    {
        icon = ItemSpriteSheet.Icons.RING_CONSERVATION;
        buffClass = Conservation.class;
    }

    public static float recycleChance(Char target) {
        return (float) Math.pow(getBuffedBonus(target, Conservation.class), 0.15f) - 0.9f;
    }
    public static float cursedProc(Char target) {
        return (getBuffedBonus(target, Conservation.class) / 15f) - 0.12f;
    }

    public String statsInfo() {
        System.out.println("level: " + soloBuffedBonus());
        System.out.println("recycleChance: " + RingOfConservation.recycleChance(Dungeon.hero));
        System.out.println("cursedProc: " + RingOfConservation.cursedProc(Dungeon.hero));

        if ( isIdentified() && !visiblyCursed() ){
            String info = Messages.get(this, "stats",
                    Messages.decimalFormat("#.##", 100f * ( Math.pow(soloBuffedBonus(), 0.15f) - 0.9f)) );
            if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)){
                info += "\n\n" + Messages.get(this, "combined_stats",
                        Messages.decimalFormat("#.##", 100f * ( Math.pow(combinedBuffedBonus(Dungeon.hero), 0.15f) - 0.9f)));
            }
            return info;
        } else if ( !visiblyCursed() ){
            return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 0.1f));
        } else {
            return Messages.get(this, "cursed_stats", Messages.decimalFormat("#.##", 100f * ( (soloBuffedBonus() / 15f ) - 0.12)) );
        }
    }

    public String upgradeStat1(int level){
        if (cursed) return Messages.decimalFormat("#.##", 100f * ( ((level + 1) / 15f ) - 0.12)) + "%";

        return Messages.decimalFormat("#.##", 100f * ( Math.pow(level + 1, 0.15f) - 0.9f)) + "%";
    }

    @Override
    protected RingBuff buff( ) {
        return new Conservation();
    }

    public class Conservation extends RingBuff {
    }
}
