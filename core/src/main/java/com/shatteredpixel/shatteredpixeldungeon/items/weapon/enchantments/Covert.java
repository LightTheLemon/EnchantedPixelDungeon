package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
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
            /*
            float powerMulti = Math.max(1f, procChance);

            if (defender.buff(Burning.class) == null){
                Buff.affect(defender, Burning.class).reignite(defender, 8f);
                powerMulti -= 1;
            }

            if (powerMulti > 0){
                int burnDamage = Random.NormalIntRange( 1, 3 + Dungeon.scalingDepth()/4 );
                burnDamage = Math.round(burnDamage * 0.67f * powerMulti);
                if (burnDamage > 0) {
                    defender.damage(burnDamage, this);
                }
            }
             */

            attacker.buff(Invisibility.class);
            Buff.prolong(attacker, Invisibility.class, 3);
            Buff.affect(attacker, Invisibility.class, 4f);

            GLog.i( Messages.get(this, "invisible") );
            Sample.INSTANCE.play( Assets.Sounds.MELD );
            defender.sprite.emitter().burst( ShadowParticle.MISSILE, level + 1 );

        }

        return damage; //was return damage;

    }

    @Override
    public Glowing glowing() {
        return BLUE;
    }
}
