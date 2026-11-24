package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

import java.util.ArrayList;

public class EPD_v0_0_X_Changes {

    public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){
        //add_v0_0_4_Changes(changeInfos);
        add_v0_0_3_Changes(changeInfos);
        add_v0_0_2_Changes(changeInfos);
        add_v0_0_1_Changes(changeInfos);
    }

    public static void add_v0_0_1_Changes(ArrayList<ChangeInfo> changeInfos ) {

        ChangeInfo changes = new ChangeInfo("v0.0.1", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(ChangeIcons.V040_STYLUS, "Enchantments",
                        "\n" +
                        "**-** Reworked Arcane Stylus:\n" +
                        "\n" +
                        "Has a 50% chance to enchant a weapon or armor, and a 50% chance to curse it. Increased drop rate." +
                        "\n\n" +
                        "**-** New enchantments:\n" +
                        "\n" +
                        "**Covert**: always grants invisibility on kill, with the duration based on the weapon's level" +
                        "\n\n" +
                        "**Culinary**: chance to drop food on kill and modifies enemy loot tables" +
                        "\n\n" +
                        "Added upgrade descriptions for each enchantment" +
                        "\n\n"));

        changes.addButton(new ChangeButton( ChangeIcons.V022_REMAINS , "Curses",
                "\n" +
                        "**-** Weapon curse changes:\n" +
                        "\n" +
                        "**Sacrificial**: makes you bleed on hit, but restores health on kill." +
                        "\n\n" +
                        "**Wayward**: Higher damage with debuff. However, overall lower accuracy" +
                        "\n\n" +
                        "**Annoying**: Also amoks some enemies" +
                        "\n\n" +
                        "Curse proc rates now increase with level" +
                        "\n\n" +
                        "Other curse buffs and armor curse changes" +
                        "\n\n"));


        changes.addButton(new ChangeButton( ChangeIcons.V23_ARROWS , "Balance changes",
                "\n" +
                        "**-** Food changes:\n" +
                        "**Frozen** meat has a 40% chance to give barkskin buff, which can now stack. No other effects.\n" +
                        "**Stewed** meat has a 40% chance to turn you invisible\n" +
                        "**Chargrilled** meat restores a higher amount of satiety\n\n" +
                        "**-** Other changes:\n" +
                        "**Blessed Ankh** buffed. Fully restores health and hunger\n" +
                        "**Potion of Healing** now heals instantly and can be thrown to heal allies\n" +
                        "Reduced chasm damage\n" +
                        "Added sip option on Dew Vial\n"));

        changes.addButton(new ChangeButton( ChangeIcons.EPD_WELL_ENCH , "Well Of Enchantment",
                "\n" +
                        "Drinking from the **Well Of Enchantment** will remove the curses on all cursed items and enchant all non-cursed items!" +
                        "\n\n" +
                        "**-** Updated the **Well of Health**: Drinking from the well of health no longer uncurses your items, but you do get a well fed buff." +
                        "\n\n" +
                        "Both of these wells will upgrade an item if you throw in an upgradable item.\n"));
    }

    public static void add_v0_0_2_Changes(ArrayList<ChangeInfo> changeInfos ) {

        ChangeInfo changes = new ChangeInfo("v0.0.2", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton( ChangeIcons.EPD_CHARM , "Wands!",
                "\n" +
                        "Added **Wand of Charm**!\n(Replaces Wand of Transfusion)\n\n" +
                        "Apply charm effect to any enemy, preventing it from attacking you. Stack this effect as much as you can to attack defenseless enemies. Enemies with enough charm may become allies.\n" +
                        "**Arcane Resin** now gives permanent upgrades with no cap. Cost increased to 8 energy.\n"));

        changes.addButton(new ChangeButton( ChangeIcons.EPD_STOPWATCH , "Artifacts!",
                "\n" +
                        "New artifact: **Cape Of Thorns**. Charges with damage dealt or taken. Activate buff to tank damage and deal damage to your attacker.\n\n" +
                        "Timekeeper's Hourglass split into two artifacts:\n" +
                        "**- Phantom Stopwatch**: Activate to stop time! You can also attack in stopped time, with reduced damage.\n" +
                        "**- Timekeeper's Hourglass**: Activate for one turn of stasis. Repeated activations will give you stasis for longer.\n\n" +
                        "Updated **Dried Rose**: summon the ghost to any location at any time. Ghost can now equip cursed equipment or equipment without enough strength, but at a cost!\n" +
                        "**Ethereal Chains** will stun and cripple enemies if you chain them\n" +
                        "**Unstable Spellbook** will always tell you the next scroll effect\n" +
                        "**Arcane resin** now gives permanent upgrades with no cap.\n"));

        changes.addButton(new ChangeButton( ChangeIcons.V064_CHALLENGES , "Choose your Difficulty!",
                "\n" +
                        "Added a new difficulty slider which replaces challenges.\n\n1.0 difficulty is the default, 0.0 is a new easy mode, and 3.0 is about as difficult as 9 chal (all challenges active)\n"));

        changes.addButton(new ChangeButton( ChangeIcons.V22_PICKAXE , "Pickaxe!",
                "\n" +
                        "You can now mine with the **Pickaxe** from the Troll Blacksmith! It costs 3000 favor." +
                        "\n\nYou cannot break into locked areas, or walls on boss floors besides DM-300. Everything else is fair game though."));


        changes.addButton(new ChangeButton( ChangeIcons.V071_RING_RUBY , "Rings!",
                "\n" +
                        "**-**Added a new ring: The **Ring Of Conservation** gives you a chance to recycle your consumable items on use. Basically, you'll be able to use it without it being removed from your inventory!\n" +
                        "However, the chance for this is much smaller for scrolls of upgrade or potions of strength.\n\n" +
                        "**-**Ring of Might split into two rings:\n" +
                        "**Ring of Vitality**: Increases your max HP significantly, but healing recieved doesn't scale as much.\n" +
                        "**Ring of Might**: No more max health increase, but now you have an additional bonus if you have 10 or more excess strength.\n\n" +
                        "The **Ring of Accuracy** now also averages the damage of your attacks.\n" +
                        "The **Ring Of Elements** now extends the length of your buffs! In addition to decreasing debuffs.\n" +
                        "The **Ring Of Furor** now applies to wands.\n"));

    }

    public static void add_v0_0_3_Changes(ArrayList<ChangeInfo> changeInfos ) {

        ChangeInfo changes = new ChangeInfo("v0.0.3", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton( ChangeIcons.EPD_BOW , "Archery!",
                "\n" +
                        "New tier-2 **Bow**: Takes one turn to fire, and one to reload. Deals decent damage, but of course more effective at long range. You don't have to worry about ammo.\n\n" +
                        "The **Crossbow** and **Spirit Bow** now have the reloading mechanic as the new bow.\n" +
                        "Spirit Bow damage increased to compensate for reloading. Thrown weapon/dart effects removed for Crossbow.\n\n" +
                        "Updated boomerang!"));

        changes.addButton(new ChangeButton( ChangeIcons.EPD_BLANK_SCROLL , "New Alchemy/Crafting System!",
                "\n" +
                        "**Alchemy tables produce the following:**\n\n" +
                        "**-** Identify scroll or potion (if unidentified).\n" +
                        "**-** Craft brews or spells (if identified).\n" +
                        "**-** Duplicate a scroll or potion with a template!\n\n" +
                        "Added blank scrolls and empty bottles for crafting.\n" +
                        "**Removed exotic scrolls/potions**\n" +
                        "Updated **potion of invisibility**: throwing acts like a potion of shrouding fog.\n" +
                        "New potion of haste: **potion of ENERGY**. Throw it to create an electrical storm (if you don't want to drink it).\n" +
                        "Each dungeon now generates with a unique set of scrolls and potions.\n" +
                        "Added crafting menu for scrolls."));

//        changes.addButton(new ChangeButton( ChangeIcons.EPD_BARREL , "Breakable barrels!\n",
//                "**You can now break half-broken sewer barrels if you click on them. They have a chance to drop an item or a lit bomb.\n\n"));

        changes.addButton(new ChangeButton( ChangeIcons.EPD_HAIL_MARY , "Added new trinkets!",
                "\n" +
                        "**Hail Mary**: Gives you a chance to survive on death! However, you gain a slight damage vulnerability.\n\n" +
                        "**-**Dimensional Sundial split into two trinkets:\n" +
                        "**Burning Sage**: Decreases enemy spawn rate.\n" +
                        "**Amulet Shard**: Increases enemy spawn rate.\n" +
                        "Other trinkets changed:\n" +
                        "Wondrous Resin now cheaper but cursed wand effect replaces wand effect.\n" +
                        "Exotic crystals now replace gold with alchemical energy\n"));



    }

    private static void add_v0_0_4_Changes(ArrayList<ChangeInfo> changeInfos) {

    }



}
