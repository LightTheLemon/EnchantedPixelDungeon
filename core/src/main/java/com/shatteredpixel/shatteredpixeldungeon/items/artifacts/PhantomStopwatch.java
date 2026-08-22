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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class PhantomStopwatch extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_WATCH;

		levelCap = 10;
		charge = 10+(level() * 2); //value of each charge is decreased

		partialCharge = 0;
		chargeCap = 10+(level() * 2);

		exp = 0;

		defaultAction = AC_ACTIVATE;
	}

	@Override
	public void resetForTrinity(int visibleLevel) {
		super.resetForTrinity(visibleLevel);
		charge = visibleLevel/2 - 1; //grants 4-10 turns of time freeze
	}

	public static final String AC_ACTIVATE = "ACTIVATE";

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped( hero )
				&& !cursed
				&& hero.buff(MagicImmune.class) == null
				&& (charge > 0 || activeBuff != null)) {
			actions.add(AC_ACTIVATE);
		}
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute(hero, action);

		if (hero.buff(MagicImmune.class) != null) return;

		if (action.equals(AC_ACTIVATE)){

			if (!isEquipped( hero ))        GLog.i( Messages.get(Artifact.class, "need_to_equip") );
			else if (activeBuff != null) {
					activeBuff.detach();
					hero.spend(1);
					GLog.i( Messages.get(this, "deactivate") );
			} else if (charge <= 0)         GLog.i( Messages.get(this, "no_charge") );
			else if (cursed)                GLog.i( Messages.get(this, "cursed") );
			else {
				for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
					if (Dungeon.level.heroFOV[mob.pos]) {
						artifactProc(mob, visiblyUpgraded(), 1);
					}
				}

				GLog.i( Messages.get(PhantomStopwatch.class, "onfreeze") );
				GameScene.flash(0x80FFFFFF);
				Sample.INSTANCE.play(Assets.Sounds.TELEPORT);

				//Invisibility.dispel(Dungeon.hero);
				activeBuff = new timeFreeze();
				Talent.onArtifactUsed(Dungeon.hero);
				activeBuff.attachTo(Dungeon.hero);
				//charge--;
				((timeFreeze)activeBuff).processTime(0f);
				}

		}
	}

	@Override
	public void activate(Char ch) {
		super.activate(ch);
		if (activeBuff != null)
			activeBuff.attachTo(ch);
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (super.doUnequip(hero, collect, single)){
			if (activeBuff != null){
				activeBuff.detach();
				activeBuff = null;
			}
			return true;
		} else
			return false;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new watchRecharge();
	}
	
	@Override
	public void charge(Hero target, float amount) {
		if (charge < chargeCap && !cursed && target.buff(MagicImmune.class) == null){
			partialCharge += 0.2f*amount; //from 0.25f*amount
			while (partialCharge >= 1){
				partialCharge--;
				charge++;
			}
			if (charge >= chargeCap){
				partialCharge = 0;
			}
			updateQuickslot();
		}
	}

	@Override
	public Item upgrade() {
		chargeCap = Math.min(chargeCap + 2, 25);

		return super.upgrade();
	}

	@Override
	public String desc() {
		String desc = super.desc();

		if (isEquipped( Dungeon.hero )){
			if (!cursed) {
				if (level() < levelCap )
					desc += "\n\n" + Messages.get(this, "desc_hint");

			} else
				desc += "\n\n" + Messages.get(this, "desc_cursed");
		}
		return desc;
	}
	private static final String BUFF =      "buff";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);

		if (activeBuff != null)
			bundle.put( BUFF , activeBuff );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);

		//these buffs belong to hourglass, need to handle unbundling within the hourglass class.
		if (bundle.contains( BUFF )){
			Bundle buffBundle = bundle.getBundle( BUFF );

			if (buffBundle.contains( timeFreeze.PRESSES ))
				activeBuff = new timeFreeze();

			activeBuff.restoreFromBundle(buffBundle);
		}
	}


	public class watchRecharge extends ArtifactBuff {
		@Override
		public boolean act() {
			if (charge < chargeCap
					&& !cursed
					&& target.buff(MagicImmune.class) == null
					&& Regeneration.regenOn()
					&& activeBuff == null) {
				float chargeGain = 1 / (75f ); // was (100f - (chargeCap - charge)*3f)
				chargeGain *= RingOfEnergy.artifactChargeMultiplier(target);
				partialCharge += chargeGain;

				while (partialCharge >= 1) {
					partialCharge --;
					charge ++;

					if (charge == chargeCap){
						partialCharge = 0;
					}
				}
			} else if (cursed && Random.Int(10) == 0)
				((Hero) target).spend( TICK );


			int lvlDiffFromTarget = ((Hero) target).lvl - (1+level()*2);

			updateQuickslot();

			spend( TICK );

			return true;
		}

		public void gainExp(float levelPortion) {
			if (cursed || target.buff(MagicImmune.class) != null || levelPortion == 0) return;

			exp += Math.round(levelPortion*100);

			//past the soft charge cap, gaining  charge from leveling is slowed.
			if (charge > 5+(level()*2)){
				levelPortion *= (5+((float)level()*2))/charge;
			}
			partialCharge += levelPortion*6f;

			if (exp > 100+level()*100 && level() < levelCap){
				exp -= 100+level()*100;
				GLog.p( Messages.get(this, "levelup") );
				Catalog.countUses(PhantomStopwatch.class, 2);
				upgrade();
			}

		}




	}

	public class timeFreeze extends ArtifactBuff {
		
		{
			type = buffType.POSITIVE;
		}

		float turnsToCost = 0f;

		ArrayList<Integer> presses = new ArrayList<>();

		public void processTime(float timePass){
			// Buffer the time spent into your float tracker
			turnsToCost -= timePass;

			// Only drop a charge when a full 1.0 turn has accumulated
			while (turnsToCost <= -1f) {
				turnsToCost += 1f;
				charge--;
			}

			updateQuickslot();

			if (charge <= 0){
				charge = 0;
				detach();
			}
		}

		public void setDelayedPress(int cell){
			if (!presses.contains(cell))
				presses.add(cell);
		}

		public void triggerPresses(){
			ArrayList<Integer> toTrigger = presses;
			presses = new ArrayList<>();
			Actor.add(new Actor() {
				{
					actPriority = VFX_PRIO;
				}

				@Override
				protected boolean act() {
					for (int cell : toTrigger){
						Plant p = Dungeon.level.plants.get(cell);
						if (p != null){
							p.trigger();
						}
						Trap t = Dungeon.level.traps.get(cell);
						if (t != null){
							t.trigger();
						}
					}
					Actor.remove(this);
					return true;
				}
			});
		}

		public void disarmPresses(){
			for (int cell : presses){
				Plant p = Dungeon.level.plants.get(cell);
				if (p != null && !(p instanceof Rotberry)) {
					Dungeon.level.uproot(cell);
				}
				Trap t = Dungeon.level.traps.get(cell);
				if (t != null && t.disarmedByActivation) {
					t.disarm();
				}
			}

			presses = new ArrayList<>();
		}

		@Override
		public void detach(){
			updateQuickslot();
			super.detach();
			activeBuff = null;
			triggerPresses();
			target.next();
		}

		@Override
		public void fx(boolean on) {
			if (!(target instanceof Hero)) return;
			Emitter.freezeEmitters = on;
			if (on){
				for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
					if (mob.sprite != null) mob.sprite.add(CharSprite.State.PARALYSED);
				}
			} else {
				for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
					if (mob.paralysed <= 0) mob.sprite.remove(CharSprite.State.PARALYSED);
				}
			}
		}

		@Override
		public int icon() {
			return BuffIndicator.TIME;
		}

		/*
		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(1f, 0.5f, 0);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (2f - turnsToCost) / 2f);
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString((int)(turnsToCost + 0.001f));
		}



		@Override
		public String desc() {
			return Messages.get(this, "desc");
		}
		*/
		private static final String PRESSES = "presses";
		private static final String TURNSTOCOST = "turnsToCost";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);

			int[] values = new int[presses.size()];
			for (int i = 0; i < values.length; i ++)
				values[i] = presses.get(i);
			bundle.put( PRESSES , values );

			bundle.put( TURNSTOCOST , turnsToCost);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);

			int[] values = bundle.getIntArray( PRESSES );
			for (int value : values)
				presses.add(value);

			turnsToCost = bundle.getFloat( TURNSTOCOST );
		}
	}

}

