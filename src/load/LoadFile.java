package load;

import model.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LoadFile implements INLoadFile {

    private CheckFile checkFile;

    public LoadFile() {
        checkFile = new CheckFile();
    }

    @Override
    public Graph ScanFile(String path) throws Exception {

        Graph graf = new Graph();

       if (!checkFile.checkFile(path)) {
            return null;
        }

        String row;
        boolean farms, shops, roads;
        farms = shops = roads = false;

        try {
            FileReader data = new FileReader(path);
            BufferedReader dataReader = new BufferedReader(data);

            while ((row = dataReader.readLine()) != null) {
                String[] line = row.split(" ");
                if (line[1].contains("erm")) {
                    farms = true;
                    continue;
                } else if (line[1].contains("klep")) {
                    shops = true;
                    farms = false;
                    continue;
                } else if (line[1].contains("czeni")) {
                    roads = true;
                    shops = false;
                    continue;
                }
                if (farms || shops) {

                    StringBuilder sb = new StringBuilder();
                    String name = "";
                    int id = Integer.parseInt(line[0]);
                    int production_demand = Integer.parseInt(line[line.length - 1]);
                    for (int i = 1; i < line.length - 1; i++) {
                        name = sb.append(line[i]).append(" ").toString();
                    }
                    if (shops) {
                        Shop eggShop = new Shop(name, id, production_demand);
                        graf.getShopList().add(eggShop);
                    } else {
                        Farm eggFarm = new Farm(name, id, production_demand);
                        graf.getFarmList().add(eggFarm);
                    }
                } else if (roads) {
                    int fromFarm, toShop, dailyMaxEggs, eggTransportCost;
                    fromFarm = Integer.parseInt(line[0]);
                    toShop = Integer.parseInt(line[1]);
                    dailyMaxEggs = Integer.parseInt(line[2]);
                    eggTransportCost = Integer.parseInt(line[3]);
                    Path road = new Path(toShop, fromFarm, dailyMaxEggs, eggTransportCost);
                    graf.getRoadList().add(road);
                }

            }

        } catch (FileNotFoundException e) {
            System.out.println("File does not exists in specified location.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return graf;
    }

}


