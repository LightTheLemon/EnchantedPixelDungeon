/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Kinetic;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import static java.lang.Math.pow;

public class Annoying extends Weapon.Enchantment {

	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {

		float procChance = 1/20f * procChanceMultiplier(attacker) + weapon.buffedLvl()/80f;
		if (Random.Float() < procChance) {
			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
				mob.beckon(attacker.pos);
			}
			attacker.sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.3f, 3);
			Sample.INSTANCE.play(Assets.Sounds.MIMIC);
			Invisibility.dispel();
			int chance;
			//~1/100 for each rare line, ~1/10 for each common line
			if (Random.Int(33) != 0) {
				chance = Random.IntRange(1, 10);
				GLog.n(Messages.get(this, "msg_" + Random.IntRange(1, 10)));
			} else {
				chance = Random.IntRange(11, 13);
				GLog.n(Messages.get(this, "msg_" + Random.IntRange(11, 13)));
			}
			if(Random.Float() < 1-pow(.05,.1*weapon.buffedLvl())){
				switch (chance){
					case 1 : Buff.affect(attacker, Kinetic.ConservedDamage.class).setBonus((int) (Dungeon.depth*2.5f));
					case 2 : Buff.affect(attacker, Adrenaline.class, 5f);
					case 3 : Buff.affect(defender, Vertigo.class, 4f);
					case 4 : Buff.affect(attacker, Awareness.class, 15f);
					case 5 : Buff.affect(attacker, Weakness.class, 2f);
					case 6 : Buff.affect(defender, Vulnerable.class, 5f);
					case 7 : Buff.affect(attacker, Haste.class, 10f);
					case 8 : Buff.affect(attacker, Drowsy.class, 3f);
					case 9 : Buff.affect(defender, Amok.class, 5f);
					case 10: Buff.affect(defender, Daze.class, 7f);
					case 11: Buff.affect(attacker, Slow.class, 20f);
					case 12: Buff.affect(attacker, Bless.class, 25f);
					case 13: {
						for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
							if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
								if (!mob.isImmune(Dread.class)){
									Buff.affect( mob, Dread.class ).object = attacker.id();
								} else {
									Buff.affect( mob, Terror.class, Terror.DURATION ).object = attacker.id();
								}
							}
						}
					}


				}
			}




		}

		return damage;
	}

	@Override
	public boolean curse() {
		return true;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

}