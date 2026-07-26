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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Displacement extends Armor.Glyph {

	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage ) {
		boolean noDamage = false;
		float procChance = 1/20f * procChanceMultiplier(defender) + armor.buffedLvl()/100f ;
		if ( Random.Float() < procChance ) {
			ScrollOfTeleportation.teleportChar(defender);
			noDamage = true;
			Buff.affect(defender, Daze.class, armor.buffedLvl()*1.1f);
			Buff.affect(defender, Vertigo.class, armor.buffedLvl()*1.1f);
			Buff.affect(attacker, Daze.class, armor.buffedLvl()*.7f);
			Buff.affect(attacker, Vertigo.class, armor.buffedLvl()*.7f);
		}

		if ( Random.Float() < procChance ) {
			ScrollOfTeleportation.teleportChar(attacker);
			noDamage = true;
			Buff.affect(attacker, Daze.class, armor.buffedLvl()*1.1f);
			Buff.affect(attacker, Vertigo.class, armor.buffedLvl()*1.1f);
			Buff.affect(defender, Daze.class, armor.buffedLvl()*.7f);
			Buff.affect(defender, Vertigo.class, armor.buffedLvl()*.7f);
		}


		return !noDamage ? damage : 0;
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
