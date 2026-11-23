package com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranged;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Crossbow extends RangedWeapon {

    {
        tier = 4;
    }
    @Override
    protected int unloadedTexture() {
        return ItemSpriteSheet.CROSSBOW_1;
    }
    @Override
    protected int loadedTexture() {
        return ItemSpriteSheet.CROSSBOW_2;
    }
    @Override
    protected int projectileTexture() {
        return ItemSpriteSheet.CROSSBOW_BOLT;
    }
    @Override
    protected String projectileHitsound() {
        return Assets.Sounds.HIT;
    }
}
