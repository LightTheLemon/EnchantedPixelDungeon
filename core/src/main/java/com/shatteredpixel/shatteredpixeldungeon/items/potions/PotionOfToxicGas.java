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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class PotionOfToxicGas extends Potion {

	{
		icon = Dungeon.swapGas ? ItemSpriteSheet.Icons.POTION_PARAGAS : ItemSpriteSheet.Icons.POTION_TOXICGAS;
	}

	@Override
	public void shatter( int cell ) {

		splash( cell );
		if (Dungeon.level.heroFOV[cell]) {
			identify();

			Sample.INSTANCE.play( Assets.Sounds.SHATTER );
			Sample.INSTANCE.play( Assets.Sounds.GAS );
		}

		var gasType = Dungeon.swapGas ? ParalyticGas.class : ToxicGas.class;
		GameScene.add( Blob.seed( cell, 1000, gasType ) );
	}
	
	@Override
	public int value() {
		return isKnown() ? 30 * quantity : super.value();
	}

	@Override
	public String name() {
		if (isKnown()) {
			return Dungeon.swapGas ? Messages.get(this, "alt_name") : super.name();
		} else {
			return Messages.get(this, color);
		}
	}

	@Override
	public String desc() {
		if (isKnown()) {
			return Dungeon.swapGas ? Messages.get(this, "alt_desc") : super.name();
		} else {
			return Messages.get(this, "unknown_desc");
		}
	}

}
