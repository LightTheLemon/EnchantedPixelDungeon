package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite.Glowing;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class Covert extends Weapon.Enchantment {

    private static ItemSprite.Glowing BLUE = new ItemSprite.Glowing( 0x0000FF );

    @Override
    public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
        int level = Math.max( 0, weapon.buffedLvl() );
        // lvl 0 - 33%
        // lvl 1 - 50%
        // lvl 2 - 60%

        //float procChance = (level+1f)/(level+3f) * procChanceMultiplier(attacker);

        if (damage >= defender.HP){


            //Buff.affect(attacker, Invisibility.class, 1 * level * procChanceMultiplier(attacker));
            //Buff.affect(hero, Invisibility.class, 1 * level * procChanceMultiplier(attacker));
            //Buff.append(Dungeon.hero, Invisibility.class, 1 * level * procChanceMultiplier(attacker));
            //Buff.affect( Dungeon.hero, Charm.class, Charm.DURATION ).object = attacker.id(); //test
            Buff.prolong( attacker, Invisibility.class, Invisibility.DURATION / 3);

            //GLog.i("You turn invisible!");
            //GLog.i( Messages.get(this, "invisible") );
            Sample.INSTANCE.play( Assets.Sounds.MELD );
            defender.sprite.emitter().burst( ShadowParticle.MISSILE, (level / 2) + 1 );

        }

        return damage;

    }

    @Override
    public Glowing glowing() {
        return BLUE;
    }
}
