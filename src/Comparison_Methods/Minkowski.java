package Comparison_Methods;

import Dataset.Dataset;

import java.util.List;


public class Minkowski {
    private Dataset dataset;
    private Double value;
    private int p;
    public String Minkowski(Dataset recordToCompare, List<Dataset> allRecords, int _p) {

        this.dataset = recordToCompare;
        this.p = _p;

        boolean isFirstIteration = true;
        for(Dataset record : allRecords){
            if (isFirstIteration){
                value = calculateDistance(dataset, record);
                isFirstIteration = false;
            } else {
                Double temp = calculateDistance(dataset, record);
                if(value > temp){
                    value = temp;
                }
            }
        }

        if(value > 100){
            return "IGNORE";
        } else {
            return "ADD";
        }
    }

    private Double calculateDistance(Dataset _dataset1, Dataset _dataset2) {
        double sum = 0;

        sum += Math.pow(_dataset1.getMinutesVeryActive() - _dataset2.getMinutesVeryActive(), p);
        sum += Math.pow(_dataset1.getTrackerActivityCalories() - _dataset2.getTrackerActivityCalories(), p);
        sum += Math.pow(_dataset1.getFloors() - _dataset2.getFloors(), p);
        sum += Math.pow(_dataset1.getTrackerCalories() - _dataset2.getTrackerCalories(), p);
        sum += Math.pow(_dataset1.getMinutesLightlyActive() - _dataset2.getMinutesLightlyActive(), p);
        sum += Math.pow(_dataset1.getfoodsLogCaloriesIn() - _dataset2.getfoodsLogCaloriesIn(), p);
        sum += Math.pow(_dataset1.getDistance() - _dataset2.getDistance(), p);
        sum += Math.pow(_dataset1.getMinutesFairlyActive() - _dataset2.getMinutesFairlyActive(), p);
        sum += Math.pow(_dataset1.getTrackerMinutesVeryActive() - _dataset2.getTrackerMinutesVeryActive(), p);
        sum += Math.pow(_dataset1.getTrackerFloors() - _dataset2.getTrackerFloors(), p);
        sum += Math.pow(_dataset1.getTrackerSteps() - _dataset2.getTrackerSteps(), p);
        sum += Math.pow(_dataset1.getTrackerDistance() - _dataset2.getTrackerDistance(), p);
        sum += Math.pow(_dataset1.getTrackerElevation() - _dataset2.getTrackerElevation(), p);
        sum += Math.pow(_dataset1.getTrackerMinutesFairlyActive() - _dataset2.getTrackerMinutesFairlyActive(), p);
        sum += Math.pow(_dataset1.getTrackerMinutesSedentary() - _dataset2.getTrackerMinutesSedentary(), p);
        sum += Math.pow(_dataset1.getElevation() - _dataset2.getElevation(), p);
        sum += Math.pow(_dataset1.getSteps() - _dataset2.getSteps(), p);
        sum += Math.pow(_dataset1.getActivityCalories() - _dataset2.getActivityCalories(), p);
        sum += Math.pow(_dataset1.getTrackerMinutesLightlyActive() - _dataset2.getTrackerMinutesLightlyActive(), p);
        sum += Math.pow(_dataset1.getBodyLogFat() - _dataset2.getBodyLogFat(), p);
        sum += Math.pow(_dataset1.getBodyLogWeight() - _dataset2.getBodyLogWeight(), p);
        sum += Math.pow(_dataset1.getMinutesSedentary() - _dataset2.getMinutesSedentary(), p);
        sum += Math.pow(_dataset1.getHeart() - _dataset2.getHeart(), p);
        sum += Math.pow(_dataset1.getsleep() - _dataset2.getsleep(), p);

        return Math.pow(sum, 1.0/p);
    }
}
