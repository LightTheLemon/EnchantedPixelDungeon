package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite.Glowing;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class Covert extends Weapon.Enchantment {
    private static ItemSprite.Glowing DARK_BLUE = new ItemSprite.Glowing( 0x04006e );

    @Override
    public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
        int level = Math.max( 0, weapon.buffedLvl() );

        if (damage >= defender.HP){
            GLog.i( Messages.get(this, "invisible") );
            Sample.INSTANCE.play( Assets.Sounds.MELD );
            defender.sprite.emitter().burst( ShadowParticle.MISSILE, (level / 2) + 1 );
        }
        return damage;
    }

    @Override
    public String enchantUpgradeStat1(int level) {

        float bonus = Weapon.Enchantment.genericProcChanceMultiplier(Dungeon.hero);
        float finalDuration = ((Invisibility.DURATION - 18) / 2) + (level * 2f)  * bonus;

        return Messages.decimalFormat("#.##", finalDuration );
    }

    @Override
    public Glowing glowing() {
        return DARK_BLUE;
    }
}
