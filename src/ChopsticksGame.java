public class ChopsticksGame {
    private int p1Left;
    private int p1Right;
    private int p2Left;
    private int p2Right;
    private int currentPlayer;  // 1 or 2
    private final int MAX_FINGERS = 5;  // When a hand reaches this value, it's "out"
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
        currentPlayer = 1;  // Player 1 starts
        gameOver = false;
        winner = 0;
    }

    public boolean isValidMove(String playerHand, String opponentHand) {
        if (gameOver) {
            return false;
        }

        if (currentPlayer == 1) {
            if ((playerHand.equals("left") && p1Left == 0) ||
                    (playerHand.equals("right") && p1Right == 0)) {
                return false;  // Can't use an inactive hand to attack
            }
            if ((opponentHand.equals("left") && p2Left == 0) ||
                    (opponentHand.equals("right") && p2Right == 0)) {
                return false;  // Can't attack an inactive hand
            }
        } else {
            if ((playerHand.equals("left") && p2Left == 0) ||
                    (playerHand.equals("right") && p2Right == 0)) {
                return false;
            }
            if ((opponentHand.equals("left") && p1Left == 0) ||
                    (opponentHand.equals("right") && p1Right == 0)) {
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
            attackValue = playerHand.equals("left") ? p1Left : p1Right;

            if (opponentHand.equals("left")) {
                p2Left += attackValue;
                if (p2Left >= MAX_FINGERS) {
                    p2Left = 0;  // Hand is out
                }
            } else {
                p2Right += attackValue;
                if (p2Right >= MAX_FINGERS) {
                    p2Right = 0;  // Hand is out
                }
            }
        } else {
            attackValue = playerHand.equals("left") ? p2Left : p2Right;

            if (opponentHand.equals("left")) {
                p1Left += attackValue;
                if (p1Left >= MAX_FINGERS) {
                    p1Left = 0;  // Hand is out
                }
            } else {
                p1Right += attackValue;
                if (p1Right >= MAX_FINGERS) {
                    p1Right = 0;  // Hand is out
                }
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

        if (!p1Alive) {
            winner = 2;  // Player 2 wins
            gameOver = true;
        } else if (!p2Alive) {
            winner = 1;  // Player 1 wins
            gameOver = true;
        }
    }

    public String getGameState() {
        return String.format(
                "Player 1: Left=%d, Right=%d\n" +
                        "Player 2: Left=%d, Right=%d\n" +
                        "Current Player: %d",
                p1Left, p1Right, p2Left, p2Right, currentPlayer
        );
    }

    public boolean splitFingers(int leftAmount, int rightAmount) {
        if (gameOver) {
            return false;
        }

        if (currentPlayer == 1) {
            if (leftAmount + rightAmount != p1Left + p1Right ||
                    leftAmount < 0 || rightAmount < 0 ||
                    leftAmount >= MAX_FINGERS || rightAmount >= MAX_FINGERS) {
                return false;
            }
            p1Left = leftAmount;
            p1Right = rightAmount;
        } else {
            if (leftAmount + rightAmount != p2Left + p2Right ||
                    leftAmount < 0 || rightAmount < 0 ||
                    leftAmount >= MAX_FINGERS || rightAmount >= MAX_FINGERS) {
                return false;
            }
            p2Left = leftAmount;
            p2Right = rightAmount;
        }

        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        return true;
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

//add other variations `
//fix ai end move
//fix pxp give diff background to dif players
//add delay to pcg 2 sec DOOOONE
//look into interface and what Logan said
//Work on th split kay lague
//work on g1d for computer side as p1 under  paintComponen



