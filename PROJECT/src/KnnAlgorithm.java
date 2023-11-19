import java.util.List;

interface DistanceMetric<T> {
    double calculateDistance(T record1, T record2);
}

public class KnnAlgorithm {
    private DistanceMetric<Integer> distanceMetric;

    public KnnAlgorithm(DistanceMetric<Integer> distanceMetric) {
        this.distanceMetric = distanceMetric;
    }

    public double knn(List<Integer> dataset, Integer newRecord, int k) {
        double totalDistance = 0.0;
        for (Integer record : dataset) {
            double distance = distanceMetric.calculateDistance(newRecord, record);
            totalDistance += distance;
        }
        double averageDistance = totalDistance / dataset.size();
        double threshold = 10.0;
        if (averageDistance < threshold) {
            System.out.println("Add | Not Add");
        }

        return averageDistance;
    }
}

class Hamming implements DistanceMetric<Integer> {
    @Override
    public double calculateDistance(Integer record1, Integer record2) {
        return record1.equals(record2) ? 0 : 1;
    }
}

class Manhattan implements DistanceMetric<Integer> {
    @Override
    public double calculateDistance(Integer record1, Integer record2) {
        return Math.abs(record1 - record2);
    }
}

class Minkowski implements DistanceMetric<Integer> {
    private double p;

    public Minkowski(double p) {
        this.p = p;
    }

    @Override
    public double calculateDistance(Integer record1, Integer record2) {
        return Math.pow(Math.abs(record1 - record2), p);
    }
}
