package com.shatteredpixel.shatteredpixeldungeon.items;


public class test {

    static float numberUsed = 1; //declaring a variable does not need to be in the method


    static class ClassName{
        String stringObject = "hello everyone";
    }

    public static void main(String[] args) { //main method

        ClassName nameObject = new ClassName();

        System.out.println(nameObject.stringObject.length());
        // prints "14"

        System.out.println(nameObject.stringObject);
        //prints "hello everyone"
        // what this is doing is getting the "stringObject" variable inside the "nameObject" object i created above


        String variable = "test";

        System.out.println("Variable length: " + variable.length());



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


