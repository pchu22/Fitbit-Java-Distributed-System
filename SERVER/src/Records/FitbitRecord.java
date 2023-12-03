package Records;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class FitbitRecord extends UnicastRemoteObject implements FitbitInterface {
    private static final ArrayList<double[]> records = new ArrayList<>();

    public FitbitRecord() throws RemoteException {}

    @Override
    public void addRecord(double[] record) throws RemoteException {
        records.add(record);
    }

    public ArrayList<double[]> getRecords() throws RemoteException {
        return new ArrayList<>(records); // Returns a copy to prevent direct modification
    }
}
