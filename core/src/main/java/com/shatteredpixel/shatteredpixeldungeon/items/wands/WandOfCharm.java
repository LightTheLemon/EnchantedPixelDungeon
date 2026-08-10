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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
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

			//heals allies
			if (ch.alignment == Char.Alignment.ALLY){

				//Might be interesting to charm allies later on
				//Buff.prolong( ch, Charm.class, Charm.DURATION ).object = curUser.id();

				if (ch.HP < ch.HT) {
					int healing = Math.max(2, buffedLvl() );
					ch.HP = Math.min(ch.HP + healing, ch.HT);
					ch.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1 + (buffedLvl() / 4));
					ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(healing), FloatingText.HEALING);
				} else {
					/*
					float shielding = Math.max(5, (buffedLvl() / 50f) * ch.HT + 1);
					Buff.affect(ch, Barrier.class).setShield(  (int) shielding );
					ch.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString( (int)shielding ), FloatingText.SHIELDING );
					 */
					Barrier barrier = Buff.affect(ch, Barrier.class);
					int currentShield = barrier.shielding();
					int addedShield = (int)(20f / Math.sqrt(currentShield + 1));

					barrier.setShield(currentShield + addedShield);


				}


			} else if (ch.alignment == Char.Alignment.ENEMY || ch instanceof Mimic) {

				//// Note: if needed, make the charm effect weaker on undead enemies
				int duration = (int) (( Charm.DURATION/5f ) + (buffedLvl()/3f) + 1);
				if (ch.properties().contains(Char.Property.BOSS) || ch.properties().contains(Char.Property.UNDEAD)) {
					Buff.extend(ch, Charm.class, ( duration ) / 2f);
					GLog.i("This creature resists the charm effect, halving its duration");
				} else {
					Charm charm = Buff.extend(ch, Charm.class, duration );
					charm.object = curUser.id();
					charm.ignoreHeroAllies = true;
					ch.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 2 + (buffedLvl() / 5) );
				}

				//charms living enemies
				//if (!ch.properties().contains(Char.Property.UNDEAD)) {
				//	Charm charm = Buff.prolong(ch, Charm.class, Charm.DURATION/2f);
				//	charm.object = curUser.id();
				//	charm.ignoreHeroAllies = true;
				//	ch.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 3 );
				
				//harms the undead
				//} else {
				//	ch.damage(damageRoll(), this);
				//	ch.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10 + buffedLvl());
				//	Sample.INSTANCE.play(Assets.Sounds.BURNING);
				//}

			}
			
		}
		
	}

	@Override //This is only for battlemage
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		if (defender.buff(Charm.class) == null){
			float healToGive = (2 + (buffedLvl() / 2f) + procChanceMultiplier(attacker) ) / 100f ;
			if (Math.random() < 0.5 ) {
				//Dungeon.hero.HP = (int) Math.min(Dungeon.hero.HT, healToGive * Dungeon.hero.HT);
				attacker.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1 + (buffedLvl() / 5));
				Dungeon.hero.HP = Math.min( Dungeon.hero.HT, Dungeon.hero.HP + (int)(healToGive * Dungeon.hero.HT) );

			}
		}
	}

	@Override
	public void fx(Ballistica beam, Callback callback) {

		//curUser.sprite.parent.add(
		//new Beam.HealthRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));

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
		particle.am = 0.9f; //was 0.6f
		particle.setLifespan(1f);
		particle.speed.polar( Random.Float(PointF.PI2), 2f );
		particle.setSize( 1f, 2f);
		particle.radiateXY(0.5f);
	}

	@Override
	public String statsDesc() {
		int duration = Math.round(Charm.DURATION/5f ) + (buffedLvl()/3) + 2; //+2 instead of +1 to account for casting to an int
		if (levelKnown)
			return Messages.get(this, "stats_desc", duration);
		else
			return Messages.get(this, "stats_desc", 4);
	}

	@Override
	public String upgradeStat1(int level) {
		int duration = Math.round(Charm.DURATION/5f ) + (level / 3) + 2; //+2 instead of +1 to account for casting to an int
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

}
