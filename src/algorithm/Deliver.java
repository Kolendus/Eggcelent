package algorithm;

import java.util.ArrayList;

public class Deliver implements INDeliver {


    private String farmName;
    private String shopName;
    private int eggs;
    private int eggCost;
    private static int totalCost = 0;
    private static ArrayList<Deliver> orders = new ArrayList<>();
    private int totalEggs = 0;
    public  ArrayList<Deliver> getOrders() {
        return orders;
    }

    public Deliver(String farmName, String shopName, int eggs, int eggCost) {
        this.farmName = farmName;
        this.shopName = shopName;
        this.eggs = eggs;

        this.eggCost = eggCost;
    }

    public Deliver() {

    }

    public void addDelivery(String farmName, String shopName, int eggs, int eggCost) {
        Deliver newDelivery = new Deliver(farmName, shopName, eggs, eggCost);
        orders.add(newDelivery);
        totalCost += (eggCost * eggs);
        this.totalEggs += eggs;
    }

    public void printTotalEggs(){
        System.out.println("Total amount of eggs -> " + totalEggs);
    }
    public void editOrder(String farmName, String shopName, int newEggs, int eggCost) {
        Deliver orderToEdit = new Deliver(farmName, shopName, newEggs, eggCost);
        Deliver suspectedOrder = findOrder(orderToEdit);
        this.totalEggs -= newEggs;
        if (suspectedOrder != null) {
            findOrder(orderToEdit).changeEggAmount(newEggs, eggCost);
        }
    }

    private Deliver findOrder(Deliver order) {

        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).equals(order)) {
                return orders.get(i);
            }
        }
        return null;
    }

    private void changeEggAmount(int newEggs, int eggCost) {
        int difference = newEggs * eggCost;
        this.totalCost -= difference;
        this.eggs -= newEggs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Deliver deliver = (Deliver) o;

        if (eggCost != deliver.eggCost) return false;
        if (!farmName.equals(deliver.farmName)) return false;
        return shopName.equals(deliver.shopName);
    }

    public void printDeliveries() {
        for (int i = 0; i < orders.size(); i++) {
            System.out.println(orders.get(i).toString());
        }
        System.out.println("Total Cost = " + totalCost);
    }

    public String toString() {
        return farmName + '\t' + ">" + '\t' + shopName + "" +
                "[Summary = " + eggs + " * " + eggCost + " = " + (eggs * eggCost) + "]";
    }
}
