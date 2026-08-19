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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Sacrificial extends Weapon.Enchantment {

	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	private float hits = 1;

	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage ) {

		float procChance = 1/6f * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {
			if (attacker.buff(Healing.class) == null) {
				Buff.affect(attacker, Bleeding.class).set( (attacker.HT/10f) * hits, getClass() );
			}
			hits += 0.2f;
		}
		if (damage >= defender.HP) {
			//Other possible conditions: && attacker.HP < attacker.HT * 0.75f, && attacker.buff(Bleeding.class) != null
			//Do I want to increase the healing for more hits? I think not.
			hits = 1;
			if (attacker.buff(Bleeding.class) != null) {
				Buff.detach(attacker, Bleeding.class);
			} else if (attacker.buff(Healing.class) != null)
                Buff.affect(attacker, Healing.class).setHeal(attacker.HT / 5, 0.01f, attacker.HT / 20);  //extra healing to reward consecutive kills
            else {
				Buff.affect(attacker, Healing.class).setHeal(attacker.HT / 10, 0.01f, attacker.HT / 20);
			}
		}

		return damage;
	}

	@Override
	public boolean curse() {
		return true;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

}
