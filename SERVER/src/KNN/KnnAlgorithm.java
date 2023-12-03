package KNN;

import java.util.ArrayList;
import java.util.List;

public class KnnAlgorithm {
    public static List<Double> findNearestNeighbours(double[] target, double[][] dataset, int k, int method) {
        List<Double> nearestDistances = new ArrayList<>();

        if (k <= 0 || dataset.length == 0 || dataset[0].length != target.length) {
            return nearestDistances;
        }

        double[] distances = calculateDistances(target, dataset, method);

        if (method != 0) {
            for (int i = 0; i < k; i++) {
                double minDistance = Double.MAX_VALUE;
                int minIndex = -1;

                for (int j = 0; j < distances.length; j++) {
                    if (distances[j] < minDistance && !nearestDistances.contains(distances[j])) {
                        minDistance = distances[j];
                        minIndex = j;
                    }
                }
                if (minIndex != -1) {
                    nearestDistances.add(minDistance);
                } else {
                    break;
                }
            }
        }
        return nearestDistances;
    }

    private static double[] calculateDistances(double[] target, double[][] dataset, int method) {
        double[] distances = new double[dataset.length];

        for (int i = 0; i < dataset.length; i++) {
            if (method == 1) {
                distances[i] = Metrics.Minkowski(target, dataset[i], 2);
            } else if (method == 2) {
                distances[i] = Metrics.Hamming(target, dataset[i]);
            } else if (method == 3) {
                distances[i] = Metrics.Manhattan(target, dataset[i]);
            }
        }

        return distances;
    }
}
