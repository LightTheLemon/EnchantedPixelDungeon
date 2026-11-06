package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
public class EmptyBottle extends Item {


    {
        image = ItemSpriteSheet.EMPTY_BOTTLE;

        bones = true;

        stackable = true;
    }

    @Override
    protected void onThrow( int cell ) {
        if (Dungeon.level.map[cell] == Terrain.WELL || Dungeon.level.pit[cell] ) {
            super.onThrow(cell);

        } else {
            shatter( cell );
        }
    }

    public void shatter( int cell ) {
        splash( cell );
        if (Dungeon.level.heroFOV[cell]) {
            GLog.i( Messages.get(this, "shatter_empty") );
            Sample.INSTANCE.play( Assets.Sounds.SHATTER );
        }
        Char ch = Dungeon.level.findMob(cell);
        if (ch != null) {
            Buff.affect(ch, Bleeding.class).set(Dungeon.hero.HT/8f);
        }
    }

    protected void splash( int cell ) {

        Char ch = Actor.findChar(cell);
        if (Dungeon.level.heroFOV[cell]) {
            if (ch != null) {
                Splash.at(ch.sprite.center(), 0xFFFFFF, 5);
            } else {
                Splash.at(cell, 0xFFFFFF, 5);
            }
        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return 20 * quantity;
    }

}
