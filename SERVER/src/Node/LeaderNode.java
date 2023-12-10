package Node;

import java.util.HashMap;
import java.util.Map;

public class LeaderNode {
    private Map<Integer, Long> lastHeartbeatTimes = new HashMap<>();
    private Map<Integer, Boolean> votingStatusMap = new HashMap<>();
    private static final int DEFAULT_INACTIVITY_THRESHOLD = 30000;
    private final int inactivityThreshold;

    public LeaderNode() {
        this(DEFAULT_INACTIVITY_THRESHOLD);
    }

    public LeaderNode(int inactivityThreshold) {
        this.inactivityThreshold = inactivityThreshold;
    }

    public void receivedHeartbeat(int _id) {
        lastHeartbeatTimes.put(_id, System.currentTimeMillis());
    }

    public void checkAndRemoveInactiveNodes() {
        long currentTime = System.currentTimeMillis();

        for (Map.Entry<Integer, Long> entry : lastHeartbeatTimes.entrySet()) {
            int _id = entry.getKey();
            long lastHeartbeatTime = entry.getValue();

            try {
                if (currentTime - lastHeartbeatTime > inactivityThreshold) {
                    if (consensusToRemoveNode(_id)) {
                        removeNode(_id);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error checking and removing inactive node: " + e.getMessage());
            }
        }
    }

    public boolean consensusToRemoveNode(int _id) {
        int totalMembers = lastHeartbeatTimes.size();
        int votesNeeded = totalMembers / 2 + 1;

        int approvalCount = 0;

        for (Map.Entry<Integer, Boolean> entry : votingStatusMap.entrySet()) {
            boolean hasApproved = entry.getValue();

            if (hasApproved) {
                approvalCount++;
            }
        }

        return approvalCount >= votesNeeded;
    }

    private void removeNode(int _id) {
        try {
            lastHeartbeatTimes.remove(_id);
            votingStatusMap.remove(_id);
            System.out.println("Node " + _id + " removed from the group.");
        } catch (Exception e) {
            System.err.println("Error removing node: " + e.getMessage());
        }
    }
}
