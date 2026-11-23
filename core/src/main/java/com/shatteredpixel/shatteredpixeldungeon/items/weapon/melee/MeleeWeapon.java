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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Crystal;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class MeleeWeapon extends Weapon {

	public int tier;

	@Override
	public int min(int lvl) {
		return  tier +  //base
				lvl;    //level scaling
	}

	@Override
	public int max(int lvl) {
		return  5*(tier+1) +    //base
				lvl*(tier+1);   //level scaling
	}

	public int STRReq(int lvl){
		int req = STRReq(tier, lvl);
		req -= 2*masteryPotionBonus;

		return req;
	}

	private static boolean evaluatingTwinUpgrades = false;
	@Override
	public int buffedLvl() {
		if (!evaluatingTwinUpgrades && Dungeon.hero != null && isEquipped(Dungeon.hero) && Dungeon.hero.hasTalent(Talent.TWIN_UPGRADES)){
			KindOfWeapon other = null;
			if (Dungeon.hero.belongings.weapon() != this) other = Dungeon.hero.belongings.weapon();
			if (Dungeon.hero.belongings.secondWep() != this) other = Dungeon.hero.belongings.secondWep();

			if (other instanceof MeleeWeapon) {
				evaluatingTwinUpgrades = true;
				int otherLevel = other.buffedLvl();
				evaluatingTwinUpgrades = false;

				//weaker weapon needs to be 2/1/0 tiers lower, based on talent level
				if ((tier + (3 - Dungeon.hero.pointsInTalent(Talent.TWIN_UPGRADES))) <= ((MeleeWeapon) other).tier
						&& otherLevel > super.buffedLvl()) {
					return otherLevel;
				}

			}
		}
		return super.buffedLvl();
	}

	@Override
	public int damageRoll(Char owner) {
		int damage = augment.damageFactor(super.damageRoll( owner ));

		if (owner instanceof Hero) {
			int exStr = ((Hero)owner).STR() - STRReq();

			if (exStr > 0) {
				damage += Hero.heroDamageIntRange( 0, exStr );
			}
			if ( exStr >= 10 && owner.buff(RingOfMight.Might.class) != null) {
				damage *= (int) Math.round(Math.pow(1.1, exStr - 5));
			}

		}
		return damage;
	}
	@Override
	public String defaultAction() {
		if (Dungeon.hero != null && (Dungeon.hero.heroClass == HeroClass.DUELIST
				|| Dungeon.hero.hasTalent(Talent.SWIFT_EQUIP))){
			return AC_ABILITY;
		} else {
			return super.defaultAction();
		}
	}
	
	@Override
	public String info() {

		String info = super.info();

		if (levelKnown) {
			info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_known", tier, augment.damageFactor(min()), augment.damageFactor(max()), STRReq());
			if (Dungeon.hero != null) {
				if (STRReq() > Dungeon.hero.STR()) {
					info += " " + Messages.get(Weapon.class, "too_heavy");
				} else if (Dungeon.hero.STR() > STRReq()) {
					info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - STRReq());

					int exStr = Dungeon.hero.STR() - STRReq();
					if (exStr >= 10 && Dungeon.hero.buff(RingOfMight.Might.class) != null ) {
						info += " " + Messages.get(Weapon.class, "might_bonus", 100 - (100 * Math.round(Math.pow(1.1, exStr - 5))));
					}
				}
			}
		} else {
			info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_unknown", tier, min(0), max(0), STRReq(0));
			if (Dungeon.hero != null && STRReq(0) > Dungeon.hero.STR()) {
				info += " " + Messages.get(MeleeWeapon.class, "probably_too_heavy");
			}
		}

		String statsInfo = statsInfo();
		if (!statsInfo.equals("")) info += "\n\n" + statsInfo;

		switch (augment) {
			case SPEED:
				info += " " + Messages.get(Weapon.class, "faster");
				break;
			case DAMAGE:
				info += " " + Messages.get(Weapon.class, "stronger");
				break;
			case NONE:
		}

		if (isEquipped(Dungeon.hero) && !hasCurseEnchant() && Dungeon.hero.buff(HolyWeapon.HolyWepBuff.class) != null
				&& (Dungeon.hero.subClass != HeroSubClass.PALADIN || enchantment == null)){
			info += "\n\n" + Messages.capitalize(Messages.get(Weapon.class, "enchanted", Messages.get(HolyWeapon.class, "ench_name", Messages.get(Enchantment.class, "enchant"))));
			info += " " + Messages.get(HolyWeapon.class, "ench_desc");
		} else if (enchantment != null && (cursedKnown || !enchantment.curse())){
			info += "\n\n" + Messages.capitalize(Messages.get(Weapon.class, "enchanted", enchantment.name()));
			if (enchantHardened) info += " " + Messages.get(Weapon.class, "enchant_hardened");
			info += " " + enchantment.desc();
		} else if (enchantHardened){
			info += "\n\n" + Messages.get(Weapon.class, "hardened_no_enchant");
		}

		if (cursed && isEquipped( Dungeon.hero )) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
		} else if (cursedKnown && cursed) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed");
		} else if (!isIdentified() && cursedKnown){
			if (enchantment != null && enchantment.curse()) {
				info += "\n\n" + Messages.get(Weapon.class, "weak_cursed");
			} else {
				info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
			}
		}

		//the mage's staff has no ability as it can only be gained by the mage
		if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.DUELIST && !(this instanceof MagesStaff)){
			info += "\n\n" + abilityInfo();
		}
		
		return info;
	}

	@Override
	public String status() {
		if (isEquipped(Dungeon.hero)
				&& Dungeon.hero.buff(Charger.class) != null) {
			Charger buff = Dungeon.hero.buff(Charger.class);
			return buff.charges + "/" + buff.chargeCap();
		} else {
			return super.status();
		}
	}

	@Override
	public int value() {
		int price = 20 * tier;
		if (hasGoodEnchant()) {
			price *= 1.5f;
			if (enchantment instanceof Crystal){
				price *= 3;
			}
		}
		if (cursedKnown && (cursed || hasCurseEnchant())) {
			price /= 2;
		}
		if (levelKnown && level() > 0) {
			price *= (level() + 1);
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}

}
