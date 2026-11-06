package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class BlankScroll extends Item {

    {
        image = ItemSpriteSheet.BLANK_SCROLL;

        bones = true;

        stackable = true;
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
