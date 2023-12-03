import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 3030);
            FitbitInterface fitbit = (FitbitInterface) registry.lookup("fitbit");

            double[] target = {11, 60, 18, 28};
            fitbit.addRecord(target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
