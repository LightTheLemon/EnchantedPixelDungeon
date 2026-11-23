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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Visual;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

public class Boomerang extends MissileWeapon {
	
	{
		image = ItemSpriteSheet.BOOMERANG;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 1f;

		tier = 4;
		sticky = false;
		baseUses = 15;
	}

	@Override
	public int max(int lvl) {
		return  8 * tier +                                      //32 base, up from 20 because you get less of them
				Math.round(1.3f*(tier + 1)) * lvl;              //+6.5 per level, up from +4

	}

	boolean circlingBack = false;

	@Override
	protected float adjacentAccFactor(Char owner, Char target) {
		if (circlingBack){
			new Flare( 6, 16 ).color(0xFFFFFF, true).show( target.sprite, 0.5f );
			return 1_000_000;
		}
		return super.adjacentAccFactor(owner, target);
	}
	@Override
	public int damageRoll(Char owner) {
		//if (circlingBack) return Math.round(1.2f * super.damageRoll(owner));
		return super.damageRoll(owner);
	}
	@Override
	public int defaultQuantity(){
		return 1;
	}

	@Override
	public float pickupDelay() {
		//pickup is instant when circling back
		return circlingBack ? 0f : super.pickupDelay();
	}

	@Override
	protected void rangedHit(Char enemy, int cell) {
		decrementDurability();
		if (durability > 0){
			Buff.append(Dungeon.hero, CircleBack.class).setup(this, cell, Dungeon.hero.pos, Dungeon.depth, Dungeon.branch);
		}
	}
	
	@Override
	protected void rangedMiss(int cell) {
		parent = null;
		Buff.append(Dungeon.hero, CircleBack.class).setup(this, cell, Dungeon.hero.pos, Dungeon.depth, Dungeon.branch);
	}

	@Override
	protected void onThrow( int cell ) {
		if (cell == Dungeon.hero.pos) {
			super.onThrow(cell);
		} else {
			Buff.append(Dungeon.hero, CircleBack.class).setup(this, cell, Dungeon.hero.pos, Dungeon.depth, Dungeon.branch);
		}
	}

	public static class CircleBack extends Buff {

		{
			revivePersists = true;
		}
		
		private Boomerang boomerang;
		private int thrownPos;
		private int returnPos;
		private int returnDepth;
		private int returnBranch;

		private int left;

		public void setup(Boomerang boomerang, int thrownPos, int returnPos, int returnDepth, int returnBranch){
			this.boomerang = boomerang;
			this.thrownPos = thrownPos;
			this.returnPos = returnPos;
			this.returnDepth = returnDepth;
			this.returnBranch = returnBranch;
			left = 5;
		}
		
		public int returnPos(){
			return returnPos;
		}
		
		public MissileWeapon cancel(){
			detach();
			return boomerang;
		}

		@Override
		public void detach() {
			super.detach();
		}

		public int activeDepth(){
			return returnDepth;
		}
		class Sprite extends MissileSprite {

			Sprite() {
				try {
					setup(worldToCamera(thrownPos), worldToCamera(thrownPos), boomerang, null);
				} catch (NullPointerException ignored) {
				}
			}

			public void reset(int to, Callback listener) {
				reset(thrownPos, to, boomerang, listener);
			}

			public void reset(Visual to, Callback listener) {
				reset(thrownPos, to, boomerang, listener);
			}

			@Override
			public synchronized void onComplete(Tweener tweener) {
				super.onComplete(tweener);
				revive(); // this unkills the sprite.
			}

		}
		private Sprite sprite;
		public Sprite sprite() {
			if(sprite == null) sprite = new Sprite(); // you'll never get a null from this
			try {
				target.sprite.parent.add(sprite);
			} catch (NullPointerException ignored) { /* just give up and move on. it'll get added eventually. */
			}
			return sprite;
		}
		public void spinInPlace() {
			sprite().reset(thrownPos,thrownPos,boomerang,null);
		}

