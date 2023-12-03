package Records;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TCP {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(3030);
            FitbitInterface record = new FitbitRecord();
            registry.rebind("Records", record);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
