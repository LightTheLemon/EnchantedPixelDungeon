package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.OptionSlider;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ColorBlock;

import java.util.ArrayList;

public class WndDifficulty extends Window {

    private static final int WIDTH		= 120;
    private static final int TTL_HEIGHT = 16;
    OptionSlider difficultySlider;
    private int numberOfStats = 0;
    private float oldDifficulty = -1;
    private String difficultyDescription = "Medium";
    private RenderedTextBlock difficultyText = PixelScene.renderTextBlock(( String.valueOf( getDifficulty() ) + ": " + difficultyDescription), 8 );
    private RenderedTextBlock armorStats = PixelScene.renderTextBlock(( String.valueOf( "Armor effectiveness: " + 100 * Math.round((getDifficulty() / -4) + 1.25f)) + "%"), 5 );
    private RenderedTextBlock bossStats = PixelScene.renderTextBlock(( String.valueOf( "Boss health: " + 100 * Math.round((getDifficulty() / 8f) + 0.875f) + "%")), 5 );
    private RenderedTextBlock foodStats = PixelScene.renderTextBlock(( String.valueOf( "Food effectiveness: " + Math.round( 100 / getDifficulty())) + "%"), 5);
    private RenderedTextBlock potionStats = PixelScene.renderTextBlock(String.valueOf("Potion of health healing: " + Math.round(100 * (getDifficulty() / -1.8 ) + 1.56)) + "%", 5);
    private RenderedTextBlock championStats = PixelScene.renderTextBlock(( String.valueOf( "Hostile champion spawn rate: " + Math.round(1f / (17f - (3 * Dungeon.difficulty)))) + "%"), 5 );
    private RenderedTextBlock dewdropStats = PixelScene.renderTextBlock(( String.valueOf( "Dewdrop rate: " + Math.round(25 * Math.min(1.5f, Math.max(0f, ( getDifficulty() / -2f ) + 1.5f))) + "%")), 5 );
    private RenderedTextBlock plantStats = PixelScene.renderTextBlock( String.valueOf( "Seeds do not sprout."), 5 );
    private RenderedTextBlock regenStats = PixelScene.renderTextBlock(( String.valueOf( "Health regeneration speed: " + Math.round(100 / (getDifficulty() / 3 ) + 0.6)) + "%"), 5);
    private RenderedTextBlock upgradeStats = PixelScene.renderTextBlock(( String.valueOf( "Upgrade scrolls: " + Math.max(25 , Math.round(100 * ((getDifficulty()/-5f) + 0.95f))) + "% chance per floor")), 5 );
    private RenderedTextBlock swarmIntelStats = PixelScene.renderTextBlock(( String.valueOf( "Swarm intelligence counter: " + ((getDifficulty() / 2f) - 0.5f)) + " per turn"), 5 );


    public WndDifficulty( float value, boolean editable ) {

        super();

        RenderedTextBlock title = PixelScene.renderTextBlock( Messages.get(this, "title"), 12 );
        title.hardlight( TITLE_COLOR );
        title.setPos(
                (WIDTH - title.width()) / 2,
                (TTL_HEIGHT - title.height()) / 2
        );
        PixelScene.align(title);
        add( title );

        difficultySlider = new OptionSlider(" ",
                Messages.get(this, "easy"), Messages.get(this, "hard"), 0, 40) {
            @Override
            protected void onChange() {
                SPDSettings.difficulty(getSelectedValue());
            }

        };
        difficultySlider.setSelectedValue( SPDSettings.difficulty() );
        difficultySlider.enable(editable);
        difficultySlider.setRect(0, TTL_HEIGHT + 85, WIDTH, 16);
        add(difficultySlider);

        if      (getDifficulty() < 0.3)    difficultyDescription = Messages.get(this, "very_easy");
        else if (getDifficulty() < 1.0)    difficultyDescription = Messages.get(this, "easy");
        else if (getDifficulty() < 2.0)    difficultyDescription = Messages.get(this, "medium");
        else if (getDifficulty() < 3.3)    difficultyDescription = Messages.get(this, "hard");
        else                               difficultyDescription = Messages.get(this, "extreme");
        difficultyText.hardlight( WHITE );
        difficultyText.setPos(
                0,
                90
        );
        PixelScene.align(title);
        add( difficultyText );

        resize( WIDTH, TTL_HEIGHT + Math.max(100, numberOfStats * 10));

        difficultyStats();

    }

