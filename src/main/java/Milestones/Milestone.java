package Milestones;

import java.time.LocalDateTime;

public class Milestone {
    private String milestoneInfo;
    private int totalLevel;
    private LocalDateTime milestoneDate;

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

    public LocalDateTime getMilestoneDate() {
        return milestoneDate;
    }

    public void setMilestoneDate(LocalDateTime milestoneDate) {
        this.milestoneDate = milestoneDate;
    }
}
