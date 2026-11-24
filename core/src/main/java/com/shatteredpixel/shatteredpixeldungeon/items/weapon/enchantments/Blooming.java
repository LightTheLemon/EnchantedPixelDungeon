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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Blooming extends Weapon.Enchantment {
	
	private static ItemSprite.Glowing DARK_GREEN = new ItemSprite.Glowing( 0x008800 );
	
	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		int level = Math.max( 0, weapon.buffedLvl() );

		// lvl 0 - 33%
		// lvl 1 - 50%
		// lvl 2 - 60%
		float procChance = (level+1f)/(level+3f) * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {

			float powerMulti = Math.max(1f, procChance);

			float plants = (1f + 0.1f*level) * powerMulti;
			if (Random.Float() < plants%1){
				plants = (float)Math.ceil(plants);
			} else {
				plants = (float)Math.floor(plants);
			}

			if (Dungeon.level.map[defender.pos] == Terrain.HIGH_GRASS) {
				if (Dungeon.level.adjacent(defender.pos, attacker.pos)) {
					Buff.prolong(defender, Roots.class, 10f);
				} else {
					Buff.prolong(defender, Roots.class, 5f);
				}
			}
			
			if (plantGrass(defender.pos)){
				plants--;
				if (plants <= 0){
					return damage;
				}
			}
			
			ArrayList<Integer> positions = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS8){
				if (defender.pos + i != attacker.pos) {
					positions.add(defender.pos + i);
				}
			}
			Random.shuffle( positions );

			//The attacker's position is always lowest priority
			if (Dungeon.level.adjacent(attacker.pos, defender.pos)){
				positions.add(attacker.pos);
			}

			for (int i : positions){
				if (plantGrass(i)){
					plants--;
					if (plants <= 0) {
						return damage;
					}
				}
			}
			
		}
		
		return damage;
	}
	
	private boolean plantGrass(int cell){
		int t = Dungeon.level.map[cell];
		boolean plantable = (t == Terrain.EMPTY || t == Terrain.EMPTY_DECO || t == Terrain.EMBERS || t == Terrain.FURROWED_GRASS || t == Terrain.GRASS) && Dungeon.level.plants.get(cell) == null;
		float multi = Weapon.Enchantment.genericProcChanceMultiplier(Dungeon.hero);

		if (plantable){

			Level.set(cell, Regeneration.regenOn() ? Terrain.HIGH_GRASS : Terrain.FURROWED_GRASS);

			//Plants extra grass or plants around
			for (int i : PathFinder.NEIGHBOURS4) {

				if (Random.Float() < 0.30 * multi) {

					if ((Dungeon.level.map[cell + i] == Terrain.EMPTY || Dungeon.level.map[cell + i] == Terrain.EMPTY_DECO || Dungeon.level.map[cell + i] == Terrain.EMBERS) && Dungeon.level.plants.get(cell + i) == null) {

						float c = Random.Float();
						if (c < 0.30 * multi ) {
							Dungeon.level.plant((Plant.Seed) Generator.randomUsingDefaults(Generator.Category.SEED), cell + i);
						} else if (c < 0.50 * multi) {
							Level.set(cell + i, Terrain.HIGH_GRASS);
						} else {
							Level.set(cell + i, Terrain.FURROWED_GRASS);
						}
					}
				}
			}

			GameScene.updateMap(cell);
			CellEmitter.get( cell ).burst( LeafParticle.LEVEL_SPECIFIC, Math.round(4f * multi));
			return true;
		}
		return false;
	}

	@Override
	public String enchantUpgradeStat1(int level) {

		float baseChance = (level+1f)/(level+3f);
		float bonus = Weapon.Enchantment.genericProcChanceMultiplier(Dungeon.hero);

		return Messages.decimalFormat("#.##", 100f * (baseChance * bonus)) + "%";
	}
	
	@Override
	public ItemSprite.Glowing glowing() {
		return DARK_GREEN;
	}
}
