package lazer.com.apilazer.model;

public class Capture {
    private int team; // 1 = Rouge, 2 = Bleu
    private long timeHeld; //Temps de possession en milliseconds

    public Capture() {
    }

    public Capture(int team, long timeHeld) {
        this.team = team;
        this.timeHeld = timeHeld;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public long getTimeHeld() {
        return timeHeld;
    }

    public void setTimeHeld(long timeHeld) {
        this.timeHeld = timeHeld;
    }
}
