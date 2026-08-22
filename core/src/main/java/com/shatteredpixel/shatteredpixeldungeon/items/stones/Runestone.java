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

package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfConservation;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

public abstract class Runestone extends Item {
	
	{
		stackable = true;
		defaultAction = AC_THROW;
	}

	//anonymous stones don't count as consumed, do not drop, etc.
	//useful for stones which are only spawned for their effects
	protected boolean anonymous = false;
	public void anonymize(){
		image = ItemSpriteSheet.STONE_HOLDER;
		anonymous = true;
	}

	/*
	public static final String AC_THROW	= "THROW";

	@Override
	public ArrayList<String> actions(Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_THROW );
		actions.remove( AC_THROW);
		return actions;
	}

	@Override
	public void execute (Hero hero, String action) {
		GameScene.cancel();
		curUser = hero;
		curItem = this;

		if (action.equals( AC_DROP_ALL )) {

			if (hero.belongings.backpack.contains(this) || isEquipped(hero)) {
				doDrop(hero);
			}

		} else if (action.equals( AC_THROW )) {

			if (hero.belongings.backpack.contains(this) || isEquipped(hero)) {
				doThrow(hero);
			}

		}
	}

	 */

	@Override
	protected void onThrow(int cell) {
		///inventory stones are thrown like normal items, other stones don't trigger when thrown into pits
		if (this instanceof InventoryStone ||
				Dungeon.hero.buff(MagicImmune.class) != null ||
				(Dungeon.level.pit[cell] && Actor.findChar(cell) == null)){
			if (!anonymous) super.onThrow( cell );
		} else {
			if (!anonymous) {
				Catalog.countUse(getClass());
				Talent.onRunestoneUsed(curUser, cell, getClass());
			}
			activate(cell);
			if (Actor.findChar(cell) == null) Dungeon.level.pressCell( cell );
			Invisibility.dispel();
		}
	}

	@Override
	public Item detach( Bag container ) {

		if (quantity <= 0) {

			return null;

		} else
		if (quantity == 1) {

			if (stackable){
				Dungeon.quickslot.convertToPlaceholder(this);
			}

			if (Random.Float() < RingOfConservation.recycleChance(Dungeon.hero) ) {
				GLog.p(Messages.get(RingOfConservation.class, "conservation_proc"));
				new Flare(6, 32).color(0x00E626, true).show(Dungeon.hero.sprite, 2f);
				Sample.INSTANCE.play( Assets.Sounds.BADGE );

				return null;
			} else {
				return detachAll( container );
			}

		} else {
			if (Random.Float() < RingOfConservation.recycleChance(Dungeon.hero) ) {
				GLog.p(Messages.get(RingOfConservation.class, "conservation_proc"));
				new Flare(6, 32).color(0x00E626, true).show(Dungeon.hero.sprite, 2f);
				Sample.INSTANCE.play( Assets.Sounds.BADGE );

				return null;
			} else if (Float.isNaN(RingOfConservation.recycleChance(Dungeon.hero)) && quantity > 1 && Random.Float() < ( -1 * RingOfConservation.cursedProc(Dungeon.hero)) ) {
				GLog.p(Messages.get(RingOfConservation.class, "cursed_proc"));
				new Flare(6, 32).color(0x000000, true).show(Dungeon.hero.sprite, 2f);
				Sample.INSTANCE.play( Assets.Sounds.CURSED );

				Item detached = split(2);
				updateQuickslot();
				if (detached != null) ((Runestone) detached).onDetach();
				return detached;
			} else {

				Item detached = split(1);
				updateQuickslot();
				if (detached != null) ((Runestone) detached).onDetach();
				return detached;

			}
		}
	}
	
	protected abstract void activate(int cell);
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public int value() {
		return 15 * quantity;
	}

	@Override
	public int energyVal() {
		return 3 * quantity;
	}

	public static class PlaceHolder extends Runestone {
		
		{
			image = ItemSpriteSheet.STONE_HOLDER;
		}
		
		@Override
		protected void activate(int cell) {
			//does nothing
		}
		
		@Override
		public boolean isSimilar(Item item) {
			return item instanceof Runestone;
		}
		
		@Override
		public String info() {
			return "";
		}
	}
}
