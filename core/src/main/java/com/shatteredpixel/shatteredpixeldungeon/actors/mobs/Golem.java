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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GolemSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Golem extends Mob {
	
	{
		spriteClass = GolemSprite.class;
		
		HP = HT = 140;
		defenseSkill = 18;
		baseSpeed = 0.5f;
		
		EXP = 12;
		maxLvl = 22;

		loot = Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR);
		lootChance = 0.2f; //initially, see lootChance()

		properties.add(Property.INORGANIC);
		//properties.add(Property.LARGE);

		WANDERING = new Wandering();
		HUNTING = new Hunting();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 25, 35 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 28;
	}
	
	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 12);
	}

	@Override
	public float lootChance() {
		//each drop makes future drops 1/3 as likely
		// so loot chance looks like: 1/5, 1/15, 1/45, 1/135, etc.
		return super.lootChance() * (float)Math.pow(1/3f, Dungeon.LimitedDrops.GOLEM_EQUIP.count);
	}

	@Override
	public void rollToDropLoot() {
		Imp.Quest.oldProcess( this );
		super.rollToDropLoot();
	}

	@Override
	public void damage( int dmg, Object src ) {
		if (src != null && AntiMagic.RESISTS.contains(src.getClass())) {
			Sample.INSTANCE.play( Assets.Sounds.HIT_PARRY, 0.75f, 0.95f);
			dmg *= 0.35f;
			Buff.affect(this, Barrier.class).setShield(1);
		}
		super.damage(dmg, src);
	}

	@Override
	public String defenseVerb() {
		return "blocked";
	}

	@Override
	public int defenseProc( Char enemy, int damage ) {

		if (!Dungeon.level.adjacent(pos, enemy.pos)) {
			Sample.INSTANCE.play( Assets.Sounds.HIT_PARRY, 0.75f, 0.95f);
			damage *= 0.35f;
			Buff.affect(this, Barrier.class).setShield(1);
		}

		return super.defenseProc(enemy, damage);
	}

	public Item createLoot() {
		Dungeon.LimitedDrops.GOLEM_EQUIP.count++;
		//uses probability tables for demon halls
		if (loot == Generator.Category.WEAPON){
			return Generator.randomWeapon(5, true);
		} else {
			return Generator.randomArmor(5);
		}
	}

}
