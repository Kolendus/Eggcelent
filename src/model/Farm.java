package model;

public class Farm {
    private String farmName;
    private int farmID;
    private int eggProduction;
    private int basicProduction;
    public Farm(String farmName, int farmID, int eggProduction) {
        this.farmName = farmName;
        this.farmID = farmID;
        this.eggProduction = eggProduction;
        basicProduction = eggProduction;
    }

    public String getFarmName() {
        return farmName;
    }

    public int getFarmID() {
        return farmID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Farm farm = (Farm) o;

        if (farmID != farm.farmID) return false;
        if (eggProduction != farm.eggProduction) return false;
        return farmName.equals(farm.farmName);
    }



    public int getEggProduction() {
        return eggProduction;
    }

    public void takeEggs(int amount) {
        int eggsLeft = this.eggProduction - amount;

        if (eggsLeft < 0) {
            System.out.println("Farm does not have that many eggs to give.");
            return ;
        } else
            this.eggProduction = eggsLeft;
    }

    public void giveEggs(int amount){
        int eggsAfter = this.eggProduction+amount;
        if(eggsAfter > basicProduction){
            System.out.println("Farm produces less eggs,there is an error");
            return;
        }
        this.eggProduction += amount;
        return;
    }

    @Override
    public String toString() {
        return farmID + " " + farmName + " " + eggProduction;
    }
}
