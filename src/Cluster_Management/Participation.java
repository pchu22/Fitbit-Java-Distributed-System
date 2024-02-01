package Cluster_Management;

import java.time.Duration;
import java.time.LocalDateTime;

public class Participation {
    private final String ID;
    private LocalDateTime lastResponseTime;
    private boolean  status;  //1 - active, 0 - inactive

    public Participation(String _id) {
        this.ID = _id;
        this.lastResponseTime = LocalDateTime.now();
        this.status = true;
    }

    public String getID() { return ID; }

    public LocalDateTime getLastResponseTime() { return lastResponseTime; }

    public boolean getStatus(){ return status; }

    public void setStatus(boolean _state) { this.status = _state; }

    public void setLastResponseTime(){ this.lastResponseTime = LocalDateTime.now(); }

    public boolean hasBeenUpdated() {
        if (lastResponseTime == null) {
            return false;
        }

        long secondsSinceLastResponse = Duration.between(lastResponseTime, LocalDateTime.now()).getSeconds();
        return secondsSinceLastResponse <= 60;
    }
}
