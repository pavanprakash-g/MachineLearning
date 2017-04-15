import java.util.ArrayList;

/**
 * Created by pavan on 4/14/17.
 */
public class Main {

    public static void main(String[] args){
        try {
            int k = Integer.parseInt(args[0]);
            String inputPath = args[1];
            String outputPath = args[2];
            KMeans kMeans = new KMeans();
            kMeans.getData(inputPath);
            kMeans.generateRandomCenters(k);
            kMeans.applyKMeans();
            float SSE = kMeans.calculateSSE();
            kMeans.writeOutput(outputPath, SSE);
            System.out.println();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
