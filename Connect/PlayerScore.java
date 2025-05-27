package Connect;

public class PlayerScore {
    private String name;
    private int score;

    public PlayerScore(String name, int score) {
        this.name = name;
        this.score = score;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    // Setter methods (nếu cần)
    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
