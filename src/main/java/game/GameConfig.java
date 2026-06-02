package game;

public final class GameConfig {

    private GameConfig () { }

    public static final int BOARD_WIDTH = 360;
    public static final int BOARD_HEIGHT = 640;

    public static final int CANVAS_WIDTH = BOARD_WIDTH;
    public static final int CANVAS_HEIGHT = BOARD_HEIGHT;

    public static final long UPDATE_INTERVAL_NANOS = 16_666_667L;;

}
