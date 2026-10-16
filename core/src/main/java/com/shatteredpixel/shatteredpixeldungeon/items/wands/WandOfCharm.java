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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LifeLink;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollExile;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.PhantomStopwatch;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
public class WandOfCharm extends Wand {

	{
		image = ItemSpriteSheet.WAND_CHARM;

		collisionProperties = Ballistica.PROJECTILE;
	}

	@Override
	public void onZap(Ballistica bolt) {

		Char ch = Actor.findChar(bolt.collisionPos);

		if (ch instanceof Mob){
			
			wandProc(ch, chargesPerCast());

			if (ch.alignment == Char.Alignment.ALLY ){

				//Might be interesting or more balanced to charm allies?
				//Buff.prolong( ch, Charm.class, Charm.DURATION / 2).object = curUser.id();

				if (ch.HP < ch.HT) {
					int healing = Math.round(((buffedLvl()/20f) + 0.05f) * ch.HT);
					ch.HP = Math.min(ch.HP + healing, ch.HT);
					ch.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1 + (buffedLvl() / 4));
					ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(healing), FloatingText.HEALING);

					if (ch.buff(WandOfCharm.InLove.class) != null){
						Dungeon.hero.HP = Math.min(Dungeon.hero.HP + (healing / 2), Dungeon.hero.HT);
					}

				} else {

					Barrier barrier = Buff.affect(ch, Barrier.class);
					int currentShield = barrier.shielding();
					int addedShield = (int)(20f / Math.sqrt(currentShield + 1));
					barrier.setShield(currentShield + addedShield);

				}

			} else if (ch.alignment == Char.Alignment.ENEMY || ch instanceof Mimic) {

				int duration = Math.round(( Charm.DURATION/5f ) + (buffedLvl()/2f) + 1);

				//Interactions with other buffs
				for (Buff buff : ch.buffs() ) {
					if (buff instanceof Terror)
						buff.detach();
					if (buff instanceof Dread)
						buff.detach();
					if (buff instanceof Drowsy)
						Buff.affect(ch, MagicalSleep.class);
					if (((Mob)ch).state == ((Mob)ch).SLEEPING)
						Buff.affect(ch, MagicalSleep.class);
					if (buff instanceof Corruption)
						ch.damage(Math.round(ch.HT * 0.15f), this);
				}

				if (ch.buff(Charm.class) == null && (ch.properties().contains(Char.Property.UNDEAD) || ch.properties().contains( Char.Property.MINIBOSS) || ch.properties().contains(Char.Property.BOSS)) ) {
					duration /= 2;
					GLog.w( Messages.get(this,"undead_resist"));

				}

//				if ( ch.HP <= ch.HT * 0.5f ) {
//					duration /= 2;
//					GLog.w( Messages.get(this,"hurt_resist"));
//
//				}
//				if (((Mob)ch).state == ((Mob)ch).PASSIVE && ((Mob)ch).isTargeting(Dungeon.hero)  ) {
//					duration /= 2;
//					GLog.w( Messages.get(this,"aggro_resist"));
//				}


				Charm charm = Buff.extend(ch, Charm.class, duration );
				charm.object = curUser.id();
				charm.ignoreHeroAllies = true;
				ch.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 2 + (buffedLvl() / 6) );

				if (ch.buff(Charm.class) != null && !ch.properties().contains(Char.Property.BOSS) && Dungeon.hero.buff(PhantomStopwatch.timeFreeze.class) == null && charm.cooldown() > Math.max(25, (duration * 4) + 1)) {
					GLog.p("The " + ch.name() + " loves you!");
					AllyBuff.affectAndLoot((Mob) ch, curUser, InLove.class);

					//ch.alignment = Char.Alignment.ALLY;
					//((Mob) ch).state = ((Mob) ch).PASSIVE;
					//((Mob)ch).aggro(null);
					//((Mob)ch).isTargeting(Dungeon.hero);

					Buff.prolong(ch, LifeLink.class, duration * 10).object = Dungeon.hero.id();

					int healing = Math.round(((buffedLvl()/20f) + 0.05f) * Dungeon.hero.HT);
					Dungeon.hero.HP = Math.min(Dungeon.hero.HP + healing, Dungeon.hero.HT);

//					enemyInLove = ch;
//					System.out.println("enemyInLove: " + enemyInLove.name());
//			        if (enemyInLove != null && enemyInLove.fieldOfView[Dungeon.hero.pos] && Dungeon.level.heroFOV[enemyInLove.pos]){
//			        }

					Sample.INSTANCE.play( Assets.Sounds.PUFF, 0.9f );
					ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(ch.HT), FloatingText.HEALING);
					ch.buff(Charm.class).detach();
				}

			}
		}
	}

	//This is only for battlemage
	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		if (defender.buff(Charm.class) == null){
			float healToGive = (2 + (buffedLvl() * 2f) ) / 100f * procChanceMultiplier(attacker) ;
			if (Random.Float() < 0.3 ) {
				//Dungeon.hero.HP = (int) Math.min(Dungeon.hero.HT, healToGive * Dungeon.hero.HT);
				attacker.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1 + (buffedLvl() / 5));
				Dungeon.hero.HP = Math.min( Dungeon.hero.HT, Dungeon.hero.HP + (int)(healToGive * Dungeon.hero.HT) );
			}
		}
	}

	@Override
	public void fx(Ballistica beam, Callback callback) {

		MagicMissile.boltFromChar(curUser.sprite.parent,
				MagicMissile.CHARM,
				curUser.sprite,
				beam.collisionPos,
				callback);

		Sample.INSTANCE.play( Assets.Sounds.ZAP );
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color( 0xFF00A2 );
		particle.am = 0.8f;
		particle.setLifespan(1f);
		particle.speed.polar( Random.Float(PointF.PI2), 2f );
		particle.setSize( 1f, 2f);
		particle.radiateXY(0.5f);
	}

	@Override
	public String statsDesc() {
		int duration = Math.round(Charm.DURATION/5f ) + (buffedLvl()/2) + 2; //needs to add an extra turn to match actual duration for some reason
		if (levelKnown)
			return Messages.get(this, "stats_desc", duration);
		else
			return Messages.get(this, "stats_desc", 4);
	}

	@Override
	public String upgradeStat1(int level) {
		int duration = Math.round(Charm.DURATION/5f ) + (level / 2) + 2; //needs to add an extra turn to match actual duration for some reason
		return Integer.toString(duration);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
	}

	public static class InLove extends AllyBuff {

		{
			type = buffType.NEGATIVE;
			announced = true;
		}

		@Override
		public void fx(boolean on) {
			if (on) target.sprite.add( CharSprite.State.PINKIFIED );
			else if (target.invisible == 0) target.sprite.remove( CharSprite.State.PINKIFIED );
		}

		@Override
		public int icon() {
			return BuffIndicator.HEART;
		}
	}

}
