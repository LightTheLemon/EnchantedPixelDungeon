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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfConservation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Torch extends Item {

	public static final String AC_LIGHT	= "LIGHT";
	
	public static final float TIME_TO_LIGHT = 1;
	
	{
		image = ItemSpriteSheet.TORCH;
		
		stackable = true;
		
		defaultAction = AC_LIGHT;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_LIGHT );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );
		
		if (action.equals( AC_LIGHT )) {
			
			hero.spend( TIME_TO_LIGHT );
			hero.busy();
			
			hero.sprite.operate( hero.pos );
			
			detach( hero.belongings.backpack );
			Catalog.countUse(getClass());
			
			Buff.affect(hero, Light.class, Light.DURATION);
			Sample.INSTANCE.play(Assets.Sounds.BURNING);
			
			Emitter emitter = hero.sprite.centerEmitter();
			emitter.start( FlameParticle.FACTORY, 0.2f, 3 );
			
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
				if (detached != null) ( detached).onDetach();
				return detached;
			} else {

				Item detached = split(1);
				updateQuickslot();
				if (detached != null) ( detached).onDetach();
				return detached;

			}
		}
	}
	
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
		return 8 * quantity;
	}

}
