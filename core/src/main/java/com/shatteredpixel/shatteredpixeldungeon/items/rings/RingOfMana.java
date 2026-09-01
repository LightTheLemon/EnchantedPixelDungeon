package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RingOfMana extends Ring {

    {
        icon = ItemSpriteSheet.Icons.RING_MANA;
        buffClass = RingOfMana.Mana.class;
    }

    public String statsInfo() {
        if (isIdentified()){
            String info = Messages.get(this, "stats",
                    Messages.decimalFormat("#.##", -100f * (Math.pow(0.9, soloBuffedBonus()) - 1f)));
            if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)){
                info += "\n\n" + Messages.get(this, "combined_stats",
                        Messages.decimalFormat("#.##", -100f * (Math.pow(0.9, combinedBuffedBonus(Dungeon.hero)) - 1f)));
            }
            return info;
        } else {
            return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 0.9));
        }
    }

    public String upgradeStat1(int level){
        if (cursed && cursedKnown) level = Math.min(-1, level-3);

        return Messages.decimalFormat("#.##", 100f *  Math.pow(0.9, level + 1)) + "%";
    }

    @Override
    protected RingBuff buff( ) {
        return new Mana();
    }

    public static float ZapSpeedMultiplier( Char target ){
        return (float) Math.pow(0.9, getBuffedBonus(target, Mana.class));
    }

    public class Mana extends RingBuff {
    }
}
