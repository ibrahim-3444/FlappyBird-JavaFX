package game;

public interface GameListener {

    void onScoreChanged(int score);
    void onGameOver(int finalScore);

}
