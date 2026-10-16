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

package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bee;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BloodParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfVitality;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.VialOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class PotionOfHealing extends Potion {

	{
		icon = ItemSpriteSheet.Icons.POTION_HEALING;

		bones = true;
	}
	
	@Override
	public void apply( Hero hero ) {
		identify();
		cure( hero );
		heal( hero );
	}

	public static void heal( Char ch ){

		if (ch == Dungeon.hero && Dungeon.difficulty > 2.8){
			pharmacophobiaProc(Dungeon.hero);
		} else {
			CellEmitter.get( ch.pos ).start( Speck.factory( Speck.HEALING ), 0.4f, 4 );
			if (VialOfBlood.delayBurstHealing()){
				Healing healing = Buff.affect(ch, Healing.class);
				healing.setHeal(ch.HT, 0, VialOfBlood.maxHealPerTurn(), true);
				healing.applyVitalityEffect();
			} else {
				float healAmount = ch.HT;
				healAmount /= RingOfVitality.HealingMultiplier(Dungeon.hero);
				healAmount *= Math.max(0, Dungeon.healingDifficultyMultiplier());

				ch.HP = Math.round( Math.min(ch.HT, ch.HP + healAmount) );

				System.out.println("healAmount: " + healAmount );

			}

			////starts out healing 30 hp, equalizes with hero health total at level 11
			//Healing healing = Buff.affect(ch, Healing.class);
			//healing.setHeal((int) (0.8f * ch.HT + 14), 0.25f, 0);
			//Buff.affect(ch, Healing.class).applyVialEffect();

			if (ch == Dungeon.hero){
				GLog.p( Messages.get(PotionOfHealing.class, "heal") );
			}
		}
	}

	@Override
	public void shatter(int cell) {
		splash( cell );
		if (Dungeon.level.heroFOV[cell]) {
			GLog.i( Messages.get(Potion.class, "shatter") );
			Sample.INSTANCE.play( Assets.Sounds.SHATTER );
		}

		Char ch = Actor.findChar(cell);
		if (ch != null && Dungeon.difficulty <= 2.8 ){

			//Base healing for throwing a potion at a character is lower
			float healAmount = ch.HT * 0.66f;
			healAmount *= (Dungeon.healingDifficultyMultiplier());

			if (ch != Dungeon.hero) {

				if (ch.alignment == Char.Alignment.ALLY) {
					GLog.p( Messages.get(PotionOfHealing.class, "heal_ally") );
				} else {
					GLog.p( Messages.get(PotionOfHealing.class, "heal_enemy") );
					healAmount += 0.2f * ch.HT;
				}
				ch.HP = Math.min( ch.HT, Math.round(ch.HP + healAmount));

			} else {

				healAmount /= RingOfVitality.HealingMultiplier(Dungeon.hero);
				ch.HP = Math.min( ch.HT, Math.round(ch.HP + healAmount));

			}

			PotionOfHealing.cure(ch);
			ch.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);

			if (ch != Dungeon.hero && ch.HP == ch.HT) {
				ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(ch.HT / 4), FloatingText.SHIELDING);
				Buff.affect(ch, Barrier.class).setShield( Math.round(healAmount / 2f));
			}

			if (ch instanceof Bee && ch.alignment != curUser.alignment){
				ch.alignment = Char.Alignment.ALLY;
				((Bee)ch).setPotInfo(-1, null);
			}

		} else if (ch != null && ch.alignment != Char.Alignment.ENEMY ) {
			ch.sprite.emitter().burst( BloodParticle.BURST, 2 );
			Buff.affect(ch, Poison.class).set(3);

		}
	}

	public static void pharmacophobiaProc( Hero hero ){
		// harms the hero depending on difficulty
		Buff.affect( hero, Poison.class).set(6 + hero.lvl/1.5f * (Math.max(0.05f, -Dungeon.healingDifficultyMultiplier()) ) );
	}
	
	public static void cure( Char ch ) {
		Buff.detach( ch, Poison.class );
		Buff.detach( ch, Cripple.class );
		Buff.detach( ch, Weakness.class );
		Buff.detach( ch, Vulnerable.class );
		Buff.detach( ch, Bleeding.class );
		Buff.detach( ch, Blindness.class );
		Buff.detach( ch, Drowsy.class );
		Buff.detach( ch, Slow.class );
		Buff.detach( ch, Vertigo.class);
		Buff.detach( ch, Ooze.class );
	}

	@Override
	public int value() {
		return isKnown() ? 30 * quantity : super.value();
	}
}
