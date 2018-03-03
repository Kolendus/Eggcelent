package model;

import algorithm.Deliver;

import java.util.ArrayList;
import java.util.List;

public class Graph {

    private List<Shop> shopList;
    private List<Farm> farmList;
    private List<Path> roadList;
    private List<Shop> deletedShopList;
    private List<Farm> deletedFarmList;
    private List<Path> deletedRoadList;
    private boolean isReady = false;
    private Deliver deliveries = new Deliver();

    public Graph() {
        this.shopList = new ArrayList<>();
        this.farmList = new ArrayList<>();
        this.roadList = new ArrayList<>();
        this.deletedFarmList = new ArrayList<>();
        this.deletedRoadList = new ArrayList<>();
        this.deletedShopList = new ArrayList<>();
    }


    public void calculateLowestEggTransportCost() {
        while (!isReady) {
            isReady = optimize();
        }
        /*Jesli nie ma niepełnych sklepów*/
        if (getShopList().size() != 0) {
            addRestPaths(deletedRoadList);

            for(int i = 0; i< getShopList().size(); i++) {
                ArrayList availableFarmRoads = createListWithPathsFromFarm(getFarmList().get(i).getFarmID());
                fillShops(deletedRoadList, deletedShopList, deletedFarmList,availableFarmRoads, getShopList().get(i).getEggDemand() , i);
            }
        }

        return;
    }

    private boolean optimize() {

        Path shortestOne = findShortestPath();

        if (shortestOne == null || getFarmList() == null || getShopList() == null) {
            return true;
        } else {
            int roadMaximum = shortestOne.getRoadCapacity();

            int shopNeed = getShop(shortestOne.getShopID()).getEggDemand();
            int farmEggs = getFarm(shortestOne.getFarmID()).getEggProduction();
            int shopID = shortestOne.getShopID();
            int farmID = shortestOne.getFarmID();
            String shopName = getShop(shopID).getShopName();
            String farmName = getFarm(farmID).getFarmName();
            int eggCost = shortestOne.getEggCost();
            if (roadMaximum > shopNeed) { // sklep potrzebuje mniej niż droga oferuje
                if (shopNeed > farmEggs) { // przesylamy do sklepu tyle jajek ile farma pozwolila
                    fillFarmOrShopOrBoth(shopID,farmID,farmEggs,true,false,farmName,shopName,eggCost);
                } else if (shopNeed < farmEggs) {//przesylamu do sklepu tyle jajek ile mu potrzeba
                    fillFarmOrShopOrBoth(shopID,farmID,shopNeed,false,false,farmName,shopName,eggCost);
                } else if (shopNeed == farmEggs) {//przesylamy tyle ile potrzebuje sklep ,akurat tyle ma farma , polaczenie tez usuwamy
                    fillFarmOrShopOrBoth(shopID,farmID,shopNeed,false,true,farmName,shopName,eggCost);
                }
            } else if (shopNeed > roadMaximum) { // sklep potrzebuje wiecej niz droga oferuje
                if (roadMaximum > farmEggs) { // przesylamy tyle ile sie da z farmy
                    fillFarmOrShopOrBoth(shopID,farmID,farmEggs,true,false,farmName,shopName,eggCost);
                } else if (roadMaximum < farmEggs) { // przesylamy tyle ile droga pozwala
                 fillRoadCapacity(shopID,farmID,roadMaximum,farmName,shopName,eggCost);
                } else if (roadMaximum == farmEggs) {// przesylamy tyle ile droga pozwala a akurat tyle zostalo w fermie
                    fillFarmOrShopOrBoth(shopID,farmID,farmEggs,true,false,farmName,shopName,eggCost);
                }
            } else if (roadMaximum == shopNeed) {
                if (farmEggs < shopNeed) {//przesyłamy tyle ile się da z fermy
                   fillFarmOrShopOrBoth(shopID,farmID,farmEggs,true,false,farmName,shopName,eggCost);
                } else if (farmEggs > shopNeed) {//przesyłamy tyle ile sklep potrzebuje
                   fillFarmOrShopOrBoth(shopID,farmID,shopNeed,false,false,farmName,shopName,eggCost);
                } else {//przesylamy tyle ile potrzebuje sklep, tyle ma farma, tyle zapewnia droga
                   fillFarmOrShopOrBoth(shopID,farmID,shopNeed,false,true,farmName,shopName,eggCost);
                }
            }

        }
        return false;
    }

