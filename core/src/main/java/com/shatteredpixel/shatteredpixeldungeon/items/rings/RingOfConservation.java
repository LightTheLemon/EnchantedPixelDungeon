package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class RingOfConservation extends Ring {

    {
        icon = ItemSpriteSheet.Icons.RING_CONSERVATION;
        buffClass = Conservation.class;
    }

    public static float recycleChance(Char target) {
        return (float) Math.pow(getBuffedBonus(target, Conservation.class), 0.12f) - 0.9f;
    }
    public static float curseChance(Char target) {
        return (getBuffedBonus(target, Conservation.class) * -0.05f) + 0.1f;
    }

    public String statsInfo() {
        System.out.println("level: " + soloBuffedBonus());
        System.out.println("recycleChance: " + RingOfConservation.recycleChance(Dungeon.hero));
        System.out.println("cursedProc: " + RingOfConservation.curseChance(Dungeon.hero));

        if ( isIdentified() && !visiblyCursed() ){
            String info = Messages.get(this, "stats",
                    Messages.decimalFormat("#.##", 100f * ( Math.pow(soloBuffedBonus(), 0.12f) - 0.9f)) );
            if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)){
                info += "\n\n" + Messages.get(this, "combined_stats",
                        Messages.decimalFormat("#.##", 100f * ( Math.pow(combinedBuffedBonus(Dungeon.hero), 0.12f) - 0.9f)));
            }
            return info;
        } else if ( !visiblyCursed() ){
            return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 0.1f));
        } else {
            return Messages.get(this, "cursed_stats", Messages.decimalFormat("#.##", 100f * ( (soloBuffedBonus() * -0.05f ) + 0.1)) );
        }
    }
    public static boolean recycleProc() {
        if ( Random.Float() < RingOfConservation.recycleChance(Dungeon.hero) ) {

            GLog.p(Messages.get(RingOfConservation.class, "conservation_proc"));
            new Flare(6, 32).color(0x00E626, true).show(Dungeon.hero.sprite, 2f);
            Sample.INSTANCE.play( Assets.Sounds.TELEPORT );

            return true;

        } else {
            return false;
        }
    }
    public static boolean recycleCurseProc() {
        if ( Float.isNaN(RingOfConservation.recycleChance(Dungeon.hero) ) && Random.Float() < RingOfConservation.curseChance(Dungeon.hero) ) {

            GLog.p(Messages.get(RingOfConservation.class, "cursed_proc"));
            new Flare(6, 32).color(0x000000, true).show(Dungeon.hero.sprite, 2f);
            Sample.INSTANCE.play( Assets.Sounds.CURSED );

            return true;

        } else {
            return false;
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
