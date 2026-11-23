package com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranged;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Bow extends RangedWeapon {

    {
        tier = 2;
    }
    @Override
    protected int unloadedTexture() {
        return ItemSpriteSheet.BOW_1;
    }
    @Override
    protected int loadedTexture() {
        return ItemSpriteSheet.BOW_2;
    }
    @Override
    protected int projectileTexture() {
        return ItemSpriteSheet.BOW_ARROW;
    }
    @Override
    protected String projectileHitsound() {
        return Assets.Sounds.HIT_ARROW;
    }
}
