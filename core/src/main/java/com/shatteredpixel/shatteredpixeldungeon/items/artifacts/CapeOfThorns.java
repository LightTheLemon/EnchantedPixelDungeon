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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import static com.shatteredpixel.shatteredpixeldungeon.actors.Actor.TICK;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class CapeOfThorns extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_CAPE;

		levelCap = 10;

		charge = 0;
		chargeCap = 100;
		cooldown = 0;

		defaultAction = AC_ACTIVATE;
	}

	private boolean isFullyCharged = false;
	private float partialCooldown = 0f;
	public static final String AC_ACTIVATE = "ACTIVATE";

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Thorns();
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero)
				&& hero.buff(MagicImmune.class) == null
				&& !cursed) {
			actions.add(AC_ACTIVATE);
		}
		return actions;
	}
	
	@Override
	public void charge(Hero target, float amount) {
		if (cooldown == 0) {
			charge += Math.round(4*amount);
			updateQuickslot();
		} else if (cooldown < 0) {
			cooldown = 0;
		}
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (hero.buff(MagicImmune.class) != null) return;

		if (action.equals(AC_ACTIVATE)){

			curUser = hero;

			if (charge <= 0) {
				GLog.i( Messages.get(this, "no_charge") );
				charge = 0;
				usesTargeting = false;

			} else if (cursed) {
				GLog.w( Messages.get(this, "cursed") );
				usesTargeting = false;

			} else {
				CapeOfThorns.Thorns thorns = Dungeon.hero.buff(CapeOfThorns.Thorns.class);
				if (thorns != null) {
					cooldown = charge;
					charge = 0;
					isFullyCharged = false;
					GLog.p( Messages.get(this, "radiating") );
					Sample.INSTANCE.play( Assets.Sounds.ROCKS );
				}
				hero.spendAndNext(TICK);
				updateQuickslot();
			}

		}
	}
	
	@Override
	public String desc() {
		String desc = Messages.get(this, "desc");
		if (isEquipped( Dungeon.hero )) {
			desc += "\n\n";
            if (!cursed) {
                if (cooldown == 0)
                    desc += Messages.get(this, "desc_inactive");
                else
                    desc += Messages.get(this, "desc_active");
            } else {
				desc += Messages.get(this, "desc_cursed");
			}
        }

		return desc;
	}

	public class Thorns extends ArtifactBuff{

		@Override
		public boolean act(){
			//if (cooldown > 0) {
			//	//cooldown -= damage;
			//	if (cooldown == 0) {
			//		GLog.w( Messages.get(this, "inert") );
			//	}
			//	updateQuickslot();
			//}

			if (cursed && Dungeon.hero.buff(Vulnerable.class) == null && Random.Int(15) == 0) {
				Buff.affect(Dungeon.hero, Vulnerable.class, Vulnerable.DURATION / 2);
			}
			updateQuickslot();
			spend(TICK);
			if (Dungeon.hero.buff(Thorns.class) != null) {
				partialCooldown += 0.1f;
				if (partialCooldown >= 1f) {
					partialCooldown = 0f;
					cooldown = Math.max(0, cooldown - 1);
					updateQuickslot();
				}
			}

			return true;
		}

		public int proc(int damage, Char attacker, Char defender){
			if (cooldown == 0){
				float partialCharge = (damage*0.30f) + ((level()*0.05f) );
				//charge go up with xp
				charge += partialCharge;
				if (charge >= chargeCap){
					charge = chargeCap;
					if (!isFullyCharged) {
						isFullyCharged = true;
						GLog.p( Messages.get(this, "fully_charged") );
					}
				}
			}

			if (cooldown != 0){
				cooldown = Math.max(0, cooldown - (damage / ( 1 + (level() / 10) )));
				int deflected = Random.NormalIntRange(level() / 2, damage * ( 1 + (level() / 10) ));
				System.out.println(deflected);
				damage -= deflected;

				if (defender == null) defender = target;
				if (attacker != null && defender != null && Dungeon.level.adjacent(attacker.pos, defender.pos)) {
					attacker.damage(deflected, this);
				}

				exp += deflected;

				if (exp >= (level()+1)*5 && level() < levelCap){
					exp -= (level()+1)*5;
					GLog.p( Messages.get(this, "levelup") );
					Catalog.countUse(CapeOfThorns.class);
					upgrade();
				}
			}

			updateQuickslot();
			return damage;
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", dispTurns(cooldown));
		}

		@Override
		public int icon() {
			if (cooldown == 0)
				return BuffIndicator.NONE;
			else
				return BuffIndicator.THORNS;
		}

		@Override
		public void detach(){
			cooldown = 0;
			super.detach();
		}

	}


}
