import load.LoadFile;
import model.Graph;

public class Main {

    public static void main(String[] args)  {
      /*if(args.length < 1){
            System.out.println("Prosze podać argument wejściowy będący ścieżka do pliku z danymi\n-----------------------------------------------");
            System.out.println("Przykładowe wywołanie programu: ");
            System.out.println("java -jar <nazwa_programu__w_jar> <nazwa_pliku_z_danymi>");
            return;
        }
        if(args.length > 1){
            System.out.println("Za dużo argumentów wejściowych, prosze podać jeden plik!");
            return;
        }*/
      String path = "C:\\Users\\Michał\\IdeaProjects\\Eggcelent\\src\\load\\eggData.txt";
        LoadFile loader = new LoadFile();
        try {
            Graph solver = loader.ScanFile(path);
            solver.calculateLowestEggTransportCost();
            solver.printResult();
            solver.printTotalEggs();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }
}
