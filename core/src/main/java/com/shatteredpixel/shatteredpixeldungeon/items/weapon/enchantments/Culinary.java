package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import static com.shatteredpixel.shatteredpixeldungeon.actors.Char.Property.INORGANIC;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.Visual;
import com.watabou.utils.Random;

public class Culinary extends Weapon.Enchantment {
    private int timesUsed = 0;
    private static ItemSprite.Glowing SALMON = new ItemSprite.Glowing( 0xFFB366 );

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        double procChance = (Math.pow(weapon.buffedLvl() / 40f, 0.6) + 0.08) * Weapon.Enchantment.genericProcChanceMultiplier(Dungeon.hero);

        //Chance also decreases the more drops you get
        if (!Char.hasProp(defender, INORGANIC) && Random.Float() < procChance * Math.pow(0.9, timesUsed)) {
            Buff.affect(defender, culinaryProc.class);
            timesUsed++;
        }
        Buff.affect(defender, culinaryKill.class);

        System.out.println("procChance: " + procChance);
        System.out.println("Chance after times used: " + procChance * Math.pow(0.9, timesUsed));

        return damage;
    }

    @Override
    public String enchantUpgradeStat1(int level) {

        double baseChance = (Math.pow(level / 40f, 0.6) + 0.08);
        float bonus = Weapon.Enchantment.genericProcChanceMultiplier(Dungeon.hero);

        return Messages.decimalFormat("#.##", 100f * (baseChance * bonus) ) + "%";
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return SALMON;
    }

    public static void showFlare( Visual vis ){
        new Flare(6, 20).color(0xFFB366, true).show(vis, 3f);
    }

    public static class culinaryProc extends Buff {

        @Override
        public boolean act() {
            detach();
            return true;
        }

        public Item genLoot(){
            detach();
            return new MysteryMeat();
        }
    }

    public static class culinaryKill extends Buff {

        @Override
        public boolean act() {
            detach();
            return true;
        }
    }

}
