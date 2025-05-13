import java.util.ArrayList;
import java.util.List;

public class ChopsticksGame {
    private int p1Left;
    private int p1Right;
    private int p2Left;
    private int p2Right;
    private int currentPlayer;
    private final int MAX_FINGERS = 5;
    private boolean gameOver;
    private int winner;

    public ChopsticksGame() {
        resetGame();
    }

    public void resetGame() {
        p1Left = 1;
        p1Right = 1;
        p2Left = 1;
        p2Right = 1;
        currentPlayer = 1;
        gameOver = false;
        winner = 0;
    }

    public boolean isValidMove(String playerHand, String opponentHand) {
        if (gameOver) {
            return false;
        }

        if (playerHand == null || opponentHand == null ||
                (!playerHand.equalsIgnoreCase("left") && !playerHand.equalsIgnoreCase("right")) ||
                (!opponentHand.equalsIgnoreCase("left") && !opponentHand.equalsIgnoreCase("right"))) {
            return false;
        }

        if (currentPlayer == 1) {
            int attackingValue = playerHand.equalsIgnoreCase("left") ? p1Left : p1Right;
            if (attackingValue == 0) {
                return false;
            }

            int targetValue = opponentHand.equalsIgnoreCase("left") ? p2Left : p2Right;
            if (targetValue == 0) {
                return false;
            }
        } else {
            int attackingValue = playerHand.equalsIgnoreCase("left") ? p2Left : p2Right;
            if (attackingValue == 0) {
                return false;
            }

            int targetValue = opponentHand.equalsIgnoreCase("left") ? p1Left : p1Right;
            if (targetValue == 0) {
                return false;
            }
        }

        return true;
    }

    public boolean makeMove(String playerHand, String opponentHand) {
        if (!isValidMove(playerHand, opponentHand)) {
            return false;
        }

        int attackValue;

        if (currentPlayer == 1) {
            attackValue = playerHand.equalsIgnoreCase("left") ? p1Left : p1Right;

            if (opponentHand.equalsIgnoreCase("left")) {
                int newValue = (p2Left + attackValue) % MAX_FINGERS;
                p2Left = newValue == 0 ? 0 : newValue;
            } else {
                int newValue = (p2Right + attackValue) % MAX_FINGERS;
                p2Right = newValue == 0 ? 0 : newValue;
            }
        } else {
            attackValue = playerHand.equalsIgnoreCase("left") ? p2Left : p2Right;

            if (opponentHand.equalsIgnoreCase("left")) {
                int newValue = (p1Left + attackValue) % MAX_FINGERS;
                p1Left = newValue == 0 ? 0 : newValue;
            } else {
                int newValue = (p1Right + attackValue) % MAX_FINGERS;
                p1Right = newValue == 0 ? 0 : newValue;
            }
        }


        checkWinner();

        if (!gameOver) {
            currentPlayer = (currentPlayer == 1) ? 2 : 1;
        }

        return true;
    }

    public void checkWinner() {
        boolean p1Alive = (p1Left > 0) || (p1Right > 0);
        boolean p2Alive = (p2Left > 0) || (p2Right > 0);

        if (!p2Alive || (currentPlayer == 2 && getAllValidMoves().isEmpty())) {
            winner = 1;
            gameOver = true;
        } else if (!p1Alive || (currentPlayer == 1 && getAllValidMoves().isEmpty())) {
            winner = 2;
            gameOver = true;
        }
    }

    private List<String[]> getAllValidMoves() {
        List<String[]> moves = new ArrayList<>();

        if (gameOver) {
            return moves;
        }

        String[] playerHands = {"left", "right"};
        String[] opponentHands = {"left", "right"};

        for (String playerHand : playerHands) {
            int attackValue = currentPlayer == 1 ?
                    (playerHand.equals("left") ? p1Left : p1Right ):
                    (playerHand.equals("left") ? p2Left : p2Right);

            if (attackValue == 0) continue;

            for (String opponentHand : opponentHands) {
                int targetValue = currentPlayer == 1 ?
                        (opponentHand.equals("left") ? p2Left : p2Right) :
                        (opponentHand.equals("left") ? p1Left : p1Right);

                if (targetValue == 0) continue;

                moves.add(new String[]{playerHand, opponentHand});
            }
        }

        return moves;
    }

    public String getGameState() {
        return String.format(
                "Player 1: Left=%d, Right=%d\n" +
                        "Player 2: Left=%d, Right=%d\n" +
                        "Current Player: %d\n" +
                        "Game Over: %b, Winner: %d",
                p1Left, p1Right, p2Left, p2Right, currentPlayer, gameOver, winner
        );
    }

    // Getters
    public int getP1Left() { return p1Left; }
    public int getP1Right() { return p1Right; }
    public int getP2Left() { return p2Left; }
    public int getP2Right() { return p2Right; }
    public int getCurrentPlayer() { return currentPlayer; }
    public boolean isGameOver() { return gameOver; }
    public int getWinner() { return winner; }
}