    private boolean fillShops(List<Path> deletedRoad, List<Shop> deletedShop, List<Farm> deletedFarm, List<Path> availableFarmRoads, int missingEggs, int j) {
        deletedRoadsWhichAreNotSufficient(missingEggs, availableFarmRoads);// w liscie drog dostepnych z fermy z jajkami mamy drogi tylko te gdzie mozemy dostarczyc x jajek
        deleteRoadFromFarm(getFarmList().get(j).getFarmID(), deletedRoad); // w liscie deletedRoad mamy tylko drogi z dwoch ferm pustych
        int shopNotFullID = getShopList().get(j).getShopID(); //sklep w ktorym brakuje jajek
        int alternativeShop = -1, whichFarm = -1, farmAccepted = -1;
        Path checkPath = null;
        int x = 0;
        for (int i = 0; i < availableFarmRoads.size(); i++) {
            checkPath = availableFarmRoads.get(i);
            //sklep w ktorym brakuje jajek
            alternativeShop = checkPath.getShopID(); // id sklepu do ktorego wyslemy alternatywnie
            whichFarm = checkPath.getFarmID(); // id fermy w ktorej zostaly jajka

            farmAccepted = lookForAlternativePath(alternativeShop, shopNotFullID, deletedRoad, missingEggs);

            if (farmAccepted != -1) {
                x = i;
                break;
            }
        }
        getShop(alternativeShop, deletedShop).takeEggs(missingEggs);// zabieram z niego 50 jajek

        getFarm(farmAccepted, deletedFarm).giveEggs(missingEggs);//daje jajka dla fermy
        Path alternativePath = getSpecificPath(farmAccepted, alternativeShop, deletedRoad);
        getSpecificPath(farmAccepted, alternativeShop, deletedRoad).reDeliver(missingEggs);
        alternativePath.reDeliver(missingEggs);//poprawiam przepływ drogi która zabieram jajka ze sklepu
        Path sendMissingOnes = getSpecificPath(farmAccepted, shopNotFullID, deletedRoad);// z alternatywnej fermy uzupelniamy brakujacy sklep
        availableFarmRoads.get(x).deliveryFlow(missingEggs);
        checkPath.deliveryFlow(missingEggs); // z sciezki wybranej uzupelniamy sklep z ktorego zabralismy
        getSpecificPath(farmAccepted, shopNotFullID, deletedRoad).deliveryFlow(missingEggs);
        sendMissingOnes.deliveryFlow(missingEggs);// wysylam sciezka brakujace jajka
        getShop(shopNotFullID, getShopList()).deliverEggs(missingEggs); // dodaje je do sklepu

        getShop(alternativeShop, deletedShop).deliverEggs(missingEggs);//dodaje je do alternatynwego sklepu
        if (getShop(shopNotFullID, getShopList()).getEggDemand() == 0 && getShop(alternativeShop, deletedShop).getEggDemand() == 0) {

            deliveries.editOrder(getFarm(farmAccepted, deletedFarm).getFarmName(), getShop(alternativeShop, deletedShop).getShopName(), missingEggs, getSpecificPath(farmAccepted, alternativeShop, deletedRoad).getEggCost());

            deliveries.addDelivery(getFarm(checkPath.getFarmID(), getFarmList()).getFarmName(), getShop(checkPath.getShopID(), deletedShop).getShopName(),
                    missingEggs, checkPath.getEggCost());
            deliveries.addDelivery(getFarm(sendMissingOnes.getFarmID(), deletedFarm).getFarmName(), getShop(sendMissingOnes.getShopID(), getShopList()).getShopName()
                    , missingEggs, sendMissingOnes.getEggCost());
            removeShop(shopNotFullID);//sklep juz zapełniony
            return true;
        }

        return false;
    }


