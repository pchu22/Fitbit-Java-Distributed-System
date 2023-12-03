package Fitbit;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TCP {
    public static void main(String[] args) {
        Registry registry = null;

        try {
           registry = LocateRegistry.createRegistry(3030);
        } catch (RemoteException e){
            e.printStackTrace();
        }

        try {
            FitbitRecord record = new FitbitRecord();
            registry.rebind("Fitbit", record);
        } catch (Exception e){
            System.out.println("Server: " + e.getMessage());
        }
    }
}
