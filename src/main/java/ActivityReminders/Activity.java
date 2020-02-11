package ActivityReminders;

public abstract class Activity {
    protected String name;
    protected ActivityFrequency frequency;

    public Activity(String activityName, ActivityFrequency freq) {
        this.name = activityName;
        this.frequency = freq;
    }
}
