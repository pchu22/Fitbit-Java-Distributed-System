package Comparison_Methods;

import Dataset.Dataset;

import java.util.List;

public class Hamming {
    private Dataset dataset;
    private Double value;
    private int difference;

    public String Hamming(Dataset recordToCompare, List<Dataset> allRecords){
        this.dataset = recordToCompare;
        this.difference = 0;

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

    private int compareCharacters(int _n1, int _n2){
        StringBuilder str1 = new StringBuilder(Integer.toString(_n1));
        StringBuilder str2 = new StringBuilder(Integer.toString(_n2));

        while (str1.length() != str2.length()){
            if (str1.length() > str2.length()){
                str1.insert(0, "0");
            } else {
                str2.insert(0, "0");
            }
        }

        for(int i = 0; i < str1.length(); i++){
            if (str1.charAt(i) != str2.charAt(i)){
                difference++;
            }
        }

        return difference;
    }

    private Double calculateDistance(Dataset _dataset1, Dataset _dataset2){
        double sum = 0;

        sum += compareCharacters(_dataset1.getMinutesVeryActive(), _dataset2.getMinutesVeryActive());
        sum += compareCharacters(_dataset1.getTrackerActivityCalories(), _dataset2.getTrackerActivityCalories());
        sum += compareCharacters(_dataset1.getFloors(), _dataset2.getFloors());
        sum += compareCharacters(_dataset1.getTrackerCalories(), _dataset2.getTrackerCalories());
        sum += compareCharacters(_dataset1.getMinutesLightlyActive(), _dataset2.getMinutesLightlyActive());
        sum += compareCharacters(_dataset1.getfoodsLogCaloriesIn(), _dataset2.getfoodsLogCaloriesIn());
        sum += compareCharacters(_dataset1.getDistance(), _dataset2.getDistance());
        sum += compareCharacters(_dataset1.getMinutesFairlyActive(), _dataset2.getMinutesFairlyActive());
        sum += compareCharacters(_dataset1.getTrackerMinutesVeryActive(), _dataset2.getTrackerMinutesVeryActive());
        sum += compareCharacters(_dataset1.getTrackerFloors(), _dataset2.getTrackerFloors());
        sum += compareCharacters(_dataset1.getTrackerSteps(), _dataset2.getTrackerSteps());
        sum += compareCharacters(_dataset1.getTrackerDistance(), _dataset2.getTrackerDistance());
        sum += compareCharacters(_dataset1.getTrackerElevation(), _dataset2.getTrackerElevation());
        sum += compareCharacters(_dataset1.getTrackerMinutesFairlyActive(), _dataset2.getTrackerMinutesFairlyActive());
        sum += compareCharacters(_dataset1.getTrackerMinutesSedentary(), _dataset2.getTrackerMinutesSedentary());
        sum += compareCharacters(_dataset1.getElevation(), _dataset2.getElevation());
        sum += compareCharacters(_dataset1.getSteps(), _dataset2.getSteps());
        sum += compareCharacters(_dataset1.getActivityCalories(), _dataset2.getActivityCalories());
        sum += compareCharacters(_dataset1.getTrackerMinutesLightlyActive(), _dataset2.getTrackerMinutesLightlyActive());
        sum += compareCharacters(_dataset1.getBodyLogFat(), _dataset2.getBodyLogFat());
        sum += compareCharacters(_dataset1.getBodyLogWeight(), _dataset2.getBodyLogWeight());
        sum += compareCharacters(_dataset1.getMinutesSedentary(), _dataset2.getMinutesSedentary());
        sum += compareCharacters(_dataset1.getHeart(), _dataset2.getHeart());
        sum += compareCharacters(_dataset1.getsleep(), _dataset2.getsleep());

        return sum;
    }
}
