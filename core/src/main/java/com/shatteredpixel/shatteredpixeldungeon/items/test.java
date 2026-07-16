package com.shatteredpixel.shatteredpixeldungeon.items;
class Coffee {
    String ingredient1;
    String ingredient2;


}
class Main {
    public static String myMethod(String test1, String test2) {
        return test1 + test2;

    }

    public void main(String[] args) {
        Coffee coffee1 = new Coffee();
        coffee1.ingredient1 = "Espresso";
        coffee1.ingredient2 = "Milk";
    }

}

