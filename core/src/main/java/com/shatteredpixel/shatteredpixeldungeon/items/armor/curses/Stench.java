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

package com.shatteredpixel.shatteredpixeldungeon.items.armor.curses;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

import static java.lang.Math.min;

public class Stench extends Armor.Glyph {

	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {

		float procChance = 1/8f * procChanceMultiplier(defender);
		if ( Random.Float() < procChance ) {

			float number = Random.Float(0,min(armor.buffedLvl()*.5f,3.5f));
			if (number <= .5f)
				GameScene.add(Blob.seed(defender.pos, 250 + armor.buffedLvl() * 10, ToxicGas.class));
			else if (number <= 1)
				GameScene.add(Blob.seed(defender.pos, 350 + armor.buffedLvl() * 10, ConfusionGas.class));
			else if (number <= 1.5f)
				GameScene.add(Blob.seed(defender.pos, 250 + armor.buffedLvl() * 10, CorrosiveGas.class));
			else if (number <= 2f)
				GameScene.add(Blob.seed(defender.pos, 300 + armor.buffedLvl() * 10, StormCloud.class));
			else if (number <= 2.25f)
				GameScene.add(Blob.seed(defender.pos, 250 + armor.buffedLvl() * 10, StenchGas.class));
			else if (number <= 2.5f)
				GameScene.add(Blob.seed(defender.pos, 200 + armor.buffedLvl() * 10, ParalyticGas.class));
			else if (number <= 3f)
				GameScene.add(Blob.seed(defender.pos, 300 + armor.buffedLvl() * 10, Regrowth.class));
			else if (number <= 3.25f)
				GameScene.add(Blob.seed(defender.pos, 350 + armor.buffedLvl() * 10, Blizzard.class));
			else if (number <= 3.5f)
				GameScene.add(Blob.seed(defender.pos, 350 + armor.buffedLvl() * 10, Inferno.class));


		}

		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

	@Override
	public boolean curse() {
		return true;
	}
}