		@Override
		public boolean act() {
			if (returnDepth == Dungeon.depth && returnBranch == Dungeon.branch){
				left--;
				if (left <= 0){
					final Char returnTarget = Actor.findChar(returnPos);
					final Char target = this.target;
					sprite().reset( thrownPos,
									returnPos,
									boomerang,
									new Callback() {
										@Override
										public void call() {
											detach();
											sprite().killAndErase();
											boomerang.circlingBack = true;
											if (returnTarget == target){
												if (!boomerang.spawnedForEffect) {
													if (!(target instanceof Hero) || !boomerang.doPickUp((Hero) target)) {
														Dungeon.level.drop(boomerang, returnPos).sprite.drop();
													}
												}

											} else if (returnTarget != null){
												if (((Hero)target).shoot( returnTarget, boomerang )) {
													boomerang.decrementDurability();
												}
												if (!boomerang.spawnedForEffect && boomerang.durability > 0) {
													Dungeon.level.drop(boomerang, returnPos).sprite.drop();
												}

											} else if (!boomerang.spawnedForEffect) {
												Dungeon.level.drop(boomerang, returnPos).sprite.drop();
											}
											boomerang.circlingBack = false;
											CircleBack.this.next();

										}
									});
					boomerang.grabItem(thrownPos, curUser);
					return false;
				}
			}
			spinInPlace();
			spend( TICK );
			return true;
		}
		
		private static final String BOOMERANG = "boomerang";
		private static final String THROWN_POS = "thrown_pos";
		private static final String RETURN_POS = "return_pos";
		private static final String RETURN_DEPTH = "return_depth";
		private static final String RETURN_BRANCH = "return_branch";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(BOOMERANG, boomerang);
			bundle.put(THROWN_POS, thrownPos);
			bundle.put(RETURN_POS, returnPos);
			bundle.put(RETURN_DEPTH, returnDepth);
			bundle.put(RETURN_BRANCH, returnBranch);
		}
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			boomerang = (Boomerang) bundle.get(BOOMERANG);
			thrownPos = bundle.getInt(THROWN_POS);
			returnPos = bundle.getInt(RETURN_POS);
			returnDepth = bundle.getInt(RETURN_DEPTH);
			returnBranch = bundle.contains(RETURN_BRANCH) ? bundle.getInt(RETURN_BRANCH) : 0;
		}
	}

	//Mostly the same logic as affectTarget in TelekineticGrab.java
	protected void grabItem(int cell, Hero hero) {
		Char ch = Actor.findChar(cell);

		float totalpickupTime = 0;

		if (ch != null && ch.buff(PinCushion.class) != null){

			while (ch.buff(PinCushion.class) != null) {
				Item item = ch.buff(PinCushion.class).grabOne();

				if (item.doPickUp(hero, ch.pos)) {
					totalpickupTime += item.pickupDelay();
					this.decrementDurability();
					GLog.i( Messages.capitalize(Messages.get(hero, "you_now_have", item.name())) );

				} else {
					GLog.w(Messages.get(Boomerang.class, "cant_grab"));
					Dungeon.level.drop(item, ch.pos).sprite.drop();
					break;
				}

			}

			//casting the spell takes at most 1 turn
			if (totalpickupTime > 1){
				hero.spend(-(totalpickupTime-1));
			}

		} else if (Dungeon.level.heaps.get(cell) != null){

			Heap h = Dungeon.level.heaps.get(cell);

			if (h.type != Heap.Type.HEAP){
				if (h.type != Heap.Type.CHEST) GLog.w(Messages.get(Boomerang.class, "cant_grab"));
				hero.spend(Actor.TICK);
				h.sprite.drop();
			} else {

				if (!h.isEmpty() ) {
					Item item = h.peek();
					if (item.doPickUp(hero, h.pos)) {
						h.pickUp();
						totalpickupTime += item.pickupDelay();

						this.decrementDurability();
						GLog.i(Messages.capitalize(Messages.get(hero, "you_now_have", item.name())));

					} else {
						GLog.w(Messages.get(Boomerang.class, "cant_grab"));
						h.sprite.drop();
					}
				}

				//casting the spell takes at most 1 turn
				if (totalpickupTime > 1) {
					hero.spend(-(totalpickupTime - 1));
				}
			}

		} else {
			hero.spend(Actor.TICK);
		}

	}

}
