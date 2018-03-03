package loadPackageTests;

import load.LoadFile;
import model.Farm;
import model.Graph;
import model.Path;
import model.Shop;
import org.junit.Test;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.MatcherAssert.assertThat;


public class LoadFileTest {

    LoadFile load = new LoadFile();
    Graph graph;

    @Test
    public void scanFile() throws Exception {

        String path = "src\\resources\\eggData.txt";
        graph = load.ScanFile(path);

        assertThat(graph.getShopList(), containsInAnyOrder(new Shop("Żuk ", 0, 150),
                new Shop("Pszczółka ", 1, 230),
                new Shop("Jodłowiec ", 2, 400),
                new Shop("Iskierka – sklep rodzinny ", 3, 200)));


        assertThat(graph.getFarmList(), containsInAnyOrder(new Farm("Józef Żuczek Jaja rodzinne ", 0, 300),
                new Farm("Marian Kundera Moje kury ", 1, 330),
                new Farm("Łazarewicz i Spółka – Jaja od pokoleń ", 2, 500)));


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

    }


}