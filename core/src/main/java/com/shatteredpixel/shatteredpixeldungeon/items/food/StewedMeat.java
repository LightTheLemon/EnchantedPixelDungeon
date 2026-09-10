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

package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class StewedMeat extends Food {
	
	{
		image = ItemSpriteSheet.STEWED;
		energy = Hunger.HUNGRY/2f;
	}
	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero);
		effect(hero);
	}

	@Override
	public int value() {
		return 40 * quantity;
	}

	public static void effect(Hero hero){
		if(Random.Float() <= .40f) {
			GLog.i( Messages.get(StewedMeat.class, "invis") );
			Buff.affect( hero, Invisibility.class, (Invisibility.DURATION - 12));
		}
	}
	
	public static class oneMeat extends Recipe.SimpleRecipe{
		{
			inputs =  new Class[]{MysteryMeat.class};
			inQuantity = new int[]{1};
			
			cost = 1;
			
			output = StewedMeat.class;
			outQuantity = 1;
		}
	}
	
	public static class twoMeat extends Recipe.SimpleRecipe{
		{
			inputs =  new Class[]{MysteryMeat.class};
			inQuantity = new int[]{2};
			
			cost = 2;
			
			output = StewedMeat.class;
			outQuantity = 2;
		}
	}
	
	//red meat
	//blue meat
	
	public static class threeMeat extends Recipe.SimpleRecipe{
		{
			inputs =  new Class[]{MysteryMeat.class};
			inQuantity = new int[]{3};
			
			cost = 2;
			
			output = StewedMeat.class;
			outQuantity = 3;
		}
	}

}