    private Path checkIfWeCanSendSpecificAmountOfEggsToMissingShop(int farmID, int xEggs, int shopNotFullID, List<Path> deletedRoad) {
        for (int i = 0; i < deletedRoad.size(); i++) {
            Path suspectPath = deletedRoad.get(i);
            //czy jest polaczenie z farmy na razie pustej do sklepu niepełnego gdzie mozemy przeslac x jajek
            if ((suspectPath.getFarmID() == farmID) && (suspectPath.getShopID() == shopNotFullID) && (suspectPath.checkEggFlow(xEggs))) {
                return suspectPath;
            }
        }
        return null;
    }
    private Path findShortestPath() {

        int nShops = getShopList().size();
        if (nShops == 0) {
            return null;
        }
        int nFarms = getFarmList().size();
        Path[] shortestPaths = new Path[nShops]; //tablica najkrotszych sciezek
        for (int i = 0; i < nShops; i++) {
            int nShopID = getShopList().get(i).getShopID(); //id sklepu

            Path shortest = null;

            for (int j = 0; j <= nFarms + nShops; j++) {
                Path suspectedPath = getSpecificPath(j, nShopID); // iteracja po kolejnych drogach do konkretnego sklepu

                if (suspectedPath == null) {
                    ; // w przypadku gry juz nie istnieje taka sciezka
                } else if (suspectedPath.compareTo(shortest) > 0) {
                    shortest = suspectedPath;
                }
            }
            shortestPaths[i] = shortest;
        }

        Path shortestOne = shortestPaths[0];

        for (int i = 1; i < shortestPaths.length; i++) {
            if (shortestOne == null) {
                return null;
            } else if (shortestOne.compareTo(shortestPaths[i]) < 0) {
                shortestOne = shortestPaths[i];
            }
        }
        return shortestOne;
    }
    private int lookForAlternativePath(int alternativeShopID, int shopNotFullID, List<Path> deletedRoad, int xEggs) {//wybieramy sciezke ktora przeslalismy do sklepu x >= jajek
        for (int i = 0; i < deletedRoad.size(); i++) {
            Path suspectPath = deletedRoad.get(i);
            if (suspectPath.getShopID() == alternativeShopID && suspectPath.checkEggFlow(xEggs)) {// czy do alternatywnego sklepu i czy mozemy "Zabrac" x  jajek

                Path theOneToFillTheGap = checkIfWeCanSendSpecificAmountOfEggsToMissingShop(suspectPath.getFarmID(), xEggs, shopNotFullID, deletedRoad);
                if (theOneToFillTheGap != null) {
                    return theOneToFillTheGap.getFarmID();
                }
            }
        }

        return -1;
    }
    private void addRestPaths(List x) {
        for (int i = 0; i < getRoadList().size(); i++) {
            x.add(getRoadList().get(i));
        }
    }


    public Path getSpecificPath(int farmID, int shopID) {
        for (int i = 0; i < roadList.size(); i++) {
            Path suspectPath = roadList.get(i);
            if ((suspectPath.getShopID() == shopID) && (suspectPath.getFarmID() == farmID)) {
                return roadList.get(i);
            }
        }
        return null;
    }
    private Path getSpecificPath(int from, int to, List<Path> roads) {
        for (int i = 0; i < roads.size(); i++) {
            Path suspectPath = roads.get(i);
            if (suspectPath.getFarmID() == from && suspectPath.getShopID() == to) {
                return roads.get(i);
            }
        }
        return null;
    }

    private void deleteRoadFromFarm(int farmID, List<Path> deletedRoadList) {

        for (int j = 0; j < deletedRoadList.size(); j++) {
            for (int i = 0; i < deletedRoadList.size(); i++) {
                Path suspectPath = deletedRoadList.get(i);
                if (suspectPath.getFarmID() == farmID) {
                    deletedRoadList.remove(suspectPath);
                }
            }
        }
    }
    private void deletedRoadsWhichAreNotSufficient(int eggsNeed, List<Path> availableFarmRoads) {
        for (int i = 0; i < availableFarmRoads.size(); i++) {
            if (availableFarmRoads.get(i).getRoadCapacity() < eggsNeed) {
                availableFarmRoads.remove(i);
            }
        }
    }

    private ArrayList createListWithPathsFromFarm(int farmID) {
        newRoads(this.deletedRoadList);
        ArrayList<Path> specificFarmRoads = new ArrayList<>();
        for (int i = 0; i < this.deletedShopList.size() + getShopList().size(); i++) {
            Path suspectPath = getSpecificPath(farmID, i);
            if (suspectPath != null) {
                specificFarmRoads.add(suspectPath);
            }
        }
        return specificFarmRoads;
    }
    public void newRoads(List newRoads) {
        this.roadList = newRoads;
    }

