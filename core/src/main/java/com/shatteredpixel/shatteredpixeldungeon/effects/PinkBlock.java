package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.Gizmo;

public class PinkBlock extends Gizmo {

    private CharSprite target;

    public PinkBlock( CharSprite target ) {
        super();

        this.target = target;
    }

    @Override
    public void update() {
        super.update();

        target.rm = 1.00f; target.gm = 0.40f; target.bm = 0.40f;
        target.ra = 1.0f; target.ga = 0.70f; target.ba = 0.70f;

    }

    public void lighten() {

        target.resetColor();
        killAndErase();

    }

    public static PinkBlock darken( CharSprite sprite ) {

        PinkBlock pinkBlock = new PinkBlock( sprite );
        if (sprite.parent != null)
            sprite.parent.add( pinkBlock );

        return pinkBlock;
    }

}