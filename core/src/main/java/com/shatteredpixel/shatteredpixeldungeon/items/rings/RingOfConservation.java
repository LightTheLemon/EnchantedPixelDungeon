package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
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

    //some items have a custom way of handling the ring instead of calling this method
    //these include liquid metal, upgrade scrolls (WndUpgrade)
    public static void detachProc (Item item, int amount) {
        if (item == null) return;

        if (!(item instanceof PotionOfStrength) || Random.Float() < 0.5)  {
            if (recycleProc()) {
                //keep the item
            } else if (recycleCurseProc()) {
                item.detach(Dungeon.hero.belongings.backpack);
                item.detach(Dungeon.hero.belongings.backpack);
            } else {
                item.detach(Dungeon.hero.belongings.backpack);
            }
        } else {
            item.detach(Dungeon.hero.belongings.backpack);
        }
    }

    public static float recycleChance(Char target) {
        return (float) Math.pow(getBuffedBonus(target, Conservation.class), 0.10f) - 0.92f;
    }
    public static float curseChance(Char target) {
        return (getBuffedBonus(target, Conservation.class) * -0.05f) + 0.1f;
    }

    public String statsInfo() {
        //System.out.println("level: " + soloBuffedBonus());
        //System.out.println("recycleChance: " + RingOfConservation.recycleChance(Dungeon.hero));
        //System.out.println("cursedProc: " + RingOfConservation.curseChance(Dungeon.hero));

        if ( isIdentified() && !visiblyCursed() ){
            String info = Messages.get(this, "stats",
                    Messages.decimalFormat("#.##", 100f * ( Math.pow(soloBuffedBonus(), 0.10f) - 0.92f)) );
            if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)){
                info += "\n\n" + Messages.get(this, "combined_stats",
                        Messages.decimalFormat("#.##", 100f * ( Math.pow(combinedBuffedBonus(Dungeon.hero), 0.10f) - 0.92f)));
            }
            return info;
        } else if ( !visiblyCursed() ){
            return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 0.08f));
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
        if ( Float.isNaN(RingOfConservation.recycleChance(Dungeon.hero)) && Random.Float() < RingOfConservation.curseChance(Dungeon.hero) ) {

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

        return Messages.decimalFormat("#.##", 100f * ( Math.pow(level + 1, 0.10f) - 0.92f)) + "%";
    }

    @Override
    protected RingBuff buff( ) {
        return new Conservation();
    }

    public class Conservation extends RingBuff {
    }
}
