package algorithmPackageTests;

import java.util.UUID;

import algorithm.Deliver;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class DeliverTest {
    Deliver deliveries = new Deliver();
    Random r = new Random();

    @Test
    public void addDelivery() {
        for (int i = 0; i < 1000; i++) {
            String farmName = UUID.randomUUID().toString();
            String shopName = UUID.randomUUID().toString();
            farmName = farmName.replace("-","");
            shopName = shopName.replace("-","");
            int eggs = r.nextInt();
            int eggCost = r.nextInt();
            Deliver newDelivery = new Deliver(farmName, shopName, eggs, eggCost);
            deliveries.addDelivery(farmName, shopName, eggs, eggCost);
            assertTrue(deliveries.getOrders().contains(newDelivery));
        }
    }

    @Test
    public void editOrder() {
        for (int i = 0; i < 1000; i++) {
            String farmName = UUID.randomUUID().toString();
            String shopName = UUID.randomUUID().toString();
            farmName = farmName.replace("-","");
            shopName = shopName.replace("-","");
            int eggs = r.nextInt();
            int eggCost = r.nextInt();
            Deliver newDelivery = new Deliver(farmName, shopName, eggs, eggCost);
            deliveries.addDelivery(farmName, shopName, eggs, eggCost);
            deliveries.editOrder(farmName,shopName,1000+i*3,eggCost);
            Deliver expectedDelivery = new Deliver(farmName, shopName, 1000+i*3, eggCost);
            assertTrue(deliveries.getOrders().get(i).equals(expectedDelivery));
        }
    }

}