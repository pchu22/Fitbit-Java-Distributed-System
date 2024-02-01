package Comparison_Methods;

import Dataset.Dataset;
import java.util.List;

public class KNearestNeighbours {
    private Dataset dataset;
    private Double value;
    public String KNearestNeighbours(Dataset recordToCompare, List<Dataset> allRecords) {
        this.dataset = recordToCompare;

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

        sum += Math.pow(_dataset1.getMinutesVeryActive(), _dataset2.getMinutesVeryActive());
        sum += Math.pow(_dataset1.getTrackerActivityCalories(), _dataset2.getTrackerActivityCalories());
        sum += Math.pow(_dataset1.getFloors(), _dataset2.getFloors());
        sum += Math.pow(_dataset1.getTrackerCalories(), _dataset2.getTrackerCalories());
        sum += Math.pow(_dataset1.getMinutesLightlyActive(), _dataset2.getMinutesLightlyActive());
        sum += Math.pow(_dataset1.getfoodsLogCaloriesIn(), _dataset2.getfoodsLogCaloriesIn());
        sum += Math.pow(_dataset1.getDistance(), _dataset2.getDistance());
        sum += Math.pow(_dataset1.getMinutesFairlyActive(), _dataset2.getMinutesFairlyActive());
        sum += Math.pow(_dataset1.getTrackerMinutesVeryActive(), _dataset2.getTrackerMinutesVeryActive());
        sum += Math.pow(_dataset1.getTrackerFloors(), _dataset2.getTrackerFloors());
        sum += Math.pow(_dataset1.getTrackerSteps(), _dataset2.getTrackerSteps());
        sum += Math.pow(_dataset1.getTrackerDistance(), _dataset2.getTrackerDistance());
        sum += Math.pow(_dataset1.getTrackerElevation(), _dataset2.getTrackerElevation());
        sum += Math.pow(_dataset1.getTrackerMinutesFairlyActive(), _dataset2.getTrackerMinutesFairlyActive());
        sum += Math.pow(_dataset1.getTrackerMinutesSedentary(), _dataset2.getTrackerMinutesSedentary());
        sum += Math.pow(_dataset1.getElevation(), _dataset2.getElevation());
        sum += Math.pow(_dataset1.getSteps(), _dataset2.getSteps());
        sum += Math.pow(_dataset1.getActivityCalories(), _dataset2.getActivityCalories());
        sum += Math.pow(_dataset1.getTrackerMinutesLightlyActive(), _dataset2.getTrackerMinutesLightlyActive());
        sum += Math.pow(_dataset1.getBodyLogFat(), _dataset2.getBodyLogFat());
        sum += Math.pow(_dataset1.getBodyLogWeight(), _dataset2.getBodyLogWeight());
        sum += Math.pow(_dataset1.getMinutesSedentary(), _dataset2.getMinutesSedentary());
        sum += Math.pow(_dataset1.getHeart(), _dataset2.getHeart());
        sum += Math.pow(_dataset1.getsleep(), _dataset2.getsleep());

        return Math.sqrt(sum);
    }

}
