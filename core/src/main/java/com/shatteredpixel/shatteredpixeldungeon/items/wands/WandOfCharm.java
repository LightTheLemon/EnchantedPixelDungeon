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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
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

				//not sure what the line below does
				//Buff.prolong( ch, Charm.class, Charm.DURATION ).object = curUser.id();

				int healing = Math.max(1, buffedLvl() * 2);
				ch.HP = Math.min(ch.HP + healing, ch.HT);
				ch.sprite.emitter().burst(Speck.factory(Speck.HEALING), 2 + buffedLvl() / 2);

				ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(healing), FloatingText.HEALING);

			} else if (ch.alignment == Char.Alignment.ENEMY || ch instanceof Mimic) {

				Charm charm = Buff.extend(ch, Charm.class, ( Charm.DURATION/5f ) + buffedLvl() );
				charm.object = curUser.id();
				charm.ignoreHeroAllies = true;
				ch.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 2 + (buffedLvl() / 5) );

				//// Note: if needed, make the charm effect weaker on undead enemies
				
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
			//int healToGive = Math.round((2*(5 + buffedLvl()))*procChanceMultiplier(attacker));
			Buff.affect(attacker, Healing.class);
			attacker.sprite.emitter().burst(Speck.factory(Speck.HEALING), 10);
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
		int duration = Math.round(Charm.DURATION/5f ) + buffedLvl();
		if (levelKnown)
			return Messages.get(this, "stats_desc", duration);
		else
			return Messages.get(this, "stats_desc", 3);
	}

	@Override
	public String upgradeStat1(int level) {
		int duration = Math.round(Charm.DURATION/5f ) + buffedLvl();
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
