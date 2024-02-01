package Cluster_Management;

public class Voting {
    private String uuid;
    private int approvals;
    private int votes;

    public Voting(String _uuid, int _approvals, int _votes) {
        this.uuid = _uuid;
        this.approvals = _approvals;
        this.votes = _votes;
    }

    public String getUUID() { return uuid; }

    public void setUUID(String _uuid) { this.uuid = _uuid; }

    public int getApprovals() { return approvals; }

    public void setApprovals(int _approvals) { this.approvals = _approvals; }

    public int getVotes() { return votes; }

    public void addVote() { this.votes ++; }
    public void addApproval() { this.approvals ++; }

    public String toString(Voting vote){
        String res = vote.uuid + ";" + vote.approvals + ";" + vote.votes;
        return res;
    }
}