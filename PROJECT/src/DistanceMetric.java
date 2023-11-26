import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class DistanceMetric {
    public static int hammingDistance (Record x, Record y) {
        String binStr1 = Integer.toBinaryString(x.getValue());
        String binStr2 = Integer.toBinaryString(y.getValue());
        int lenDiff = Math.abs(binStr1.length() - binStr2.length());
        if (binStr1.length() < binStr2.length()) {
            binStr1 = "0".repeat(lenDiff) + binStr1;
        } else {
            binStr2 = "0".repeat(lenDiff) + binStr2;
        }
        int count = 0;
        for (int i = 0; i < binStr1.length(); i++) {
            if (binStr1.charAt(i) != binStr2.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    static int manhattanDistanceSum(int arr[], int n) {
        Arrays.sort(arr);
        int res = 0, sum = 0;
        for (int i = 0; i < n; i++) {
            res += (arr[i] * i - sum);
            sum += arr[i];
        }
        return res;
    }

    static int manhattanTotalDistance(int x[], int y[], int n) { return manhattanDistanceSum(x, n) + manhattanDistanceSum(y, n); }

    public static double pRoot(double value, double root) {
        double rootValue = 1.0 / root;
        return new BigDecimal(value).pow((int) rootValue).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }

    public static double minkowskiDistance(double[] x, double[] y, double pValue) {
        double sum = 0.0;
        for (int i = 0; i < x.length; i++) {
            double difference = x[i] - y[i];
            sum += Math.pow(Math.abs(difference), pValue);
        }
        return pRoot(sum, pValue);
    }
}