    @Override
    public void update() {

        if (oldDifficulty != getDifficulty() ) {

            difficultyStats();

            if      (getDifficulty() < 0.3)    difficultyDescription = Messages.get(this, "very_easy");
            else if (getDifficulty() < 1.0)    difficultyDescription = Messages.get(this, "easy");
            else if (getDifficulty() < 2.0)    difficultyDescription = Messages.get(this, "medium");
            else if (getDifficulty() < 3.3)    difficultyDescription = Messages.get(this, "hard");
            else                               difficultyDescription = Messages.get(this, "extreme");

            difficultyText.text( String.valueOf( getDifficulty() )  + ": " + difficultyDescription, 60 );

            resize( WIDTH, TTL_HEIGHT + Math.max(100, numberOfStats * 10));

            oldDifficulty = getDifficulty();

            System.out.println("Difficulty: " + getDifficulty());

        }

    }

    public void difficultyStats() {

        add(bossStats);
        bossStats.hardlight(WHITE);
        bossStats.setPos(
                0,
                18
        );
        bossStats.text(( String.valueOf( "Boss health: " + Math.round(100 * ((getDifficulty() / 8) + 0.875f))) + "%"), WIDTH );

        add(dewdropStats);
        dewdropStats.hardlight(WHITE);
        dewdropStats.setPos(
                0,
                25
        );
        dewdropStats.text(( String.valueOf( "Dewdrop drop rate: " + Math.round(25 * Math.min(1.5f, Math.max(0f, ( getDifficulty() / -2f ) + 1.5f))) + "%")), WIDTH );

        add(upgradeStats);
        upgradeStats.hardlight(WHITE);
        upgradeStats.setPos(
                0,
                32
        );
        upgradeStats.text(( String.valueOf( "Upgrade scrolls: " + Math.max(25 , Math.round(100 * ((getDifficulty()/-5f) + 0.95f))) + "% chance per floor")), WIDTH );


        if (getDifficulty() >= 1) {

            remove(regenStats);

            add(armorStats);
            armorStats.hardlight(WHITE);
            armorStats.setPos(
                    0,
                    39
            );
            armorStats.text(( String.valueOf( "Armor effectiveness: " + Math.round(100 * ((getDifficulty() / -4) + 1.25f))) + "%"), WIDTH );

            add(foodStats);
            foodStats.hardlight(WHITE);
            foodStats.setPos(
                    0,
                    46
            );
            foodStats.text(String.valueOf("Food effectiveness: " + Math.round( 100 / getDifficulty())) + "%", WIDTH);

            add(potionStats);
            potionStats.hardlight(WHITE);
            potionStats.setPos(
                    0,
                    53
            );
            potionStats.text(String.valueOf("Potion of health healing: " + Math.round(100 * ((getDifficulty() / -1.8 ) + 1.56))) + "%", WIDTH);

        } else {
            remove(foodStats);
            remove(potionStats);
            remove(armorStats);

            add(regenStats);
            regenStats.hardlight(WHITE);
            regenStats.setPos(
                    0,
                    39
            );
            regenStats.text(( String.valueOf( "Health regeneration speed: " + Math.round(100 / ((getDifficulty() / 3 ) + 0.6))) + "%"), WIDTH);

        }

        if (getDifficulty() > 1) {
            add(championStats);
            championStats.hardlight(WHITE);
            championStats.setPos(
                    0,
                    60
            );
            championStats.text(( String.valueOf( "Hostile champion spawn rate: " + Math.round(100 * (1f / (17f - (3 * getDifficulty()))))) + "%"), WIDTH );
        } else {
            remove(championStats);
        }

        if (getDifficulty() >= 1.5) {
            add(swarmIntelStats);
            swarmIntelStats.hardlight(WHITE);
            swarmIntelStats.setPos(
                    0,
                    67
            );
            swarmIntelStats.text(( String.valueOf( "Swarm intelligence counter: +" + Messages.decimalFormat("#.#", (getDifficulty() / 2f))) + " per turn"), WIDTH );
        } else {
            remove(swarmIntelStats);
        }

        if (getDifficulty() >= 3) {
            add(plantStats);
            plantStats.hardlight(TITLE_COLOR);
            plantStats.setPos(
                    0,
                    74
            );
            plantStats.text( String.valueOf( "Seeds do not sprout."), WIDTH );
        } else {
            remove(plantStats);
        }




    }

    public static float getDifficulty() {
        //If there's not an active game, use slider value. Otherwise, use the game's difficulty.
        if (Dungeon.hero != null) {
            return Dungeon.difficulty;
        } else {
            return (float) SPDSettings.difficulty() / 10;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
