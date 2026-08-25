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

    private static ItemSprite.Glowing SALMON = new ItemSprite.Glowing( 0xFFB366 );

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        double procChance = (Math.pow(weapon.buffedLvl() / 40f, 0.6) + 0.05) * Weapon.Enchantment.genericProcChanceMultiplier(Dungeon.hero);
        System.out.println("procChance: " + procChance);
        if (Random.Float() < procChance && !(Char.hasProp(defender, INORGANIC))) {
            Buff.affect(defender, Culinary.culinaryProc.class);
        }
        return damage;
    }

    public static boolean hasFoodEnchant(Weapon weapon) {
        return weapon != null && weapon.hasEnchant(Culinary.class, Dungeon.hero);
    }

    @Override
    public String enchantUpgradeStat1(int level) {

        double baseChance = (Math.pow(level / 40f, 0.6) + 0.05);
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

}
