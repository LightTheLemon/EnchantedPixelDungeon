package com.shatteredpixel.shatteredpixeldungeon.items;

public class test {

    static float numberUsed = 1; //declaring a variable does not need to be in the method


    class ClassName{String variable = "test";}

    public static void main(String[] args) { //main method

        String variable = "test";

        System.out.println("Variable length: " + variable.length());

        /*
        boolean[] myArray = {true, true, false}; //simple

        System.out.println(myArray.length);
        */

        while (numberUsed < 2) {

            System.out.println("Counter: " + numberUsed); numberUsed = numberUsed + 1; //change value to increment 1 until condition is met

            //System.out.println("Mwahahaha: " + numberUsed); numberUsed = numberUsed - 0.5f; //change value to increment 1 until condition is met

            //myMethod();

            System.out.println("Method: " + myMethod());

            System.out.println("Method + 1: " + (myMethod() + 1));

        }


    }

    public static int myMethod(){

        boolean[] myArray = {true, true, false};

        //System.out.println(myArray.length);

        return 0;
    }



    // hiiiiiii
    /*

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
    */
}
