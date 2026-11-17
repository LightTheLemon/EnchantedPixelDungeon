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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfConservation;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.StatusPane;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHero;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class PotionOfExperience extends Potion {

	{
		icon = Dungeon.swapExperience ? ItemSpriteSheet.Icons.POTION_DIVINE : ItemSpriteSheet.Icons.POTION_EXP;

		bones = true;

		talentFactor = 2f;
	}

	protected static boolean identifiedByUse = false;

	@Override
	public void apply( Hero hero ) {
		if (Dungeon.swapExperience) {
			super.apply(hero);
		} else {
			identify();
			hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(hero.maxExp()), FloatingText.EXPERIENCE);
			hero.earnExp( hero.maxExp(), getClass() );
			new Flare( 6, 32 ).color(0xFFFF00, true).show( curUser.sprite, 2f );
		}

	}

	@Override
	//need to override drink so that time isn't spent right away
	protected void drink(final Hero hero) {

		if (Dungeon.swapExperience) {

			if (!isKnown()) {
				identify();
				RingOfConservation.detachProc(curItem, 1);
				identifiedByUse = true;
			} else {
				identifiedByUse = false;
			}

			Buff.affect(curUser, DivineInspirationTracker.class).potionUses++;

			if (!identifiedByUse) {
				curItem.detach(curUser.belongings.backpack);
			}
			identifiedByUse = false;

			curUser.busy();
			curUser.sprite.operate(curUser.pos);

			curUser.spendAndNext(1f);

			StatusPane.talentBlink = 10f;
			WndHero.lastIdx = 1;

			GameScene.showlevelUpStars();

			Sample.INSTANCE.play( Assets.Sounds.DRINK );
			Sample.INSTANCE.playDelayed(Assets.Sounds.LEVELUP, 0.3f, 0.7f, 1.2f);
			Sample.INSTANCE.playDelayed(Assets.Sounds.LEVELUP, 0.6f, 0.7f, 1.2f);
			new Flare( 6, 32 ).color(0xFFFF00, true).show( curUser.sprite, 2f );
			GLog.p(Messages.get(PotionOfExperience.class, "bonus"));

			if (!anonymous) {
				Catalog.countUse(PotionOfExperience.class);
				if (Random.Float() < talentChance) {
					Talent.onPotionUsed(curUser, curUser.pos, talentFactor);
				}
			}

		} else {
			super.drink(hero);
		}

	}

	@Override
	public int value() {
		return isKnown() ? 50 * quantity : super.value();
	}

	@Override
	public int energyVal() {
		return 8 * quantity;
	}

	@Override
	public String name() {
		if (isKnown()) {
			if (Dungeon.swapExperience) {
				return Messages.get(PotionOfExperience.class, "alt_name");
			} else {
				return super.name();
			}
		} else {
			return Messages.get(this, color);
		}
	}

	@Override
	public String desc() {
		if (isKnown()) {
			if (Dungeon.swapExperience) {
				return Messages.get(PotionOfExperience.class, "alt_desc");
			} else {
				return super.desc();
			}
		} else {
			return Messages.get(this, "unknown_desc");
		}
	}

	public static class DivineInspirationTracker extends Buff {

		{
			type = buffType.POSITIVE;
			revivePersists = true;
		}

		public int potionUses = 0;

		private static final String BONUS_TALENT_POINTS = "bonus_talent_points";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(BONUS_TALENT_POINTS, potionUses);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			potionUses = bundle.getInt(BONUS_TALENT_POINTS);
		}

	}


}
