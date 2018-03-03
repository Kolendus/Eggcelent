package model;

public class Shop {

    private String shopName;
    private int shopID;
    private int eggDemand;
    private int basicDemand;

    public Shop(String shopName, int shopID, int eggDemand) {
        this.shopName = shopName;
        this.shopID = shopID;
        this.eggDemand = eggDemand;
        this.basicDemand = eggDemand;
    }

    public String getShopName() {
        return shopName;
    }

    public int getShopID() {
        return shopID;
    }

    public void deliverEggs(int amount) {
        int actualEggs = eggDemand - amount;
            if(actualEggs > basicDemand){
                System.out.println("We try to deliver too many eggs to the shop");
            }
            this.eggDemand -= amount;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Shop shop = (Shop) o;

        if (shopID != shop.shopID) return false;
        if (eggDemand != shop.eggDemand) return false;
        return shopName.equals(shop.shopName);
    }


    public int eggMissing(){
        return eggDemand;
    }
    public void takeEggs(int amount){
        if(amount > basicDemand){
            System.out.println("We try to take too many eggs.");
        }
        this.eggDemand += amount;
    }

    public int getEggDemand() {
        return eggDemand;
    }


    @Override
    public String toString() {
        return shopID + " " + shopName + " " + eggDemand;
    }

}
