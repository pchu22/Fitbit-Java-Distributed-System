package KNN;

import Unicast.HeartbeatSender;

import java.util.List;

public class Knn extends Thread {

    private static double[][] dataset;
    private static int method;
    private static String role;
    private static String[] requests;

    public Knn(double[][] _dataset, int _method, String _role, String[] _requests) {
        this.dataset = _dataset;
        this.method = _method;
        this.role = _role;
        this.requests = _requests;

    }

    public static void main(String[] args) {
        Knn knn = new Knn(dataset, method, role, requests);
        knn.runKnn();
    }

    public void runKnn(){
        boolean hasNeighborWithinThreshold = false;
        int k = 3;
        for (String request : requests) {
            double[] data = getDouble(request);
            String ID = getID(request);

            List<Double> nearestNeighbours = KnnAlgorithm.findNearestNeighbours(data, dataset, k, method);
            hasNeighborWithinThreshold = nearestNeighbours.stream().anyMatch(element -> element <= 5.0);
            HeartbeatSender.main(new String[]{ID, (hasNeighborWithinThreshold ? "1" : "0") + ";" + role});

        }
    }
    public static String getID(String request){
        String[] data = request.split("//");
        return data[0];
    }

    public static double[] getDouble(String request){
        String[] data = request.split("//");
        String[] strArray = data[1].substring(1, data[1].length() - 1).split(", ");
        double[] doubleArray = new double[strArray.length];

        for (int i = 0; i < strArray.length; i++) {
            doubleArray[i] = Double.parseDouble(strArray[i]);
        }

        return doubleArray;
    }
}
