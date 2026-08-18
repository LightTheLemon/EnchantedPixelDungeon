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
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Regrowth;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PitfallParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PoisonParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfSirensSong;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.WondrousResin;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.*;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Math.*;

//helper class to contain all the cursed wand zapping logic, so the main wand class doesn't get huge.
public class CursedWand {

	public static void cursedZap(final Item origin, final Char user, final Ballistica bolt, final Callback afterZap){

		boolean positiveOnly = user == Dungeon.hero && Random.Float() < WondrousResin.positiveCurseEffectChance();
		CursedEffect effect = randomValidEffect(origin, user, bolt, positiveOnly);

		effect.FX(origin, user, bolt, new Callback() {
			@Override
			public void call() {
				effect.effect(origin, user, bolt, positiveOnly);
				System.out.println("Curse effect type " + effect.getClass());
				afterZap.call();
			}
		});
	}

	public static void tryForWandProc( Char target, Item origin ){
		if (target != null && target != Dungeon.hero && origin instanceof Wand){
			Wand.wandProc(target, wandLevel(origin), 1);
		}
	}

	public static boolean randomUsingLevel(int level){
		return (Math.random() < (1- Math.pow(0.8, level)));
	}

	public static int wandLevel(Item wand){
		if(wand != null)
			return wand.buffedLvl();
		return 0;
	}

	//*** Cursed Effects ***

	public static abstract class CursedEffect {

		public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly){
			return true;
		}

		public void FX(final Item origin, final Char user, final Ballistica bolt, final Callback callback){
			MagicMissile.boltFromChar(user.sprite.parent,
					MagicMissile.RAINBOW,
					user.sprite,
					bolt.collisionPos,
					callback);
			Sample.INSTANCE.play( Assets.Sounds.ZAP );
		}

