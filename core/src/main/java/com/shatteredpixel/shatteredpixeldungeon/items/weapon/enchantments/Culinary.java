package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;

public class Culinary extends Weapon.Enchantment { //bug: after unequipping, still applies effect

    private static ItemSprite.Glowing SALMON = new ItemSprite.Glowing( 0xFFB366 );

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        return damage;
    }
    public static float culinaryEnchantProc(Weapon weapon) {
        if ( weapon != null) {
            int level = weapon.buffedLvl();
            double baseChance = (Math.pow(level / 40f, 0.6) + 0.05);
            float bonus = Weapon.Enchantment.genericProcChanceMultiplier(Dungeon.hero);

            if (weapon.hasEnchant(Culinary.class, Dungeon.hero)) {
                System.out.println("level: " + level + "\nbaseChance: " + baseChance + "\nbonus: " + bonus + "\ntotal: " + (baseChance * bonus));
                return (float) baseChance * bonus;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public String enchantUpgradeStat1(int level) {

        double baseChance = (Math.pow(level / 40f, 0.6) + 0.05);
        float bonus = Weapon.Enchantment.genericProcChanceMultiplier(Dungeon.hero);

        return Messages.decimalFormat("#.##", 100f * (baseChance * bonus) );
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return SALMON;
    }
}
