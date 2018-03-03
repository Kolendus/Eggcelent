package load;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class CheckFile implements INCheckFile {
    private int[][] edge = new int[1000][1000];
    private int farmNumber, shopNumber;
    private boolean farms, shops, roads;

    @Override
    public boolean checkFile(String path) throws Exception {

        List<String> valid;
        List<String> futureValid = null;
        if (!path.contains("txt")) {
            System.out.println("File must be .txt type");
            return false;
        }
        try {
            BufferedReader dataReader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(path), "UTF8"));
            BufferedReader oneLineFurtherReader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(path), "UTF8"));


            String row, nextRow;

            int lineNumber, shopNumber, roadsNumber, farmNumber;
            lineNumber = shopNumber = roadsNumber = farmNumber = 0;

            this.farms = this.shops = this.roads = false;

            boolean titleLine = true;

            nextRow = oneLineFurtherReader.readLine();

            while ((row = dataReader.readLine()) != null) {
                lineNumber++;


                String[] line = row.split(" ");
                makeThemBig(line);
                valid = Arrays.asList(line);
                checkIfLineIsEmpty(line, lineNumber);

                String[] nextLine = null;

                if (closeReaderIfNecessary(nextRow)) {
                    oneLineFurtherReader.close();
                } else {
                    nextRow = oneLineFurtherReader.readLine();
                    if (nextRow != null) {
                        nextLine = nextRow.split(" ");
                        makeThemBig(nextLine);
                        futureValid = Arrays.asList(nextLine);
                    }
                }


                titleLineCheck(titleLine, valid, lineNumber);


                if (line[1].contains("ERM")) {
                    this.farms = true;
                    titleLine = false;
                    continue;
                } else if (line[1].contains("KLEP")) {
                    this.shops = true;
                    this.farms = false;
                    titleLine = false;
                    continue;
                } else if (line[1].contains("OŁĄCZENI") || line[1].contains("OLACZENI")) {
                    this.roads = true;
                    this.shops = false;
                    titleLine = false;
                    continue;
                }


                int[] values = farmsShopsInsert(this.farms, this.shops, farmNumber, shopNumber, line, lineNumber);

                farmNumber = values[0];
                shopNumber = values[1];

                if (checkRoads(roads, line, lineNumber, shopNumber, farmNumber)) {
                    roadsNumber++;
                }

                titleLine = checkIfNextLineIsTitleLine(nextRow, futureValid, nextLine);

            }
            this.farmNumber = farmNumber;
            this.shopNumber = shopNumber;
            checkIfThereAreAllConnections(roadsNumber, shopNumber, farmNumber);
            dataReader.close();
            oneLineFurtherReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File does not exists in specified location.\n" + path);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Insufficient access rights for the file given.");
            e.printStackTrace();
        }

        return checkConnections(this.farmNumber, this.shopNumber, edge);
    }

    public boolean isInteger(String string) {
        try {
            Integer.valueOf(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isNegative(String string) {
        int x = Integer.parseInt(string);
        return x < 0;

    }

    public boolean checkConnections(int nFarms, int nShops, int[][] edge) throws Exception {
        boolean isRight = false;
        for (int i = 0; i < nFarms; i++) {
            for (int j = 0; j < nShops; j++) {

                if (edge[i][j] == 1) {
                    isRight = true;
                } else if (edge[i][j] != 1) {
                    throw new Exception("Missing connection from farm number = " + i + " to shop number = " + j);
                }
            }
        }

        return isRight;
    }

    public void titleLineCheck(boolean titleLine, List<String> valid, int lineNumber) throws Exception {

        if (titleLine) {
            if (!valid.contains("#".toString())) {
                throw new Exception("Missing '#' in the beggining " +
                        " of the specified data in line number: "
                        + lineNumber);
            } else if (!(valid.contains("FERMY") || valid.contains("FERM"))
                    && !(valid.contains("SKLEPY") || valid.contains("SKLEP"))
                    && !(valid.contains("POŁĄCZENIA") || valid.contains("POLACZENIA"))) {

                throw new Exception("Missing description of dataset in line " + lineNumber + ".");

            }
        }

    }

    public int[] farmsShopsInsert(boolean farms, boolean shops, int farmNumber, int shopNumber, String[] line, int lineNumber) throws Exception {

        if (farms || shops) {
            if (farms) {
                farmNumber++;
            } else {
                shopNumber++;
            }
            if (line.length < 3) {
                throw new Exception("Not enough data in line number = " + lineNumber);
            } else if (!isInteger(line[0]) || isNegative(line[0])) {  //Czy jest liczba ujemna lub nie jest parsowalna na inta.
                throw new Exception("Wrong type of argument at the begginning, when declaring data in line = " + lineNumber +
                        ".\nArgument '" + line[0] + "' is wrong.");
            } else if (!isInteger(line[line.length - 1]) || isNegative(line[line.length - 1])) {
                throw new Exception("Wrong type of argument at the end, when declaring data in line: " + lineNumber +
                        ".\nArgument '" + line[line.length - 1] + "' is wrong.");
            }
        }

        int[] values = new int[2];
        values[0] = farmNumber;
        values[1] = shopNumber;
        return values;
    }

    public boolean checkRoads(boolean roads, String[] line, int lineNumber, int shopNumber, int farmNumber) throws Exception {
        if (roads) {
            if (line.length < 4) {
                throw new Exception("Not enough data in line number: " + lineNumber);

            }
            for (int i = 0; i < 4; i++) {
                if (!isInteger(line[i])) {
                    throw new Exception("Wrong type of argument at the begginning when declaring data in line = " + lineNumber +
                            ". Argument number = " + (i + 1) + ".\nArgument '" + line[i] + "' is wrong.");

                }
                if (isNegative(line[i])) {
                    throw new Exception("Illegal data in line = " + lineNumber + " argument number = " + (i + 1) + "\n You cannot have negative data, this is illogical with" +
                            "the program " + ".\nArgument '" + line[i] + "' is wrong.");

                }
                if (i == 1) {
                    if (Integer.parseInt(line[i]) > shopNumber - 1) {
                        throw new Exception("You can not have a road from shop which does not exist.\n" + "Argument number = " + (i + 1) + " in line number = " + lineNumber + ".");
                    }
                }
                if (i == 0) {
                    if (Integer.parseInt(line[i]) > farmNumber - 1) {
                        throw new Exception("You can not have a road from farm which does not exist.\n" + "Argument number = " + (i + 1) + " in line number = " + lineNumber + ".");
                    }
                }
            }
            int farmID = Integer.parseInt(line[0]);
            int shopID = Integer.parseInt(line[1]);
            insertConnections(farmID, shopID);
            return true;
        }

        return false;
    }

    public boolean checkIfNextLineIsTitleLine(String nextRow, List<String> futureValid, String[] nextLine) {

        if (nextRow != null) {
            if (futureValid == null || nextLine == null || futureValid.size() == 1) {
                return false;
            } else if (futureValid.contains("KLEP") || futureValid.contains("ERM") || futureValid.contains("CZENIA") || futureValid.contains("NAZWA") || futureValid.contains("|")) {
                return true;
            }
        }
        return false;

    }

    public void checkIfThereAreAllConnections(int roadsNumber, int shopNumber, int farmNumber) throws Exception {
        if (roadsNumber != (shopNumber * farmNumber)) {
            throw new Exception("Not enough connections between farms and shops. Should be : " +
                    shopNumber * farmNumber + " but is " + roadsNumber + ".\n You " +
                    "are missing some connections between certain shops and suppliers," +
                    "please check your data file");
        }
    }

    public void checkIfLineIsEmpty(String[] line, int lineNumber) throws Exception {
        if (line == null || line.length == 1) {
            throw new Exception("You cannot have an empty line in line number = " + lineNumber);
        }
    }

    public boolean closeReaderIfNecessary(String nextRow) {
        if (nextRow != null) return false;
        else {
            return true;
        }
    }

    private void makeThemBig(String[] line) {
        for (int i = 0; i < line.length; i++) {
            line[i] = line[i].toUpperCase();
        }
    }

    public void insertConnections(int fermID, int shopID) {
        this.edge[fermID][shopID] = 1;
    }

    public int[][] getEdge() {
        return edge;
    }


}
