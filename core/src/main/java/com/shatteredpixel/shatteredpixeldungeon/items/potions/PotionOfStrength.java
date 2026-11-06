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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfMastery;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfConservation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class PotionOfStrength extends Potion {

	{

		icon = Dungeon.swapStrength ? ItemSpriteSheet.Icons.POTION_MASTERY : ItemSpriteSheet.Icons.POTION_STRENGTH;

		unique = true;

		talentFactor = 2f;
	}
	protected static boolean identifiedByUse = false;

	@Override
	protected void drink(final Hero hero) {
		if (Dungeon.swapStrength) {
			if (!isKnown()) {
				identify();
				RingOfConservation.detachProc(curItem, 1);
				identifiedByUse = true;
			} else {
				identifiedByUse = false;
			}

			GameScene.selectItem(itemSelector);
		} else {
			super.drink(hero);
		}

	}
	
	@Override
	public void apply( Hero hero ) {

		if (Dungeon.swapStrength) {
			super.apply(hero);

		} else {
			identify();

			hero.STR++;
			hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, "1", FloatingText.STRENGTH);

			GLog.p( Messages.get(this, "msg", hero.STR()) );

			Badges.validateStrengthAttained();
			Badges.validateDuelistUnlock();

		}
	}

	@Override
	public int value() {
		return isKnown() ? 250 * quantity : super.value();
	}

	@Override
	public int energyVal() {
		return 10 * quantity;
	}

	protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(PotionOfMastery.class, "prompt");
		}

		@Override
		public boolean itemSelectable(Item item) {
			return
					(item instanceof Weapon && !(item instanceof SpiritBow))
							|| (item instanceof Armor);
		}

		@Override
		public void onSelect(Item item) {

			if (item == null && identifiedByUse){
				GameScene.show( new WndOptions(new ItemSprite(PotionOfStrength.this),
						Messages.titleCase(name()),
						Messages.get(PotionOfStrength.class, "warning"),
						Messages.get(PotionOfStrength.class, "yes"),
						Messages.get(PotionOfStrength.class, "no") ) {
					@Override
					protected void onSelect( int index ) {
						switch (index) {
							case 0:
								curUser.spendAndNext(1f);
								identifiedByUse = false;
								break;
							case 1:
								GameScene.selectItem(itemSelector);
								break;
						}
					}
					public void onBackPressed() {}
				} );
			} else if (item != null) {

				if (item instanceof Weapon) {
					((Weapon) item).masteryPotionBonus += 1;
					GLog.p( Messages.get(PotionOfMastery.class, "weapon_easier") );
				} else if (item instanceof Armor) {
					((Armor) item).masteryPotionBonus += 1;
					GLog.p( Messages.get(PotionOfMastery.class, "armor_easier") );
				}
				updateQuickslot();

				Sample.INSTANCE.play( Assets.Sounds.DRINK );
				curUser.sprite.operate(curUser.pos);

				if (!identifiedByUse) {
					curItem.detach(curUser.belongings.backpack);
				}
				identifiedByUse = false;

				if (!anonymous) {
					Catalog.countUse(PotionOfMastery.class);
					if (Random.Float() < talentChance) {
						Talent.onPotionUsed(curUser, curUser.pos, talentFactor);
					}
				}
			}

		}
	};

	@Override
	public String name() {
		if (isKnown()) {
			if (Dungeon.swapStrength) {
				return Messages.get(this, "alt_name");
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
			if (Dungeon.swapStrength) {
				return Messages.get(this, "alt_desc");
			} else {
				return super.desc();
			}
		} else {
			return Messages.get(this, "unknown_desc");
		}
	}

}
