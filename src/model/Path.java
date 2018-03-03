package model;

public class Path {
    private int shopID;
    private int farmID;
    private int roadCapacity;
    private int eggCost;
    private int eggFlow = 0;


    public Path(int shopID, int farmID, int roadCapacity, int eggCost) {
        this.shopID = shopID;
        this.farmID = farmID;
        this.eggFlow = roadCapacity;
        this.roadCapacity = roadCapacity;
        this.eggCost = eggCost;
    }


    public int getShopID() {
        return shopID;
    }

    public int getFarmID() {
        return farmID;
    }

    public int getRoadCapacity() {
        return roadCapacity;
    }

    public int getEggCost() {
        return eggCost;
    }


    public int compareTo(Path x) {
        if (x == null) {
            return 1;
        }
        if (this.getEggCost() < x.getEggCost()) {
            return 1;
        } else if (this.getEggCost() > x.getEggCost()) {
            return -1;
        }

        if (this.getRoadCapacity() > x.getRoadCapacity()) {
            return 1;
        } else if (this.getRoadCapacity() < x.getRoadCapacity()) {
            return -1;
        }
        return 1;

    }



    @Override
    public String toString() {
        return farmID + " " + shopID + " " + roadCapacity + " " + eggCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Path path = (Path) o;

        if (shopID != path.shopID) return false;
        if (farmID != path.farmID) return false;
        if (roadCapacity != path.roadCapacity) return false;
        return eggCost == path.eggCost;
    }

    public void deliveryFlow(int amount) {
        this.roadCapacity -= amount;
        this.eggFlow += amount;
    }

    public boolean checkEggFlow(int missingEggs){
        if(missingEggs <= this.eggFlow){
            return true;
        }
        return false;
    }
    public void reDeliver(int amount) {
        if (amount > roadCapacity) {
            System.out.println("You are trying to redeliver too many eggs");
        }
        this.roadCapacity += amount;
    }
}
