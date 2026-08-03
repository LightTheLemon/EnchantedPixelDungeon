package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
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
            GLog.i( Messages.get(this, "invisible") );
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
