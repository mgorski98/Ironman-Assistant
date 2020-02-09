package Milestones;

import java.time.LocalDate;

public class Milestone {
    private String milestoneInfo;
    private int totalLevel;
    private LocalDate milestoneDate;

    public Milestone() {}

    public String getMilestoneInfo() {
        return milestoneInfo;
    }

    public void setMilestoneInfo(String milestoneInfo) {
        this.milestoneInfo = milestoneInfo;
    }

    public int getTotalLevel() {
        return totalLevel;
    }

    public void setTotalLevel(int totalLevel) {
        this.totalLevel = totalLevel;
    }

    public LocalDate getMilestoneDate() {
        return milestoneDate;
    }

    public void setMilestoneDate(LocalDate milestoneDate) {
        this.milestoneDate = milestoneDate;
    }
}
