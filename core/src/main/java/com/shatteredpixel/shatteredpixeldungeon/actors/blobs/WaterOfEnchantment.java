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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes.Landmark;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class WaterOfEnchantment extends WellWater {
	
	@Override
	protected boolean affectHero( Hero hero ) {
		
		if (!hero.isAlive()) return false;
		
		Sample.INSTANCE.play( Assets.Sounds.DRINK );

		//here's the logic for all worn equipment
		Weapon weapon = (Weapon) hero.belongings.weapon();
		if (weapon != null) {
			if (!weapon.hasGoodEnchant()) {
				weapon.enchant();
			}
			if (weapon.hasCurseEnchant()) {
				hero.belongings.uncurseEquipped();
			}
		}
		Armor armor = (Armor) hero.belongings.armor();
		if (armor != null) {
			if (!armor.hasGoodGlyph()) {
				armor.inscribe();
			}
			if (armor.hasCurseGlyph()) {
				hero.belongings.uncurseEquipped();
			}
		}

		//logic for all other equipment
		for (Item item : hero.belongings.backpack.items) {

			if (!item.cursed && ( item instanceof Weapon || item instanceof Armor ) ) {
                if(item instanceof Weapon && !((Weapon) item).hasGoodEnchant()) {
					((Weapon) item).enchant();
				}
                if (item instanceof Armor && !((Armor) item).hasGoodGlyph()) {
					((Armor) item).inscribe();
				}

			} else if (item.cursed && (item instanceof Weapon || item instanceof Armor )) {
				if (item instanceof Weapon) {
					((Weapon) item).enchant(null);
				}
				if (item instanceof Armor) {
					((Armor) item).inscribe(null);
				}

			} else if ( item instanceof Ring || item instanceof Wand || item instanceof Artifact) {
				item.cursed = false;
			}
		}

		hero.sprite.emitter().start(Speck.factory(Speck.ENCHANT_STAR), 0.6f, 6);
		hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(hero.HT), FloatingText.EXPERIENCE);

		CellEmitter.get( hero.pos ).start( ShaftParticle.FACTORY, 0.2f, 3 );

		Dungeon.hero.interrupt();
	
		GLog.p( Messages.get(this, "procced") );
		
		return true;
	}
	@Override
	protected Item affectItem( Item item, int pos ) {
		if (item instanceof Weapon || item instanceof Armor || item instanceof Ring || item instanceof Wand) {
			item.upgrade();
			CellEmitter.get( pos ).start( Speck.factory( Speck.ENCHANT_STAR ), 0.6f, 6 );
			Sample.INSTANCE.play( Assets.Sounds.DRINK );
			return item;
		} else if (item instanceof Waterskin && !((Waterskin)item).isFull()) {
			((Waterskin)item).fill(20);
			CellEmitter.get( pos ).start( Speck.factory( Speck.HEALING ), 0.4f, 4 );
			Sample.INSTANCE.play( Assets.Sounds.DRINK );
			return item;
		} else if ( item instanceof Ankh && !(((Ankh) item).isBlessed())) {
			((Ankh) item).bless();
			CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
			Sample.INSTANCE.play(Assets.Sounds.DRINK);
			return item;
		}

		return null;
	}
	
	@Override
	public Landmark landmark() {
		return Landmark.WELL_OF_ENCHANTMENT;
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.start( Speck.factory( Speck.ENCHANT_STAR ), 0.45f, 0 );
	}
	
	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
}
