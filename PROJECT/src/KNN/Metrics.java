import jdk.internal.org.jline.utils.DiffHelper;

public class Metrics {
    public static double Manhattan(double[] x1, double[] x2){
        if(x1 == null || x2 == null){ throw new IllegalArgumentException("Distance from a null vector is undefined."); }
        if(x1.length != x2.length){ throw new IllegalArgumentException("Points must be of the same dimension"); }

        double sum = 0;
        for (int i = 0; i < x1.length; i++){
            sum += Math.abs(x1[i] - x2[i]);
        }
        return sum;
    }

    public static int Hamming(double[] x1, double[] x2){
        if(x1 == null || x2 == null){ throw new IllegalArgumentException("Distance from a null vector is undefined."); }
        if(x1.length != x2.length){ throw new IllegalArgumentException("Points must be of the same dimension"); }

        int distance = 0;
        for (int i = 0; i < x1.length; i++){
            if(x1[i] != x2[i]) { distance++; }
        }
        return distance;
    }

    public static double Minkowski(double[] x1, double[] x2, int q) {
        if(q < 1){ throw new IllegalArgumentException("Argument q must be at least 1."); }
        double distance = 0;

        if(x1 == null || x2 == null){ throw new IllegalArgumentException("Distance from a null vector is undefined."); }
        if(x1.length != x2.length){ throw new IllegalArgumentException("Points must be of the same dimension"); }

        for (int i = 0; i < x1.length; i++){
            double difference = Math.abs(x1[i] - x2[i]);

            if(q == 1){
                distance += difference;
            } else if (q == 2) {
                distance += difference * difference;
            } else if (q == Double.POSITIVE_INFINITY) {
                if(difference > distance){ distance = difference; }
                else { distance += Math.pow(difference, q); }
            } else {
                distance += Math.pow(difference, q);
            }
        }
        if(q == 1 || q == Double.POSITIVE_INFINITY) {
            return distance;
        } else if (q == 2) {
            return Math.sqrt(distance);
        } else {
            return Math.pow(distance, 1 / q);
        }
    }
}

