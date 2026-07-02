package com.shatteredpixel.shatteredpixeldungeon.items;

public class test {



    // hiiiiiii


    public static void main(String[] args) {

        int StartingApples = 10;
        int AppleAmountStolen = 6;
        //int OriginalApples = AppleAmountStolen + StartingApples;
        float HalfApples = StartingApples / 2f;
        //boolean KnifeNeeded = HalfApples % 1 == 0;   //check if decimal
        int AppleDifference = StartingApples - AppleAmountStolen;

        System.out.println("I had " + StartingApples + " apples but somebody stole them. Now I only have " + AppleDifference);
        //System.out.println("I originally had " + OriginalApples + " apples.");
        System.out.println("If he had asked for " + HalfApples + " we could have shared them equally.");

        if (HalfApples % 1 == 0) {
            System.out.println("It is true that I would need a knife to split them.");

        }

    }
}
