package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RingOfVitality extends Ring {
    {
        icon = ItemSpriteSheet.Icons.RING_VITALITY;
        buffClass = RingOfVitality.Vitality.class;
    }

    public String statsInfo() {
        if (isIdentified()){
            String info = Messages.get(this, "stats",
                    Messages.decimalFormat("#.##", 100f * (soloBuffedBonus() * 0.1) + 1 - 1f));
            if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)){
                info += "\n\n" + Messages.get(this, "combined_stats",
                        Messages.decimalFormat("#.##", 100f * (combinedBuffedBonus(Dungeon.hero) * 0.1) + 1 - 1f));
            }
            return info;
        } else {
            return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 10f));
        }
    }

    public String upgradeStat1(int level){
        if (cursed && cursedKnown) level = Math.min(-1, level-3);

        return Messages.decimalFormat("#.##", 100f * (((level + 1) * 0.10f) + 1f ))  + "%";
    }

    @Override
    public boolean doEquip(Hero hero) {
        if (super.doEquip(hero)){
            hero.updateHT( false );
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)){
            hero.updateHT( false );
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Item upgrade() {
        super.upgrade();
        updateTargetHT();
        return this;
    }

    @Override
    public Item level(int value) {
        super.level(value);
        updateTargetHT();
        return this;
    }

    private void updateTargetHT(){
        if (buff != null && buff.target instanceof Hero){
            ((Hero) buff.target).updateHT( false );
        }
    }

    @Override
    protected RingBuff buff( ) {
        return new Vitality();
    }

    public static float HTMultiplier( Char target ){
        return (getBuffedBonus(target, RingOfVitality.Vitality.class) * 0.10f) + 1f;
    }

    public static float HealingMultiplier( Char target ){
        return (getBuffedBonus(target, RingOfVitality.Vitality.class) * 0.05f) + 1f;
    }

    public class Vitality extends RingBuff {
    }

}