		public abstract boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly);

	}

	// common/uncommon/rare/v.rare have a 60/30/9/1% chance respectively
	private static float[] EFFECT_CAT_CHANCES = new float[]{60, 30, 9, 1};

	public static CursedEffect randomEffect(){
		switch (Random.chances(EFFECT_CAT_CHANCES)){
			case 0: default:
				return randomCommonEffect();
			case 1:
				return randomUncommonEffect();
			case 2:
				return randomRareEffect();
			case 3:
				return randomVeryRareEffect();
		}
	}

	public static CursedEffect randomValidEffect(Item origin, Char user, Ballistica bolt, boolean positiveOnly){
		switch (Random.chances(EFFECT_CAT_CHANCES)){
			case 0: default:
				return randomValidCommonEffect(origin, user, bolt, positiveOnly);
			case 1:
				return randomValidUncommonEffect(origin, user, bolt, positiveOnly);
			case 2:
				return randomValidRareEffect(origin, user, bolt, positiveOnly);
			case 3:
				return randomValidVeryRareEffect(origin, user, bolt, positiveOnly);
		}
	}

	//**********************
	//*** Common Effects ***
	//**********************

	private static ArrayList<CursedEffect> COMMON_EFFECTS = new ArrayList<>();
	static {
		COMMON_EFFECTS.add(new BurnAndFreeze());
		COMMON_EFFECTS.add(new SpawnRegrowth());
		COMMON_EFFECTS.add(new RandomTeleport());
		COMMON_EFFECTS.add(new RandomGas());
		COMMON_EFFECTS.add(new RandomAreaEffect());
		COMMON_EFFECTS.add(new Bubbles());
		COMMON_EFFECTS.add(new SelfOoze());
		COMMON_EFFECTS.add(new Explosion());
		COMMON_EFFECTS.add(new SummonSheep());
	}

	public static CursedEffect randomCommonEffect(){
		return Random.element(COMMON_EFFECTS);
	}

	public static CursedEffect randomValidCommonEffect(Item origin, Char user, Ballistica bolt, boolean positiveOnly){
		CursedEffect effect;
		do {
			effect = Random.element(COMMON_EFFECTS);
		} while (!Objects.requireNonNull(effect).valid(origin, user, bolt, positiveOnly));
		return effect;
	}

	public static class BurnAndFreeze extends CursedEffect {
		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			Char target = Actor.findChar(bolt.collisionPos);
			//doesn't affect caster if positive only
			if (Random.Int(2) == 0) {
				if (target != null) Buff.affect(target, Burning.class).reignite(target, (float) (Burning.DURATION + Math.floor(wandLevel(origin) *2f)));
				if (!positiveOnly) Buff.affect(user, Frost.class, (float) (Frost.DURATION + Math.floor(wandLevel(origin) *2.5f)));
			} else {
				if (!positiveOnly)Buff.affect(user, Burning.class).reignite(user, (float) (Burning.DURATION + Math.floor(wandLevel(origin) *2f)));
				if (target != null) Buff.affect(target, Frost.class, (float) (Frost.DURATION + Math.floor(wandLevel(origin) *2.5f)));
			}
			tryForWandProc(target, origin);
			return true;
		}
	}

	public static class SpawnRegrowth extends CursedEffect {
		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean postiveOnly) {
			if (Actor.findChar(bolt.collisionPos) == null){
				Dungeon.level.pressCell(bolt.collisionPos);
			}
			GameScene.add( Blob.seed(bolt.collisionPos, 30 + wandLevel(origin) * 3, Regrowth.class));
			tryForWandProc(Actor.findChar(bolt.collisionPos), origin);
			return true;
		}
	}

	public static class RandomTeleport extends CursedEffect {
		@Override
		public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			Char target = Actor.findChar(bolt.collisionPos);
			if (positiveOnly && (target == null || Char.hasProp(target, Char.Property.IMMOVABLE))){
				return false;
			}
			return true;
		}

		//might be nice to have no fx if self teleports?

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			Char target = Actor.findChar( bolt.collisionPos );
			//can only teleport target if positive only
			if (target != null && !Char.hasProp(target, Char.Property.IMMOVABLE) && (positiveOnly || Random.Int(2) == 0)){
				ScrollOfTeleportation.teleportChar(target);
				Buff.affect(target, Daze.class,(float) (Math.floor(wandLevel(origin) *2.5f)));
				Buff.affect(target, Haste.class,(float) (Math.floor(wandLevel(origin) *1.8f)));
				tryForWandProc(target, origin);
				return true;
			} else {
				if (positiveOnly || user == null || Char.hasProp(user, Char.Property.IMMOVABLE)){
					return false;
				} else if (target != null) {
					ScrollOfTeleportation.teleportChar(user);
					Buff.affect(user, Daze.class,(float) (Math.floor(wandLevel(origin) * 2.5f)));
					Buff.affect(target, Haste.class,(float) (Math.floor(wandLevel(origin) *1.8f)));
					return true;
				} else {
					return false;
				}
			}
		}
	}

	public static class RandomGas extends CursedEffect {
		private static final int LEVELS_REQUIRED_TO_DOUBLE = 10;
		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			Sample.INSTANCE.play( Assets.Sounds.GAS );
			tryForWandProc(Actor.findChar(bolt.collisionPos), origin);
			if (Actor.findChar(bolt.collisionPos) == null){
				Dungeon.level.pressCell(bolt.collisionPos);
			}
			switch (Random.Int(3)) {
				case 0: default:
					GameScene.add( Blob.seed( bolt.collisionPos, 800 * (1 + wandLevel(origin)/LEVELS_REQUIRED_TO_DOUBLE), ConfusionGas.class ) );
					return true;
				case 1:
					GameScene.add( Blob.seed( bolt.collisionPos, 500 * (1 + wandLevel(origin)/LEVELS_REQUIRED_TO_DOUBLE), ToxicGas.class ) );
					return true;
				case 2:
					GameScene.add( Blob.seed( bolt.collisionPos, 200 * (1 + wandLevel(origin)/LEVELS_REQUIRED_TO_DOUBLE), ParalyticGas.class ) );
					return true;
			}
		}
	}

	public static class RandomAreaEffect extends CursedEffect {
		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			tryForWandProc(Actor.findChar(bolt.collisionPos), origin);
			if (Actor.findChar(bolt.collisionPos) == null){
				Dungeon.level.pressCell(bolt.collisionPos);
			}
			switch (Random.Int(3)) {
				case 0: default:
					if (randomUsingLevel(wandLevel(origin)))
						new BlazingTrap().set(bolt.collisionPos).activate();
					else
						new BurningTrap().set(bolt.collisionPos).activate();
					return true;
				case 1:
					if (randomUsingLevel(wandLevel(origin)))
						new FrostTrap().set(bolt.collisionPos).activate();
					else
						new ChillingTrap().set(bolt.collisionPos).activate();
					return true;
				case 2:
					if (randomUsingLevel(wandLevel(origin)))
						new StormTrap().set(bolt.collisionPos).activate();
					else
						new ShockingTrap().set(bolt.collisionPos).activate();
					return true;
			}
		}
	}

	public static class Bubbles extends CursedEffect {

		@Override
		public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
			MagicMissile.boltFromChar(user.sprite.parent,
					MagicMissile.SPECK + Speck.BUBBLE,
					user.sprite,
					bolt.collisionPos,
					callback);
			Sample.INSTANCE.play( Assets.Sounds.ZAP );
		}

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			if (Actor.findChar(bolt.collisionPos) == null){
				Dungeon.level.pressCell(bolt.collisionPos);
			}
			tryForWandProc(Actor.findChar(bolt.collisionPos), origin);
			for (int i : PathFinder.NEIGHBOURS9){
				if (!Dungeon.level.solid[bolt.collisionPos+i]){
					CellEmitter.get(bolt.collisionPos+i).start(Speck.factory(Speck.BUBBLE), 0.25f * 1/(1 + wandLevel(origin)), 40 * (1+ wandLevel(origin)/4));
				}
			}
			return true;
		}
	}

	public static class RandomWand extends CursedEffect {

		private Wand wand;

		@Override
		public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			//we have this limit atm because some wands are coded to depend on their fx logic
			// and chaos elementals trigger the effect directly, with no FX first
			return super.valid(origin, user, bolt, positiveOnly) && user instanceof Hero;
		}

		@Override
		public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
			if (wand == null){
				wand = (Wand)Generator.randomUsingDefaults(Generator.Category.WAND);
			}
			wand.fx(bolt, callback);
		}

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			if (wand == null){
				if (randomUsingLevel(wandLevel(origin)))
					try {
						wand = (Wand) origin.getClass().getConstructor().newInstance();
					} catch ( NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e){
						wand = (Wand)Generator.randomUsingDefaults(Generator.Category.WAND);
						e.printStackTrace();
					}
				else
					wand = (Wand)Generator.randomUsingDefaults(Generator.Category.WAND);
			}
			if (origin instanceof Wand){
				wand.upgrade(origin.level());
			} else {
				wand.upgrade(Dungeon.scalingDepth()/5);
			}
			wand.levelKnown = false;
			wand.onZap(bolt);
			wand = null;
			return true;
		}
	}

	public static class SelfOoze extends CursedEffect{

		@Override
		public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
			callback.call(); //no fx
		}

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			PathFinder.buildDistanceMap(user.pos, BArray.not( Dungeon.level.solid, null ), 2 + (int) sqrt(wandLevel(origin) * 2f) );
			for (int i = 0; i < PathFinder.distance.length; i++) {
				if (PathFinder.distance[i] < Integer.MAX_VALUE) {
					Splash.at( i, 0x000000, 5);
					Char ch = Actor.findChar(  i );
					//does not harm hero or allies when positive only
					if (ch != null && (!positiveOnly || ch.alignment != Char.Alignment.ALLY)){
						Buff.affect(ch, Ooze.class).set( Ooze.DURATION + (float) (Math.floor(wandLevel(origin) * 3.5f)) );
					}
				}
			}
			Sample.INSTANCE.play(Assets.Sounds.SHATTER);
			return true;
		}
	}

	//************************
	//*** Uncommon Effects ***
	//************************

	private static ArrayList<CursedEffect> UNCOMMON_EFFECTS = new ArrayList<>();
	static {
		UNCOMMON_EFFECTS.add(new RandomPlant());
		UNCOMMON_EFFECTS.add(new HealthTransfer());
		UNCOMMON_EFFECTS.add(new LightningBolt());
		UNCOMMON_EFFECTS.add(new Geyser());
		UNCOMMON_EFFECTS.add(new RandomWand());
		UNCOMMON_EFFECTS.add(new Levitate());
		UNCOMMON_EFFECTS.add(new Alarm());
	}

	public static CursedEffect randomUncommonEffect(){
		return Random.element(UNCOMMON_EFFECTS);
	}

	public static CursedEffect randomValidUncommonEffect(Item origin, Char user, Ballistica bolt, boolean positiveOnly){
		CursedEffect effect;
		do {
			effect = Random.element(UNCOMMON_EFFECTS);
		} while (!effect.valid(origin, user, bolt, positiveOnly));
		return effect;
	}

	public static class RandomPlant extends CursedEffect {

		@Override
		public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			int pos = bolt.collisionPos;

			if (Dungeon.level.map[pos] != Terrain.ALCHEMY
					&& !Dungeon.level.pit[pos]
					&& Dungeon.level.traps.get(pos) == null
					&& !Dungeon.isChallenged(Challenges.NO_HERBALISM)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			if (valid(origin, user, bolt, positiveOnly)) {
				tryForWandProc(Actor.findChar(bolt.collisionPos), origin);
				Plant.Seed plant =  (Plant.Seed) Generator.randomUsingDefaults(Generator.Category.SEED);
				Dungeon.level.plant(plant, bolt.collisionPos);
				PathFinder.buildDistanceMap(bolt.collisionPos, BArray.not( Dungeon.level.solid, null ), (int) sqrt(wandLevel(origin) / 2f) );
				for (int i = 0; i < PathFinder.distance.length; i++) {
					if (PathFinder.distance[i] < Integer.MAX_VALUE && i != bolt.collisionPos && randomUsingLevel(wandLevel(origin))) {
						Dungeon.level.plant(plant, i);
					}
				}
				return true;
			} else {
				return false;
			}
		}
	}

	public static class HealthTransfer extends CursedEffect {

		@Override
		public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			return Actor.findChar( bolt.collisionPos ) != null;
		}

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			final Char target = Actor.findChar( bolt.collisionPos );
			if (target != null) {
				int damage = (int) (Dungeon.scalingDepth() * 2 * (1 + wandLevel(origin)/6f));
				Char toHeal, toDamage;

				//can only harm target if positive only
				if (positiveOnly || Random.Int(2) == 0){
					toHeal = user;
					toDamage = target;
				} else {
					toHeal = target;
					toDamage = user;
				}
				toHeal.HP = min(toHeal.HT, toHeal.HP + damage/2);
				toHeal.sprite.emitter().burst(Speck.factory(Speck.HEALING), 3);
				toHeal.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(damage/2), FloatingText.HEALING );

				toDamage.damage(damage, new CursedWand());
				toDamage.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);

				if (toDamage == Dungeon.hero){
					Sample.INSTANCE.play(Assets.Sounds.CURSED);
					if (!toDamage.isAlive()) {
						if (user == Dungeon.hero && origin != null) {
							Badges.validateDeathFromFriendlyMagic();
							Dungeon.fail( origin );
							GLog.n( Messages.get( CursedWand.class, "ondeath", origin.name() ) );
						} else {
							Badges.validateDeathFromEnemyMagic();
							Dungeon.fail( toHeal );
						}
					}
				} else {
					Sample.INSTANCE.play(Assets.Sounds.BURNING);
				}
				tryForWandProc(target, origin);
				return true;
			} else {
				return false;
			}
		}
	}

	public static class Explosion extends CursedEffect {
		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			new Bomb.ConjuredBomb().explode(bolt.collisionPos);
			PathFinder.buildDistanceMap(bolt.collisionPos, BArray.not( Dungeon.level.solid, null ), (int) sqrt(wandLevel(origin) / 2f) );
			for (int i = 0; i < PathFinder.distance.length; i++) {
				if (PathFinder.distance[i] < Integer.MAX_VALUE && i != bolt.collisionPos && Math.random() < wandLevel(origin) / 20f  ) {
					new Bomb.ConjuredBomb().explode(i);
				}
			}
			tryForWandProc(Actor.findChar(bolt.collisionPos), origin);
			return true;
		}
	}

	public static class LightningBolt extends CursedEffect {

		public static boolean isIn(int i, int initial, int radius){
			System.out.println("i is: " + i + " initial is: " + initial + " radius is: " + radius + " w is: " + Dungeon.level.width());
			if(i <= (initial + radius) && i >= (initial-radius) )
				return true;
			if(i % Dungeon.level.width() == (initial) % Dungeon.level.width())
				return true;
			if(i % (Dungeon.level.width()-1) == initial % (Dungeon.level.width()-1))
				return true;
			if(i % (Dungeon.level.width()+1) == initial % (Dungeon.level.width()+1))
				return true;
			return false;
		}

		@Override
		public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
			Char ch = Actor.findChar( bolt.collisionPos );
			if (ch != null){
				user.sprite.parent.addToFront(new Lightning(user.sprite.center(), ch.sprite.center(), null));
			} else {
				user.sprite.parent.addToFront(new Lightning(user.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(bolt.collisionPos), null));
			}
			Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
			callback.call();
		}

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {

			ArrayList<Char> affected = new ArrayList<>();
			int factor =1;// + (int) sqrt(wandLevel(origin) * 2f);

			PathFinder.buildDistanceMap(user.pos, BArray.not( Dungeon.level.solid, null ), 1 + factor );
			user.sprite.parent.add(new Lightning(user.pos - (factor), user.pos + (factor), null));
			user.sprite.parent.add(new Lightning(user.pos - (Dungeon.level.width()*factor), user.pos + (Dungeon.level.width()*factor), null));
			user.sprite.parent.add(new Lightning(user.pos - (factor) - (Dungeon.level.width()*factor), user.pos + (factor) + (Dungeon.level.width()*factor), null));
			user.sprite.parent.add(new Lightning(user.pos - (factor) + (Dungeon.level.width()*factor), user.pos + (factor) - (Dungeon.level.width()*factor), null));
			System.out.println("Calling for self");
			for (int i : PathFinder.distance){
				if(i < Integer.MAX_VALUE) {
					System.out.println("I is: " + i + " userpos + i is " + (user.pos + i));
					System.out.println(" x,y userpos is: " + user.pos%Dungeon.level.width()+ ", "+user.pos/Dungeon.level.width());
					System.out.println("alleged x,y i is: " + (i+user.pos)%Dungeon.level.width()+ ", "+(i+user.pos)/Dungeon.level.width());
				}
				if (Actor.findChar(user.pos+i) != null && i < Integer.MAX_VALUE&& isIn(user.pos+i,user.pos,factor)){
					affected.add(Actor.findChar(user.pos+i));
				}
			}

			int pos = bolt.collisionPos;
			PathFinder.buildDistanceMap(pos, BArray.not( Dungeon.level.solid, null ), 1 + factor );
			user.sprite.parent.add(new Lightning(pos - (factor), pos + (factor), null));
			user.sprite.parent.add(new Lightning(pos - (Dungeon.level.width()*factor), pos + (Dungeon.level.width()*factor), null));
			user.sprite.parent.add(new Lightning(pos - (factor) - (Dungeon.level.width()*factor), pos + factor + (Dungeon.level.width()*factor), null));
			user.sprite.parent.add(new Lightning(pos - (factor) + (Dungeon.level.width()*factor), pos + factor - (Dungeon.level.width()*factor), null));
			System.out.println("Calling for bolt");
			for (int i : PathFinder.distance){
				if (Actor.findChar(pos+i) != null && !affected.contains(Actor.findChar(pos+i)) && i < Integer.MAX_VALUE && isIn(pos+i,pos,factor) ){
					affected.add(Actor.findChar(pos+i));
				}
			}

			tryForWandProc(Actor.findChar(bolt.collisionPos), origin);

			for (Char ch : affected){
				if (ch instanceof Hero) {
					Buff.prolong(ch, Recharging.class, (float) (Recharging.DURATION/3f * (1- Math.pow(0.5, wandLevel(origin))/5f)));
					ScrollOfRecharging.charge(ch);
					SpellSprite.show(ch, SpellSprite.CHARGE);
				}
				//does not harm allies if positive only
				if (ch.alignment != Char.Alignment.ALLY || !positiveOnly){
					//shocking dart damage and a little stun
				//	ch.damage((int) (Random.NormalIntRange(5 + Dungeon.scalingDepth() / 4, 10 + Dungeon.scalingDepth() / 4) * (1- Math.pow(0.5, wandLevel(origin))/5f)), new Electricity());
					if (ch.isAlive()) {
						Buff.affect(ch, Paralysis.class, (float)(Paralysis.DURATION / 2f * (1- Math.pow(0.5, wandLevel(origin))/5f)));
					} else if (ch == Dungeon.hero){
						if (user == Dungeon.hero && origin != null) {
							Badges.validateDeathFromFriendlyMagic();
							Dungeon.fail( origin );
							GLog.n( Messages.get( CursedWand.class, "ondeath", origin.name() ) );
						} else {
							Badges.validateDeathFromEnemyMagic();
							Dungeon.fail( user );
						}
					}
				}
			}

			return true;
		}
	}

	public static class Geyser extends CursedEffect{
		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			tryForWandProc(Actor.findChar(bolt.collisionPos), origin);
			GeyserTrap geyser = new GeyserTrap();
			geyser.pos = bolt.collisionPos;
			geyser.source = origin == null ? user : origin;
			geyser.activate();
			PathFinder.buildDistanceMap(user.pos, BArray.not( Dungeon.level.solid, null ), 1 + (int) sqrt(wandLevel(origin) * 2f) );
			for (int i : PathFinder.distance)
				if(randomUsingLevel(wandLevel(origin))&& i < Integer.MAX_VALUE){
					geyser.pos = i;
					geyser.activate();
				}
			return true;
		}
	}

	public static class SummonSheep extends CursedEffect{
		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			tryForWandProc(Actor.findChar(bolt.collisionPos), origin);
			new FlockTrap().set(bolt.collisionPos).activate();
			PathFinder.buildDistanceMap(user.pos, BArray.not( Dungeon.level.solid, null ), 5 + (int) sqrt(wandLevel(origin) * 3f) );
			for (int i : PathFinder.distance)
				if(randomUsingLevel(wandLevel(origin))&& i < Integer.MAX_VALUE)
					new FlockTrap().set(i).activate();
			return true;
		}
	}

	public static class Levitate extends CursedEffect {

		@Override
		public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			return positiveOnly || Actor.findChar(bolt.collisionPos) != null;
		}

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			Char ch = Actor.findChar(bolt.collisionPos);
			if ((!positiveOnly || (ch instanceof Piranha)) && ch != null && !ch.flying && !Char.hasProp(ch, Char.Property.IMMOVABLE)) {
				Buff.affect(ch, Levitation.class,Levitation.DURATION * (1 + wandLevel(origin)/5f));
			} else {
				Buff.affect(user, Levitation.class, Levitation.DURATION * (1 + wandLevel(origin)/5f));
			}
			return true;
		}
	}

	public static class Alarm extends CursedEffect {

		@Override
		public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
			callback.call(); //no vfx
		}

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			for (Mob mob : Dungeon.level.mobs) {
				mob.beckon( user.pos );
			}
			user.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
			if (positiveOnly || randomUsingLevel(wandLevel(origin))){
				Buff.affect(user, ScrollOfChallenge.ChallengeArena.class).setup(user.pos);
				Sample.INSTANCE.play( Assets.Sounds.CHALLENGE );
			} else {
				Sample.INSTANCE.play(Assets.Sounds.ALERT);
			}
			return true;
		}
	}

	//********************
	//*** Rare Effects ***
	//********************

	private static ArrayList<CursedEffect> RARE_EFFECTS = new ArrayList<>();
	static {
		RARE_EFFECTS.add(new SheepPolymorph());
		RARE_EFFECTS.add(new CurseEquipment());
		RARE_EFFECTS.add(new InterFloorTeleport());
		RARE_EFFECTS.add(new SummonMonsters());
		RARE_EFFECTS.add(new FireBall());
		RARE_EFFECTS.add(new ConeOfColors());
		RARE_EFFECTS.add(new MassInvuln());
		RARE_EFFECTS.add(new Petrify());
	}

	public static CursedEffect randomRareEffect(){
		return Random.element(RARE_EFFECTS);
	}

	public static CursedEffect randomValidRareEffect(Item origin, Char user, Ballistica bolt, boolean positiveOnly){
		CursedEffect effect;
		do {
			effect = Random.element(RARE_EFFECTS);
		} while (!effect.valid(origin, user, bolt, positiveOnly));
		return effect;
	}

	public static class SheepPolymorph extends CursedEffect {

		@Override
		public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			Char ch = Actor.findChar( bolt.collisionPos );
			if (ch != null && !(ch instanceof Hero)
					//ignores bosses, questgivers, rat king, etc.
					&& !ch.properties().contains(Char.Property.BOSS)
					&& !ch.properties().contains(Char.Property.MINIBOSS)
					&& !(ch instanceof NPC && ch.alignment == Char.Alignment.NEUTRAL)){
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			if (valid(origin, user, bolt, positiveOnly)){
				Char ch = Actor.findChar( bolt.collisionPos );
				Sheep sheep = new Sheep();
				sheep.initialize(10 * (1 + wandLevel(origin)));
				sheep.pos = ch.pos;
				ch.destroy();
				ch.sprite.killAndErase();
				Dungeon.level.mobs.remove(ch);
				TargetHealthIndicator.instance.target(null);
				GameScene.add(sheep);
				CellEmitter.get(sheep.pos).burst(Speck.factory(Speck.WOOL), 4);
				Sample.INSTANCE.play(Assets.Sounds.PUFF);
				Sample.INSTANCE.play(Assets.Sounds.SHEEP);
				Dungeon.level.occupyCell(sheep);
				return true;
			} else {
				return false;
			}
		}
	}

	public static class CurseEquipment extends CursedEffect {

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			//hexes target if positive only or user isn't hero
			if (positiveOnly || !(user instanceof Hero) || randomUsingLevel(wandLevel(origin))){
				Char ch = Actor.findChar( bolt.collisionPos );
				if (ch != null){
					Buff.affect(ch, Hex.class, Hex.DURATION * (1 + wandLevel(origin)/5f));
				}
				return true;
			} else {
				CursingTrap.curse( (Hero) user );
				if(randomUsingLevel(wandLevel(origin)))
					CursingTrap.curse( (Hero) user );
				return true;
			}
		}
	}

	public static class InterFloorTeleport extends CursedEffect {

		@Override
		public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
			callback.call(); //no vfx
		}

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			if (!positiveOnly && Dungeon.depth > 1 && Dungeon.interfloorTeleportAllowed() && user == Dungeon.hero && !randomUsingLevel(wandLevel(origin))) {

				//starting from 10 floors up (or floor 1), each floor has 1 more weight
				float[] depths = new float[Dungeon.depth-1];
				int start = Math.max(1, Dungeon.depth-(10 - min((int) (sqrt(4*wandLevel(origin))),9)));
				for (int i = start; i < Dungeon.depth; i++) {
					depths[i-1] = i-start+1;
				}
				int depth = 1+Random.chances(depths);

				Level.beforeTransition();
				InterlevelScene.mode = InterlevelScene.Mode.RETURN;
				InterlevelScene.returnDepth = depth;
				InterlevelScene.returnBranch = 0;
				InterlevelScene.returnPos = -1;
				Game.switchScene(InterlevelScene.class);

			//scroll of teleportation if positive only, or inter-floor teleport disallowed
			} else {
				ScrollOfTeleportation.teleportChar(user);

			}
			return true;
		}
	}

	public static class SummonMonsters extends CursedEffect {

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			//mirror images if positive only and user is hero
			if ((positiveOnly || randomUsingLevel(wandLevel(origin))) && user == Dungeon.hero){
				ScrollOfMirrorImage.spawnImages(Dungeon.hero, bolt.collisionPos, 2);
			} else {
				new SummoningTrap().set(bolt.collisionPos).activate();
				if (randomUsingLevel(wandLevel(origin)))
					new SummoningTrap().set(bolt.collisionPos).activate();
			}
			return true;
		}
	}

	public static class FireBall extends CursedEffect {

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {

			Point c = Dungeon.level.cellToPoint(bolt.collisionPos);
			boolean[] fieldOfView = new boolean[Dungeon.level.length()];
			ShadowCaster.castShadow(c.x, c.y, Dungeon.level.width(), fieldOfView, Dungeon.level.solid, 3);

			tryForWandProc(Actor.findChar(bolt.collisionPos), origin);

			for (int i = 0; i < Dungeon.level.length(); i++){
				if (fieldOfView[i] && !Dungeon.level.solid[i]){
					//does not directly harm allies
					if (positiveOnly && Actor.findChar(i) != null && Actor.findChar(i).alignment == Char.Alignment.ALLY){
						continue;
					}

					CellEmitter.get(i).burst(FlameParticle.FACTORY, 10);
					if (Actor.findChar(i) != null){
						Char ch = Actor.findChar(i);
						Burning burning = Buff.affect(ch, Burning.class);
						burning.reignite(ch);
						int dmg = Random.NormalIntRange(5 + Dungeon.scalingDepth(), 10 + Dungeon.scalingDepth()*2) *  (1 + wandLevel(origin)/4);
						ch.damage(dmg, burning);
					}
					if (Dungeon.level.flamable[i]){
						GameScene.add(Blob.seed(i, 4, Fire.class));
					}

				}
			}
			WandOfBlastWave.BlastWave.blast(bolt.collisionPos, 6);
			Sample.INSTANCE.play(Assets.Sounds.BLAST);
			Sample.INSTANCE.play(Assets.Sounds.BURNING);

			return false;
		}
	}

	public static class ConeOfColors extends CursedEffect {

		private ConeAOE cone = null;

		@Override
		public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {

			//need to re-do bolt as it should go through chars
			bolt = new Ballistica(bolt.sourcePos, bolt.collisionPos, Ballistica.STOP_SOLID);

			cone = new ConeAOE( bolt,
					8+ wandLevel(origin) /2f,
					90+min(wandLevel(origin)*3,45),
					Ballistica.STOP_SOLID);

			Ballistica longestRay = null;
			for (Ballistica ray : cone.outerRays){
				if (longestRay == null || ray.dist > longestRay.dist){
					longestRay = ray;
				}
				((MagicMissile)user.sprite.parent.recycle( MagicMissile.class )).reset(
						MagicMissile.RAINBOW_CONE,
						user.sprite,
						ray.path.get(ray.dist),
						null
				);
			}

			//final zap at half distance of the longest ray, for timing of the actual effect
			if (longestRay != null) {
				MagicMissile.boltFromChar( user.sprite.parent,
						MagicMissile.RAINBOW_CONE,
						user.sprite,
						longestRay.path.get(longestRay.dist/2),
						callback );
			} else if (callback != null) {
				callback.call();
			}

			Sample.INSTANCE.play( Assets.Sounds.ZAP );
		}

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {

			ArrayList<Char> affectedChars = new ArrayList<>();
			if (cone == null && Actor.findChar(bolt.collisionPos) != null){
				//cone may be null in cases like chaos elemental melee
				affectedChars.add(Actor.findChar(bolt.collisionPos));
			} else {
				for (Integer cell : cone.cells){
					if (cell == user.pos) {
						continue;
					}
					if (Actor.findChar(cell) != null){
						affectedChars.add(Actor.findChar(cell));
					}
				}
			}

			tryForWandProc(Actor.findChar(bolt.collisionPos), origin);

			for (Char ch : affectedChars){
				//positive only does not harm allies
				if (positiveOnly && ch.alignment == Char.Alignment.ALLY){
					continue;
				} else {

					int dmg = Random.NormalIntRange(5 + Dungeon.scalingDepth(), 10 + Dungeon.scalingDepth()*2) * (1+ wandLevel(origin)/4);
					switch (Random.Int(5)){
						case 0: default:
							Burning burning = Buff.affect(ch, Burning.class);
							burning.reignite(ch,Burning.DURATION*wandLevel(origin)*2.5f);
							ch.damage(dmg, burning);
							ch.sprite.emitter().burst(FlameParticle.FACTORY, 10*wandLevel(origin)*2);
							break;
						case 1:
							ch.damage(dmg, new Frost());
							if (ch.isAlive()) Buff.affect(ch, Frost.class, Frost.DURATION*wandLevel(origin)*3f);
							Splash.at( ch.sprite.center(), 0xFFB2D6FF, 10*wandLevel(origin)*2 );
							break;
						case 2:
							Poison poison = Buff.affect(ch, Poison.class);
							poison.set((3 + Dungeon.scalingDepth() / 2)*wandLevel(origin)*1.5f);
							ch.damage(dmg, poison);
							ch.sprite.emitter().burst(PoisonParticle.SPLASH, 10*wandLevel(origin)*2);
							break;
						case 3:
							Ooze ooze = Buff.affect(ch, Ooze.class);
							ooze.set(Ooze.DURATION*wandLevel(origin)*1.5f);
							ch.damage(dmg, ooze);
							Splash.at( ch.sprite.center(), 0x000000, 10*wandLevel(origin)*2 );
							break;
						case 4:
							ch.damage(dmg, new Electricity());
							if (ch.isAlive()) Buff.affect(ch, Paralysis.class, Paralysis.DURATION*wandLevel(origin)*1.25f);
							ch.sprite.emitter().burst(SparkParticle.FACTORY, 10*wandLevel(origin)*2);
							break;
					}

					if (ch == Dungeon.hero && !ch.isAlive()){
						if (user == Dungeon.hero && origin != null) {
							Badges.validateDeathFromFriendlyMagic();
							Dungeon.fail( origin );
							GLog.n( Messages.get( CursedWand.class, "ondeath", origin.name() ) );
						} else {
							Badges.validateDeathFromEnemyMagic();
							Dungeon.fail( user );
						}
					}

				}
			}
			return true;
		}
	}

	public static class MassInvuln extends CursedEffect {

		@Override
		public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
			callback.call(); //no vfx
		}

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {

			for (Char ch : Actor.chars()){
				Buff.affect(ch, Invulnerability.class, 10f *wandLevel(origin)/2);
				Buff.affect(ch, Bless.class, Bless.DURATION*wandLevel(origin)/2);
			}

			new Flare(5+wandLevel(origin), 48*(1+wandLevel(origin)/4f)).color(0xFFFF00, true).show(user.sprite, 3f);
			GameScene.flash(0x80FFFF40);
			Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
			GLog.p(Messages.get(CursedWand.class, "mass_invuln"));

			return true;
		}

	}

	public static class Petrify extends CursedEffect {

		@Override
		public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			return user == Dungeon.hero;
		}

		@Override
		public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
			callback.call(); //no vfx
		}

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {

			Buff.affect(user, TimeStasis.class, 3f*(1+(wandLevel(origin) * 2)));
			Sample.INSTANCE.play(Assets.Sounds.TELEPORT);

			user.sprite.emitter().burst(Speck.factory(Speck.STEAM), 10*(1+wandLevel(origin)/2));
			GLog.w(Messages.get(CursedWand.class, "petrify"));

			return true;
		}
	}

	//*************************
	//*** Very Rare Effects ***
	//*************************

	private static ArrayList<CursedEffect> VERY_RARE_EFFECTS = new ArrayList<>();
	static {
		VERY_RARE_EFFECTS.add(new ForestFire());
		VERY_RARE_EFFECTS.add(new SpawnGoldenMimic());
		VERY_RARE_EFFECTS.add(new AbortRetryFail());
		VERY_RARE_EFFECTS.add(new RandomTransmogrify());
		VERY_RARE_EFFECTS.add(new HeroShapeShift());
		VERY_RARE_EFFECTS.add(new SuperNova());
		VERY_RARE_EFFECTS.add(new SinkHole());
		VERY_RARE_EFFECTS.add(new GravityChaos());
	}

	public static CursedEffect randomVeryRareEffect(){
		return Random.element(VERY_RARE_EFFECTS);
	}

	public static CursedEffect randomValidVeryRareEffect(Item origin, Char user, Ballistica bolt, boolean positiveOnly){
		CursedEffect effect;
		do {
			effect = Random.element(VERY_RARE_EFFECTS);
		} while (!effect.valid(origin, user, bolt, positiveOnly));
		return effect;
	}

	public static class ForestFire extends CursedEffect {
		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			for (int i = 0; i < Dungeon.level.length(); i++){
				GameScene.add( Blob.seed(i, 15*(1+wandLevel(origin)/2), Regrowth.class));
			}

			new Flare(8*(1+wandLevel(origin)/2), 32*(1+wandLevel(origin)/2f)).color(0xFFFF66, true).show(user.sprite, 2f*wandLevel(origin)*1.2f);
			Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
			GLog.p(Messages.get(CursedWand.class, "grass"));
			//only grass, no fire, if positive only
			if (!positiveOnly) {
				GLog.w(Messages.get(CursedWand.class, "fire"));
				do {
					GameScene.add(Blob.seed(Dungeon.level.randomDestination(null), 10*(1+wandLevel(origin)/4), Fire.class));
				} while (Random.Int(5+((int)(sqrt(wandLevel(origin))))) != 0);
			}
			return true;
		}
	}

	public static class SpawnGoldenMimic extends CursedEffect {

		public static float decPart(float num){
			return (float) (num - floor(num));
		}



		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			Char ch = Actor.findChar(bolt.collisionPos);
			int spawnCell = bolt.collisionPos;
			if (ch != null){
				ArrayList<Integer> candidates = new ArrayList<Integer>();
				for (int n : PathFinder.NEIGHBOURS8) {
					int cell = bolt.collisionPos + n;
					if (Dungeon.level.passable[cell] && Actor.findChar( cell ) == null) {
						candidates.add( cell );
					}
				}
				if (!candidates.isEmpty()){
					spawnCell = Random.element(candidates);
				} else {
					return false;
				}
			}

			Mimic mimic = Mimic.spawnAt(spawnCell, randomUsingLevel(wandLevel(origin)) ? EbonyMimic.class : GoldenMimic.class, false);
			mimic.stopHiding();
			mimic.alignment = Char.Alignment.ENEMY;
			//play vfx/sfx manually as mimic isn't in the scene yet
			Sample.INSTANCE.play(Assets.Sounds.MIMIC, 1, 0.85f);
			CellEmitter.get(mimic.pos).burst(Speck.factory(Speck.STAR), (int) (10*wandLevel(origin)*1.2));
			mimic.items.clear();
			GameScene.add(mimic);

			//mimic is enthralled, but also contains no extra reward, if positive only
			if (positiveOnly || randomUsingLevel(wandLevel(origin))){
				Buff.affect(mimic, ScrollOfSirensSong.Enthralled.class);
			} else {
				Item reward;
				float num = (float) (3*(1-pow(0.9,wandLevel(origin))));
				do {
					reward = Generator.randomUsingDefaults(Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR,
							Generator.Category.RING, Generator.Category.WAND));
				} while (reward.level() < 1+((int) num + Math.random() < decPart(num) ? 1 : 0 ));
				mimic.items.add(reward);
				if(randomUsingLevel(wandLevel(origin))){
					do {
						reward = Generator.randomUsingDefaults(Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR,
								Generator.Category.RING, Generator.Category.WAND));
					} while (reward.level() < 1+((int) num + Math.random() < decPart(num) ? 1 : 0 ));
					mimic.items.add(reward);
				}
			}

			Dungeon.level.occupyCell(mimic);

			return true;
		}
	}

	public static class AbortRetryFail extends CursedEffect {

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			//appears to crash the game (actually just closes it)
			try {
				Dungeon.saveAll();
				if(randomUsingLevel(wandLevel(origin))) {
					if (Messages.lang() != Languages.ENGLISH) {
						//Don't bother doing this joke to none-english speakers, I doubt it would translate.
						//we still consider the effect valid here though as it's cosmetic anyway
						return false;
					} else {
						ShatteredPixelDungeon.runOnRenderThread(
								new Callback() {
									@Override
									public void call() {
										GameScene.show(
												new WndOptions(Icons.get(Icons.WARNING),
														"CURSED WAND ERROR",
														"this application will now self-destruct",
														"abort",
														"retry",
														"fail") {

													@Override
													protected void onSelect(int index) {
														Game.instance.finish();
													}

													@Override
													public void onBackPressed() {
														//do nothing
													}
												}
										);
									}
								}
						);
						return false;
					}
				}
				Game.instance.finish();
				return false;
			} catch(IOException e){
				ShatteredPixelDungeon.reportException(e);
				//maybe don't kill the game if the save failed, just do nothing
				return false;
			}
		}
	}

	public static class RandomTransmogrify extends CursedEffect {

		@Override
		public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			if (positiveOnly){
				return true;
			} else if (origin == null || user != Dungeon.hero || !Dungeon.hero.belongings.contains(origin)){
				return false;
			} else {
				return true;
			}
		}

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			//triggers metamorph effect if positive only
			if (positiveOnly || randomUsingLevel(wandLevel(origin))){
				GameScene.show(new ScrollOfMetamorphosis.WndMetamorphChoose());
				return true;
			}

			//skips this effect if there is no item to transmogrify
			if (origin == null || user != Dungeon.hero || !Dungeon.hero.belongings.contains(origin)){
				return false;
			}
			origin.detach(Dungeon.hero.belongings.backpack);
			Item result;
			do {
				result = Generator.randomUsingDefaults(Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR,
						Generator.Category.RING, Generator.Category.ARTIFACT));
			} while (result.cursed);
			if (result.isUpgradable()) result.upgrade(origin.level());
			result.cursed = result.cursedKnown = true;
			if (origin instanceof Wand){
				GLog.w( Messages.get(CursedWand.class, "transmogrify_wand") );
			} else {
				GLog.w( Messages.get(CursedWand.class, "transmogrify_other") );
			}
			Dungeon.level.drop(result, user.pos).sprite.drop();
			return true;
		}
	}

	public static class HeroShapeShift extends CursedEffect {

		@Override
		public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			return user instanceof Hero || Actor.findChar(bolt.collisionPos) instanceof Hero;
		}

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			if (user instanceof Hero){
				Buff.affect(user, HeroDisguise.class, HeroDisguise.DURATION * wandLevel(origin)*2);
				GLog.w( Messages.get(CursedWand.class, "disguise") );
				return true;
			} else if (Actor.findChar(bolt.collisionPos) instanceof Hero){
				Buff.affect(Actor.findChar(bolt.collisionPos), HeroDisguise.class, HeroDisguise.DURATION * wandLevel(origin)*2);
				GLog.w( Messages.get(CursedWand.class, "disguise") );
				return true;
			}
			return false;
		}
	}

	public static class SuperNova extends CursedEffect {
		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			SuperNovaTracker nova = Buff.append(Dungeon.hero, SuperNovaTracker.class);
			nova.pos = bolt.collisionPos;
			nova.harmsAllies = !positiveOnly;
			if (positiveOnly){
				GLog.p(Messages.get(CursedWand.class, "supernova_positive"));
			} else {
				GLog.w(Messages.get(CursedWand.class, "supernova"));
			}
			if(randomUsingLevel(wandLevel(origin))) {
				for (Mob mob : Dungeon.level.mobs) {
					mob.beckon(user.pos);
				}
				GLog.w(Messages.get(CursedWand.class, "supernova_beckon"));
			}

			return true;
		}
	}

	public static class SinkHole extends CursedEffect {

		@Override
		public boolean valid(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			//can't happen on floors where chasms aren't allowed
			if( Dungeon.bossLevel() || Dungeon.depth > 25 || Dungeon.branch != 0){
				return false;
			}
			return true;
		}

		@Override
		public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
			callback.call(); //no vfx
		}

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			boolean[] passable = BArray.not( Dungeon.level.solid, null );
			BArray.or(passable, Dungeon.level.passable, passable);
			PathFinder.buildDistanceMap( user.pos, passable, (int) (5*(1+wandLevel(origin)/3f)));
			ArrayList<Integer> positions = new ArrayList<>();
			for (int i = 0; i < PathFinder.distance.length; i++) {
				if (PathFinder.distance[i] < Integer.MAX_VALUE) {
					if (!Dungeon.level.solid[i] || Dungeon.level.passable[i]) {
						CellEmitter.floor(i).burst(PitfallParticle.FACTORY4, 8);
						positions.add(i);
					}
				}
			}
			PitfallTrap.DelayedPit p = Buff.append(Dungeon.hero, PitfallTrap.DelayedPit.class, 1*(1+wandLevel(origin)/2f));
			p.depth = Dungeon.depth;
			p.branch = Dungeon.branch;
			p.setPositions(positions);

			//effect does not harm hero/allies if positive only
			if (positiveOnly){
				p.ignoreAllies = true;
				GLog.p(Messages.get(CursedWand.class, "sinkhole_positive"));
			} else {
				GLog.w(Messages.get(CursedWand.class, "sinkhole"));
			}
			return true;
		}
	}

	public static class GravityChaos extends CursedEffect{

		@Override
		public void FX(Item origin, Char user, Ballistica bolt, Callback callback) {
			callback.call(); //no vfx
		}

		@Override
		public boolean effect(Item origin, Char user, Ballistica bolt, boolean positiveOnly) {
			positiveOnly = randomUsingLevel(wandLevel(origin)) ? !positiveOnly : positiveOnly;
			Buff.append(user, GravityChaosTracker.class).positiveOnly = positiveOnly;
			Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
			if (positiveOnly){
				GLog.p(Messages.get(CursedWand.class, "gravity_positive"));
			} else {
				GLog.w(Messages.get(CursedWand.class, "gravity"));
			}
			return true;
		}
	}

}
