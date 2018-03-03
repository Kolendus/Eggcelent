package loadPackageTests;

import load.CheckFile;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class CheckFileTest {

    CheckFile test = new CheckFile();
    Random r = new Random();

    @Test
    public void checkFile() throws Exception {
        String path = "src\\resources\\eggData.txt";
        try {
            Assert.assertTrue(test.checkFile(path));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void isInteger() throws Exception {
        for (int i = 0; i < 1000; i++) {
            Assert.assertTrue(test.isInteger(r.nextInt() + ""));
        }
    }

    @Test
    public void isNegative() throws Exception {
        for (int i = 0; i < 1000; i++) {
            int x = Math.abs(r.nextInt());
            Assert.assertTrue(test.isNegative(("-" + x)));
        }
    }

    @Test
    public void checkConnections() throws Exception {
        int nFarms = 0;
        int nShops = 0;
        for(int i = 0; i < 1000; i++) {
             nFarms = r.nextInt(20) + 1;
             nShops = r.nextInt(20) + 1;
            insertConnections(test.getEdge(), nFarms, nShops);
            Assert.assertTrue(test.checkConnections(nFarms, nShops, test.getEdge()));
        }

        try {
            for(int i = 0; i < 1000; i++) {
                nFarms = r.nextInt(3) + 1;
                 nShops = r.nextInt(2) + 1;
                int x = nFarms - 1;
                CheckFile test2 = new CheckFile();
                insertConnections(test2.getEdge(), x , nShops);
                test.checkConnections(nFarms,nShops,test2.getEdge());
            }
            fail("Expected an Exception to be thrown");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Missing connection from farm number = "+(nFarms - 1)+" to shop number = 0"));
        }

    }

    @Test
    public void titleLineCheck() throws Exception {
        try {
            String[] line = {"#", "asdfa", "asdfa"};
            List<String> valid = Arrays.asList(line);
            test.titleLineCheck(true, valid, 1);
            fail("Expected an Exception to be thrown");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Missing description of dataset in line 1."));
        }
        try {
            String[] line = {"", "Fermy", "asdfa"};
            List<String> valid = Arrays.asList(line);
            test.titleLineCheck(true, valid, 1);
            fail("Expected an Exception to be thrown");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Missing '#' in the beggining  of the specified data in line number: 1"));
        }
    }

    @Test
    public void farmsShopsInsert() throws Exception {
        try {
            String[] line = {"0", "Żuczek", "-300"};
            test.farmsShopsInsert(true, false, 0, 0, line, 1);
            fail("Expected an Exception to be thrown");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Wrong type of argument at the end, when declaring data in line: 1.\nArgument '-300' is wrong."));
        }

        try {
            String[] line = {"adfa", "12314", "300"};
            test.farmsShopsInsert(true, false, 0, 0, line, 1);
            fail("Expected an Exception to be thrown");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Wrong type of argument at the begginning, when declaring data in line = 1.\nArgument 'adfa' is wrong."));
        }

        try {
            String[] line = {"0", "300"};
            test.farmsShopsInsert(true, false, 0, 0, line, 1);
            fail("Expected an Exception to be thrown");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Not enough data in line number = 1"));
        }

    }

    @Test
    public void checkRoads() throws Exception {
        try {
            String[] line = {"0", "1", "200"};
            test.checkRoads(true, line, 1, 1, 1);
            fail("Expected an Exception to be thrown");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Not enough data in line number: " + 1));
        }

        try {
            String[] line = {"a", "1", "200", "4"};
            test.checkRoads(true, line, 1, 1, 1);
            fail("Expected an Exception to be thrown");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Wrong type of argument at the begginning when declaring data in line = " + 1 +
                    ". Argument number = " + (1) + ".\nArgument '" + "a" + "' is wrong."));
        }

        try {
            String[] line = {"0", "-1", "200", "4"};
            test.checkRoads(true, line, 1, 1, 1);
            fail("Expected an Exception to be thrown");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Illegal data in line = 1 argument number = 2\n You cannot have negative data, this is illogical withthe program .\nArgument '-1' is wrong."));
        }

        try {
            String[] line = {"4", "1", "200", "4"};
            test.checkRoads(true, line, 1, 4, 3);
            fail("Expected an Exception to be thrown");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("You can not have a road from farm which does not exist.\nArgument number = 1 in line number = 1."));
        }

    }

    @Test
    public void checkIfNextLineIsTitleLine() throws Exception {
        String nextRow = "# Fermy drobiu (id | nazwa | dzienna produkcja)";
        nextRow = nextRow.toUpperCase();
        String[] nextLine = nextRow.split(" ");
        List<String> futureValid = Arrays.asList(nextLine);
        Assert.assertTrue(test.checkIfNextLineIsTitleLine(nextRow, futureValid, nextLine));
        String nextRow2 = " em drobiu (id   dzienna produkcja";
        nextRow2 = nextRow2.toUpperCase();
        String[] nextLine2 = nextRow2.split(" ");
        List<String> futureValid2 = Arrays.asList(nextLine2);
        Assert.assertFalse(test.checkIfNextLineIsTitleLine(nextRow2, futureValid2, nextLine2));

    }

    @Test
    public void checkIfThereAreAllConnections() throws Exception {
        try {
            test.checkIfThereAreAllConnections(12, 3, 3);
            fail("Expected an Exception to be thrown");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Not enough connections between farms and shops. Should be : 9 but is 12.\n " +
                    "You are missing some connections between certain shops and suppliers,please check your data file"));
        }
    }

    @Test
    public void checkIfLineIsEmpty() throws Exception {
        try {
            String line[] = null;
            String line2[] = {""};
            test.checkIfLineIsEmpty(line, 1);
            test.checkIfLineIsEmpty(line2, 1);
            fail("Expected an Exception to be thrown");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("You cannot have an empty line in line number = " + 1));
        }
    }

    private void insertConnections(int[][] edge, int nFarms, int nShops) {
        for (int i = 0; i < nFarms; i++) {
            for (int j = 0; j < nShops; j++) {
                edge[i][j] = 1;
            }
        }
    }

}