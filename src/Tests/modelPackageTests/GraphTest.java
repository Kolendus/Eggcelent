package modelPackageTests;

import load.LoadFile;
import model.Graph;
import model.Path;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

public class GraphTest {


    Graph graph = new Graph();
    Random r = new Random();

    @Test
    public void getSpecificPath() throws Exception {
        for (int i = 0; i < 1000; i++) {
            int farmID = r.nextInt();
            int shopID = r.nextInt();
            Path expected = new Path(shopID, farmID, Math.abs(farmID - shopID), 3);
            graph.getRoadList().add(expected);
            graph.getRoadList().add(new Path(farmID, shopID, Math.abs(farmID + 670), 4));
            graph.getRoadList().add(new Path(farmID, shopID, Math.abs(farmID + 670), 7));
            graph.getRoadList().add(new Path(farmID*3, shopID/2, Math.abs(farmID - 623470), 8));
            Assert.assertEquals(graph.getSpecificPath(farmID, shopID), expected);
        }
    }

    @Test
    public void removePathConnectedTo() throws Exception {
        String path = "src\\resources\\eggData.txt";
        LoadFile load = new LoadFile();
        graph = load.ScanFile(path);
        assertThat(graph.getRoadList(), containsInAnyOrder(new Path(0, 0, 200, 3),
                new Path(1, 0, 200, 3),
                new Path(2, 0, 250, 2),
                new Path(3, 0, 100, 4),
                new Path(0, 1, 300, 3),
                new Path(1, 1, 200, 4),
                new Path(2, 1, 150, 5),
                new Path(3, 1, 300, 2),
                new Path(0, 2, 300, 4),
                new Path(1, 2, 300, 4),
                new Path(2, 2, 100, 6),
                new Path(3, 2, 200, 5)));
        graph.removePathConnectedTo(0,true);
        assertThat(graph.getRoadList(), containsInAnyOrder(new Path(0, 1, 300, 3),
                new Path(1, 1, 200, 4),
                new Path(2, 1, 150, 5),
                new Path(3, 1, 300, 2),
                new Path(0, 2, 300, 4),
                new Path(1, 2, 300, 4),
                new Path(2, 2, 100, 6),
                new Path(3, 2, 200, 5)));

    }


}