    public void removeShop(int id) {
        shopList.remove(getShop(id));
    }
    public void removeFarm(int id) {
        farmList.remove(getFarm(id));
    }
    public void removePath(int from, int to) {
        Path suspected = getSpecificPath(from, to);
        if (suspected != null) {
            roadList.remove(getSpecificPath(from, to));
        }
    }
    public void removePathConnectedTo(int to, boolean ifFarm) {
        if (ifFarm) {
            for (int i = 0; i <= shopList.size() +farmList.size(); i++) {
                removePath(to, i);
            }
        } else {
            for (int i = 0; i <= farmList.size() + shopList.size(); i++) {
                removePath(i, to);
            }
        }
    }


    public void addPathConnectedTo(int to, boolean ifFarm, List x) {
        if (ifFarm) {
            for (int i = 0; i <= shopList.size(); i++) {
                addPath(to, i, x);
            }
        } else {
            for (int i = 0; i <= farmList.size(); i++) {
                addPath(i, to, x);
            }
        }
    }
    public void addPath(int from, int to, List x) {
        Path suspected = getSpecificPath(from, to);
        if (suspected != null) {
            x.add(suspected);
        }
    }

    public Shop getShop(int shopID, List<Shop> x) {
        for (int i = 0; i < x.size(); i++) {
            Shop suspect = x.get(i);
            if (suspect.getShopID() == shopID) {
                return x.get(i);
            }
        }
        return null;
    }
    public Farm getFarm(int farmID, List<Farm> x) {
        for (int i = 0; i < x.size(); i++) {
            Farm suspect = x.get(i);
            if (suspect.getFarmID() == farmID) {
                return x.get(i);
            }
        }
        return null;
    }
    public Shop getShop(int shopID) {
        for (int i = 0; i < shopList.size(); i++) {
            Shop suspect = shopList.get(i);
            if (suspect.getShopID() == shopID) {
                return shopList.get(i);
            }
        }
        return null;
    }
    public Farm getFarm(int farmID) {
        for (int i = 0; i < farmList.size(); i++) {
            Farm suspect = farmList.get(i);
            if (suspect.getFarmID() == farmID) {
                return farmList.get(i);
            }
        }
        return null;
    }

    public List<Shop> getShopList() {
        return shopList;
    }
    public List<Farm> getFarmList() {
        return farmList;
    }
    public List<Path> getRoadList() {
        return roadList;
    }

    private void fillFarmOrShopOrBoth(int shopID,int farmID, int eggs, boolean ifFarm,boolean ifAll, String farmName, String shopName, int eggCost){
        getShop(shopID).deliverEggs(eggs);
        getFarm(farmID).takeEggs(eggs);
        deliveries.addDelivery(farmName, shopName, eggs, eggCost);

        getSpecificPath(farmID, shopID).deliveryFlow(eggs);//przeplyw drogi zmniejszony
        if(ifAll){
            deletedShopList.add(getShop(shopID));//dodajemy usuwany sklep
            addPathConnectedTo(shopID, false, deletedRoadList);///dodajemy usuwane połączenia
            deletedFarmList.add(getFarm(farmID));//dodajemy usuwana ferme
            addPathConnectedTo(farmID, true, deletedRoadList);///dodajemy usuwane połączenia
            removePathConnectedTo(farmID, true);
            removePathConnectedTo(shopID, false);
            removeFarm(farmID);
            removeShop(shopID);
            return;
        }
        if(ifFarm) {
            deletedFarmList.add(getFarm(farmID)); //dodaje usuwaną fermę
            addPathConnectedTo(farmID, ifFarm, deletedRoadList);//dodajemy do usunietych drog
            removePathConnectedTo(farmID, ifFarm);
            removeFarm(farmID);
        }else{
            deletedShopList.add(getShop(shopID));//dodajemy usuwany sklep
            addPathConnectedTo(shopID, ifFarm, deletedRoadList);///dodajemy usuwane połączenia
            removePathConnectedTo(shopID, ifFarm);
            removeShop(shopID);
        }
    }
    private void fillRoadCapacity(int shopID,int farmID,int roadMaximum,String farmName,String shopName, int eggCost){
        getShop(shopID).deliverEggs(roadMaximum);//przesylamy tyle jajek ile droga pozwala
        getFarm(farmID).takeEggs(roadMaximum);
        getSpecificPath(farmID, shopID).deliveryFlow(roadMaximum);

        deliveries.addDelivery(farmName, shopName, roadMaximum, eggCost);
        deletedRoadList.add(getSpecificPath(farmID, shopID));

        removePath(farmID, shopID);
    }

    public void printTotalEggs() {
        deliveries.printTotalEggs();
    }
    public void printResult() {
        deliveries.printDeliveries();
    }
}
