package com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranged;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GreaterHaste;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyWeapon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Crystal;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

abstract public class RangedWeapon extends Weapon {

    public static Class<? extends Item> Projectile = Projectile.class;

    public int tier;
    protected abstract int unloadedTexture();
    protected abstract int loadedTexture();
    protected abstract int projectileTexture();
    protected abstract String projectileHitsound();

    {

        defaultAction = "SHOOT";
        usesTargeting = true;

        image = loadedTexture();
    }
    public boolean reloaded = true;
    public String AC_SHOOT_OR_RELOAD(){
        return reloaded ? "SHOOT" : "RELOAD";
    }

    public int STRReq(int lvl){
        int req = STRReq(tier, lvl);
        req -= 2*masteryPotionBonus;

        return req;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (canUse(hero)) actions.add(AC_SHOOT_OR_RELOAD());
        return actions;
    }
    private boolean canUse(Hero hero) {
        return ( this instanceof SpiritBow || this.isEquipped(hero) );
    }
    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_SHOOT_OR_RELOAD()) && canUse(hero)) {

            curUser = hero;
            curItem = this;

            if (reloaded) {
                GameScene.selectCell( shooter );
            } else {
                reloaded = true;
                image = loadedTexture();
                updateQuickslot();
                Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
                hero.spendAndNext(super.delayFactor(hero));
                defaultAction = "SHOOT";
                hero.sprite.operate(hero.pos);
            }

        }
    }

    //Should fix a bug
    @Override
    public boolean doEquip(Hero hero) {
        defaultAction = AC_SHOOT_OR_RELOAD();
        return super.doEquip(hero);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        defaultAction = AC_SHOOT_OR_RELOAD();
    }

    private CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null) {
                reloaded = false;
                image = unloadedTexture();
                updateQuickslot();
                defaultAction = AC_SHOOT_OR_RELOAD();
                shootProjectile().cast(curUser, target);
            }
        }
        @Override
        public String prompt() {
            return Messages.get(RangedWeapon.class, "prompt");
        }
    };

    protected MissileWeapon shootProjectile() {
        return new Projectile();
    }

    public class Projectile extends MissileWeapon {

        {
            image = projectileTexture();

            hitSound = projectileHitsound();

            setID = 0;
        }

        @Override
        public ArrayList<String> actions(Hero hero) {
            return new ArrayList<>();
        }

        @Override
        public String defaultAction() {
            return null;
        }

        @Override
        public int defaultQuantity() {
            return 1;
        }

        @Override
        public int damageRoll(Char owner) {
            //Projectile dals more damage to account for reloading
            return Math.round(2.0f * RangedWeapon.this.damageRoll(owner));
        }
        @Override
        public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
            return RangedWeapon.this.hasEnchant(type, owner);
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            return RangedWeapon.this.proc(attacker, defender, damage);
        }

        @Override
        protected void onThrow( int cell ) {
            Char enemy = Actor.findChar( cell );
            if (enemy == null || enemy == curUser) {
                parent = null;
                Splash.at( cell, 0xCC99FFFF, 1 );
            } else {
                if (!curUser.shoot( enemy, this )) {
                    Splash.at(cell, 0xCC99FFFF, 1);
                }
            }
        }

        @Override
        public float delayFactor(Char user) {
            return RangedWeapon.this.delayFactor(user);
        }

        @Override
        public int STRReq(int lvl) {
            return RangedWeapon.this.STRReq();
        }

        @Override
        public Item split(int amount) {
            return null;
        }

        @Override
        public float accuracyFactor(Char owner, Char target) {

            return RangedWeapon.this.accuracyFactor(owner, target) * adjacentAccFactor(owner, target) * chargedShotAccFactor(owner, target);
        }

        @Override
        public void throwSound() {
            Sample.INSTANCE.play( Assets.Sounds.ATK_SPIRITBOW, 1, Random.Float(0.87f, 1.15f) );
        }

        @Override
        public void cast( final Hero user, final int dst ) {

            final int cell = throwPos( user, dst );
            user.sprite.zap( cell );
            user.busy();

            throwSound();

            final Char enemy = Actor.findChar( cell );
            QuickSlotButton.target(enemy);

            final float delay = castDelay(user, cell);

            ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
                    reset(user.sprite,
                            cell,
                            this,
                            new Callback() {
                                @Override
                                public void call() {

                                    curUser = user;

                                    reloaded = false;
                                    image = unloadedTexture();
                                    updateQuickslot();

                                    if (enemy != null && enemy.isAlive()) {
                                        onThrow(cell);
                                    }

                                    if (user.buff(Talent.LethalMomentumTracker.class) != null){
                                        user.buff(Talent.LethalMomentumTracker.class).detach();
                                        user.next();
                                    } else {
                                        user.spendAndNext(delay);
                                    }
                                }
                            });
            }

    }

    @Override
    public int targetingPos(Hero user, int dst) {
        return shootProjectile().targetingPos(user, dst);
    }
    protected int targetPos;

    @Override
    public int min() {
        if (Dungeon.hero != null){
            return Math.max(0, min(buffedLvl() + RingOfSharpshooting.levelDamageBonus(Dungeon.hero)));
        } else {
            return Math.max(0 , min( buffedLvl() ));
        }
    }
    @Override
    public int max() {
        if (Dungeon.hero != null){
            return Math.max(0, max( buffedLvl() + RingOfSharpshooting.levelDamageBonus(Dungeon.hero) ));
        } else {
            return Math.max(0 , max( buffedLvl() ));
        }
    }
    @Override
    public int min(int lvl) {
        return  (tier / 2)  +                            //base
                lvl;                                     //level scaling
    }
    @Override
    public int max(int lvl) {
        return  3 * tier +                               //Low melee damage, but arrows deal double damage
                lvl*(tier+1);                            //Same level scaling
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

    @Override
    public String info() {

        String info = super.info();

        if (levelKnown) {
            if (!(this instanceof SpiritBow)) info += "\n\n" + Messages.get(RangedWeapon.class, "stats_known", tier, 2* augment.damageFactor(min()), 2* augment.damageFactor(max()), STRReq());
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
            info += "\n\n" + Messages.get(RangedWeapon.class, "stats_unknown", tier, min(0), max(0), STRReq(0));
            if (Dungeon.hero != null && STRReq(0) > Dungeon.hero.STR()) {
                info += " " + Messages.get(RangedWeapon.class, "probably_too_heavy");
            }
        }

        if (this instanceof SpiritBow) {
            info += "\n\n" + Messages.get( SpiritBow.class, "stats",
                    Math.round(augment.damageFactor(min())),
                    Math.round(augment.damageFactor(max())),
                    STRReq());
        }

        if (Dungeon.hero != null) {
            if (STRReq() > Dungeon.hero.STR()) {
                info += " " + Messages.get(Weapon.class, "too_heavy");
            } else if (Dungeon.hero.STR() > STRReq()){
                info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - STRReq());
            }
        }

        switch (augment) {
            case SPEED:
                info += "\n\n" + Messages.get(Weapon.class, "faster");
                break;
            case DAMAGE:
                info += "\n\n" + Messages.get(Weapon.class, "stronger");
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

        info += "\n\n" + Messages.get(RangedWeapon.class, "distance");

        return info;
    }

    //DUELIST SPECIFIC STUFF
    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)){
            if (hero.buff(ChargedShot.class) != null &&
                    !(hero.belongings.weapon() instanceof Crossbow)
                    && !(hero.belongings.secondWep() instanceof Crossbow)){
                //clear charged shot if no crossbow is equipped
                hero.buff(ChargedShot.class).detach();
            }
            return true;
        } else {
            return false;
        }
    }

    public static void onAbilityKill( Hero hero, Char killed ){
        if (killed.alignment == Char.Alignment.ENEMY && hero.hasTalent(Talent.LETHAL_HASTE)){
            //effectively 3/5 turns of greater haste
            Buff.affect(hero, GreaterHaste.class).set(2 + 2*hero.pointsInTalent(Talent.LETHAL_HASTE));
        }
    }

    @Override
    public float accuracyFactor(Char owner, Char target) {

        return super.accuracyFactor(owner, target) * chargedShotAccFactor(owner, target);
     }

    protected float chargedShotAccFactor(Char owner, Char target) {
        if (owner.buff(Crossbow.ChargedShot.class) != null){
            Actor.add(new Actor() {
                { actPriority = VFX_PRIO; }
                @Override
                protected boolean act() {
                    if (owner instanceof Hero && !target.isAlive()){
                        onAbilityKill((Hero)owner, target);
                    }
                    Actor.remove(this);
                    return true;
                }
            });
            return Float.POSITIVE_INFINITY;
        } else {
            return 1;
        }
    }
    @Override
    public int proc(Char attacker, Char defender, int damage) {
        int dmg = super.proc(attacker, defender, damage);

        //stronger elastic effect
        if (attacker == Dungeon.hero
                && Dungeon.hero.buff(ChargedShot.class) != null
                //not proccing from a dart
                && Dungeon.hero.belongings.attackingWeapon() == this){
            //trace a ballistica to our target (which will also extend past them
            Ballistica trajectory = new Ballistica(attacker.pos, defender.pos, Ballistica.STOP_TARGET);
            //trim it to just be the part that goes past them
            trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
            //knock them back along that ballistica
            WandOfBlastWave.throwChar(defender,
                    trajectory,
                    4,
                    true,
                    true,
                    this);
            attacker.buff(Crossbow.ChargedShot.class).detach();
        }
        return dmg;
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        if (hero.buff(ChargedShot.class) != null){
            GLog.w(Messages.get(this, "ability_cant_use"));
            return;
        }

        beforeAbilityUsed(hero, null);
        Buff.affect(hero, ChargedShot.class);
        hero.sprite.operate(hero.pos);
        hero.next();
        afterAbilityUsed(hero);
    }

    @Override
    public String abilityInfo() {
        if (levelKnown){
            return Messages.get(this, "ability_desc", 3+buffedLvl(), 3+buffedLvl());
        } else {
            return Messages.get(this, "typical_ability_desc", 3, 3);
        }
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return Integer.toString(3 + level);
    }

    //Dart min and max for duelist
//    public int dartMin(){
//        return dartMin(buffedLvl());
//    }
//
//    public int dartMin(int lvl){
//        return  4 +     //4 base, up from dart base of 1
//                lvl;    //+1 per level
//    }
//
//    public int dartMax(){
//        return dartMax(buffedLvl());
//    }
//
//    public int dartMax(int lvl){
//        return  12 +    //12 base, up from dart base of 2
//                3*lvl;  //+3 per crossbow level
//    }

//    public String statsInfo(){
//        if (isIdentified()){
//            return Messages.get(this, "stats_desc", dartMin(), dartMax());
//        } else {
//            return Messages.get(this, "typical_stats_desc", dartMin(0), dartMax(0));
//        }
//    }

    public static class ChargedShot extends Buff{

        {
            announced = true;
            type = buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return BuffIndicator.DUEL_XBOW;
        }

    }


}
