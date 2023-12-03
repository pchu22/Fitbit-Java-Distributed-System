package Fitbit;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class FitbitRecord extends UnicastRemoteObject implements FitbitInterface {
    public static ArrayList<double[]> records = new ArrayList<>();

    public FitbitRecord() throws RemoteException{}

    @Override
    public void addRecord(double[] _record) throws RemoteException{
        records.add(_record);
    }

    public ArrayList<double[]> getRecords() throws RemoteException{
        return records;
    }
}