package Dataset;

import java.io.Serializable;

public class Dataset implements Serializable {

    private final int minutesVeryActive;
    private final int trackerActivityCalories;
    private final int floors;
    private final int trackerCalories;
    private final int minutesLightlyActive;
    private final int foodsLogCaloriesIn;
    private final int distance;
    private final int minutesFairlyActive;
    private final int trackerMinutesVeryActive;
    private final int trackerFloors;
    private final int trackerSteps;
    private final int trackerDistance;
    private final int trackerElevation;
    private final int trackerMinutesFairlyActive;
    private final int trackerMinutesSedentary;
    private final int elevation;
    private final int steps;
    private final int activityCalories;
    private final int trackerMinutesLightlyActive;
    private final int bodyLogFat;
    private final int bodyLogWeight;
    private final int minutesSedentary;
    private final int heart;
    private final int sleep;

    public Dataset(int _minutesVeryActive, int _trackerActivityCalories, int _floors, int _trackerCalories, int _minutesLightlyActive,
                   int _foodsLogCaloriesIn, int _distance, int _minutesFairlyActive, int _trackerMinutesVeryActive, int _trackerFloors,
                   int _trackerSteps, int _trackerDistance, int _trackerElevation, int _trackerMinutesFairlyActive, int _trackerMinutesSedentary,
                   int _elevation, int _steps, int _activityCalories, int _trackerMinutesLightlyActive, int _bodyLogFat, int _bodyLogWeight,
                   int _minutesSedentary, int _heart, int _sleep) {
        this.minutesVeryActive = _minutesVeryActive;
        this.trackerActivityCalories = _trackerActivityCalories;
        this.floors = _floors;
        this.trackerCalories = _trackerCalories;
        this.minutesLightlyActive = _minutesLightlyActive;
        this.foodsLogCaloriesIn = _foodsLogCaloriesIn;
        this.distance = _distance;
        this.minutesFairlyActive = _minutesFairlyActive;
        this.trackerMinutesVeryActive = _trackerMinutesVeryActive;
        this.trackerFloors = _trackerFloors;
        this.trackerSteps = _trackerSteps;
        this.trackerDistance = _trackerDistance;
        this.trackerElevation = _trackerElevation;
        this.trackerMinutesFairlyActive = _trackerMinutesFairlyActive;
        this.trackerMinutesSedentary = _trackerMinutesSedentary;
        this.elevation = _elevation;
        this.steps = _steps;
        this.activityCalories = _activityCalories;
        this.trackerMinutesLightlyActive = _trackerMinutesLightlyActive;
        this.bodyLogFat = _bodyLogFat;
        this.bodyLogWeight = _bodyLogWeight;
        this.minutesSedentary = _minutesSedentary;
        this.heart = _heart;
        this.sleep = _sleep;
    }

    public Dataset(String data) {
        String[] values = data.split(";");
        this.minutesVeryActive = Integer.parseInt(values[0]);
        this.trackerActivityCalories = Integer.parseInt(values[1]);
        this.floors = Integer.parseInt(values[2]);
        this.trackerCalories = Integer.parseInt(values[3]);
        this.minutesLightlyActive = Integer.parseInt(values[4]);
        this.foodsLogCaloriesIn = Integer.parseInt(values[5]);
        this.distance = Integer.parseInt(values[6]);
        this.minutesFairlyActive = Integer.parseInt(values[7]);
        this.trackerMinutesVeryActive = Integer.parseInt(values[8]);
        this.trackerFloors = Integer.parseInt(values[9]);
        this.trackerSteps = Integer.parseInt(values[10]);
        this.trackerDistance = Integer.parseInt(values[11]);
        this.trackerElevation = Integer.parseInt(values[12]);
        this.trackerMinutesFairlyActive = Integer.parseInt(values[13]);
        this.trackerMinutesSedentary = Integer.parseInt(values[14]);
        this.elevation = Integer.parseInt(values[15]);
        this.steps = Integer.parseInt(values[16]);
        this.activityCalories = Integer.parseInt(values[17]);
        this.trackerMinutesLightlyActive = Integer.parseInt(values[18]);
        this.bodyLogFat = Integer.parseInt(values[19]);
        this.bodyLogWeight = Integer.parseInt(values[20]);
        this.minutesSedentary = Integer.parseInt(values[21]);
        this.heart = Integer.parseInt(values[22]);
        this.sleep = Integer.parseInt(values[23]);
    }

    public String getString(){
        return (this.minutesVeryActive + ";" + this.trackerActivityCalories + ";" + this.floors + ";" + this.trackerCalories + ";" +
                this.minutesLightlyActive + ";" + this.foodsLogCaloriesIn + ";" + this.distance + ";" + this.minutesFairlyActive + ";" +
                this.trackerMinutesVeryActive + ";" + this.trackerFloors + ";" + this.trackerSteps + ";" + this.trackerDistance + ";" +
                this.trackerElevation + ";" + this.trackerMinutesFairlyActive + ";" + this.trackerMinutesSedentary + ";" +
                this.elevation + ";" + this.steps + ";" + this.activityCalories + ";" + this.trackerMinutesLightlyActive + ";" +
                this.bodyLogFat + ";" + this.bodyLogWeight + ";" + this.minutesSedentary + ";" + this.heart + ";" + this.sleep);
    }

    public int getMinutesVeryActive(){
        return this.minutesVeryActive;
    }
    public int getTrackerActivityCalories(){
        return this.trackerActivityCalories;
    }
    public int getFloors(){
        return this.floors;
    }
    public int getTrackerCalories(){
        return this.trackerCalories;
    }
    public int getMinutesLightlyActive(){
        return this.minutesLightlyActive;
    }
    public int getfoodsLogCaloriesIn(){
        return this.foodsLogCaloriesIn;
    }
    public int getDistance(){
        return this.distance;
    }
    public int getMinutesFairlyActive(){
        return this.minutesFairlyActive;
    }
    public int getTrackerMinutesVeryActive(){
        return this.trackerMinutesVeryActive;
    }
    public int getTrackerFloors(){
        return this.trackerFloors;
    }
    public int getTrackerSteps(){
        return this.trackerSteps;
    }
    public int getTrackerDistance(){
        return this.trackerDistance;
    }
    public int getTrackerElevation(){
        return this.trackerElevation;
    }
    public int getTrackerMinutesFairlyActive(){
        return this.trackerMinutesFairlyActive;
    }
    public int getTrackerMinutesSedentary(){
        return this.trackerMinutesSedentary;
    }
    public int getElevation(){
        return this.elevation;
    }
    public int getSteps(){
        return this.steps;
    }
    public int getActivityCalories(){
        return this.activityCalories;
    }
    public int getTrackerMinutesLightlyActive(){
        return this.trackerMinutesLightlyActive;
    }
    public int getBodyLogFat(){
        return this.bodyLogFat;
    }
    public int getBodyLogWeight(){
        return this.bodyLogWeight;
    }
    public int getMinutesSedentary(){ return this.minutesSedentary; }
    public int getHeart(){
        return this.heart;
    }
    public int getsleep(){
        return this.sleep;
    }
}