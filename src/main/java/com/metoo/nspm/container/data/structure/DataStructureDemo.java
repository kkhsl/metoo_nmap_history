package com.metoo.nspm.container.data.structure;

public class DataStructureDemo {

    public static void main(String[] args) {
        System.out.println("所有汽车的价格：");
        for (Car c : Car.values())
            System.out.println(c + " 需要 " + c.getPrice() + "||" + c.getPrice1() + " 千美元。");
    }
}

enum Car {

    lamborghini(900, 1), tata(2), audi(50), fiat(15), honda(12);

    private int price;
    private int price1;


    Car(int p) {
        price = p;
    }

    Car(int p, int a) {
        price = p;
        price1 = a;
    }

    int getPrice() {
        return price;
    }

    int getPrice1() {
        return price1;
    }
}