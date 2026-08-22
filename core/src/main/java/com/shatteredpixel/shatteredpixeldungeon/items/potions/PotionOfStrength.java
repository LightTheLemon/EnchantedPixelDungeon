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

package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfConservation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class PotionOfStrength extends Potion {

	{
		icon = ItemSpriteSheet.Icons.POTION_STRENGTH;

		unique = true;

		talentFactor = 2f;
	}
	
	@Override
	public void apply( Hero hero ) {
		identify();

		hero.STR++;
		hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, "1", FloatingText.STRENGTH);

		GLog.p( Messages.get(this, "msg", hero.STR()) );
		
		Badges.validateStrengthAttained();
		Badges.validateDuelistUnlock();
	}

	@Override
	public Item detach(Bag container ) {

		if (quantity <= 0) {

			return null;

		} else
		if (quantity == 1) {

			if (stackable){
				Dungeon.quickslot.convertToPlaceholder(this);
			}

			if ((Random.Float() * 2) < RingOfConservation.recycleChance(Dungeon.hero) ) {
				GLog.p(Messages.get(RingOfConservation.class, "conservation_proc"));
				new Flare(6, 32).color(0x00E626, true).show(Dungeon.hero.sprite, 2f);
				Sample.INSTANCE.play( Assets.Sounds.BADGE );

				return null;
			} else {
				return detachAll( container );
			}

		} else {
			if ((Random.Float() * 2) < RingOfConservation.recycleChance(Dungeon.hero) ) {
				GLog.p(Messages.get(RingOfConservation.class, "conservation_proc"));
				new Flare(6, 32).color(0x00E626, true).show(Dungeon.hero.sprite, 2f);
				Sample.INSTANCE.play( Assets.Sounds.BADGE );

				return null;
			} else if (Float.isNaN(RingOfConservation.recycleChance(Dungeon.hero)) && quantity > 1 && (Random.Float() * 2) < ( -1 * RingOfConservation.cursedProc(Dungeon.hero)) ) {
				GLog.p(Messages.get(RingOfConservation.class, "cursed_proc"));
				new Flare(6, 32).color(0x000000, true).show(Dungeon.hero.sprite, 2f);
				Sample.INSTANCE.play( Assets.Sounds.CURSED );

				Item detached = split(2);
				updateQuickslot();
				if (detached != null) ((PotionOfStrength) detached).onDetach();
				return detached;
			} else {

				Item detached = split(1);
				updateQuickslot();
				if (detached != null) ((PotionOfStrength) detached).onDetach();
				return detached;

			}
		}
	}

	@Override
	public int value() {
		return isKnown() ? 500 * quantity : super.value();
	}

	@Override
	public int energyVal() {
		return isKnown() ? 10 * quantity : super.energyVal();
	}
}
