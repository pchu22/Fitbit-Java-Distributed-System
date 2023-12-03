import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface FitbitInterface extends Remote {
    void addRecord(double[] record) throws RemoteException;

    ArrayList<double[]> getRecords() throws RemoteException;
}
