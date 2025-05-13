import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Minmax {
    private Random random = new Random();
    private final int MAX_FINGERS = 5;
    private final int WIN_SCORE = 100;
    private final int LOSE_SCORE = -100;

    public String[] getRandomMove(ChopsticksGame game) {
        List<String[]> validMoves = getAllValidMoves(game);
        return validMoves.isEmpty() ? null : validMoves.get(random.nextInt(validMoves.size()));
    }

    public List<String[]> getAllValidMoves(ChopsticksGame game) {
        List<String[]> validMoves = new ArrayList<>();

        if (game.isGameOver()) {
            return validMoves;
        }

        int currentPlayer = game.getCurrentPlayer();
        String[] playerHands = {"left", "right"};
        String[] opponentHands = {"left", "right"};

        for (String playerHand : playerHands) {
            int attackValue = currentPlayer == 1 ?
                    (playerHand.equals("left") ? game.getP1Left() : game.getP1Right()) :
                    (playerHand.equals("left") ? game.getP2Left() : game.getP2Right());

            if (attackValue == 0) continue;
            for (String opponentHand : opponentHands) {
                int targetValue = currentPlayer == 1 ?
                        (opponentHand.equals("left") ? game.getP2Left() : game.getP2Right()) :
                        (opponentHand.equals("left") ? game.getP1Left() : game.getP1Right());

                if (targetValue == 0) continue;

                validMoves.add(new String[]{playerHand, opponentHand});
            }
        }

        return validMoves;
    }

    public String[] getBestMove(ChopsticksGame game, int depth) {
        GameState currentState = new GameState(
                game.getP1Left(), game.getP1Right(),
                game.getP2Left(), game.getP2Right(),
                game.getCurrentPlayer()
        );

        List<String[]> validMoves = getAllValidMoves(game);
        if (validMoves.isEmpty()) {
            return null;
        }

        String[] bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (String[] move : validMoves) {
            GameState nextState = applyMove(currentState, move[0], move[1]);
            if (nextState == null) continue;

            int score = minimax(nextState, depth - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove != null ? bestMove : validMoves.get(0);
    }

    private int minimax(GameState state, int depth, boolean isMaximizing, int alpha, int beta) {
        int winner = checkWinner(state);
        if (winner == 2) return WIN_SCORE;
        if (winner == 1) return LOSE_SCORE;
        if (depth == 0) return evaluateState(state);

        List<GameState> nextStates = generateNextStates(state);
        if (nextStates.isEmpty()) {
            return isMaximizing ? LOSE_SCORE : WIN_SCORE;
        }

        if (isMaximizing) {
            int maxScore = Integer.MIN_VALUE;
            for (GameState nextState : nextStates) {
                int score = minimax(nextState, depth - 1, false, alpha, beta);
                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, score);
                if (beta <= alpha) break;
            }
            return maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (GameState nextState : nextStates) {
                int score = minimax(nextState, depth - 1, true, alpha, beta);
                minScore = Math.min(minScore, score);
                beta = Math.min(beta, score);
                if (beta <= alpha) break;
            }
            return minScore;
        }
    }

    private int evaluateState(GameState state) {

        int p1Total = state.p1Left + state.p1Right;
        int p2Total = state.p2Left + state.p2Right;
        return p2Total - p1Total;
    }

    private int checkWinner(GameState state) {
        boolean p1Alive = state.p1Left > 0 || state.p1Right > 0;
        boolean p2Alive = state.p2Left > 0 || state.p2Right > 0;

        if (!p2Alive) return 1;
        if (!p1Alive) return 2;

        boolean hasMoves = false;
        if (state.currentPlayer == 1) {
            if (state.p1Left > 0 && (state.p2Left > 0 || state.p2Right > 0)) hasMoves = true;
            if (state.p1Right > 0 && (state.p2Left > 0 || state.p2Right > 0)) hasMoves = true;
        } else {
            if (state.p2Left > 0 && (state.p1Left > 0 || state.p1Right > 0)) hasMoves = true;
            if (state.p2Right > 0 && (state.p1Left > 0 || state.p1Right > 0)) hasMoves = true;
        }

        if (!hasMoves) {
            return state.currentPlayer == 1 ? 2 : 1; // Current player loses
        }

        return 0;
    }

    private List<GameState> generateNextStates(GameState state) {
        List<GameState> nextStates = new ArrayList<>();
        if (checkWinner(state) != 0) return nextStates;

        if (state.currentPlayer == 1) {
            if (state.p1Left > 0) {
                if (state.p2Left > 0) nextStates.add(applyMove(state, "left", "left"));
                if (state.p2Right > 0) nextStates.add(applyMove(state, "left", "right"));
            }
            if (state.p1Right > 0) {
                if (state.p2Left > 0) nextStates.add(applyMove(state, "right", "left"));
                if (state.p2Right > 0) nextStates.add(applyMove(state, "right", "right"));
            }
        } else {
            if (state.p2Left > 0) {
                if (state.p1Left > 0) nextStates.add(applyMove(state, "left", "left"));
                if (state.p1Right > 0) nextStates.add(applyMove(state, "left", "right"));
            }
            if (state.p2Right > 0) {
                if (state.p1Left > 0) nextStates.add(applyMove(state, "right", "left"));
                if (state.p1Right > 0) nextStates.add(applyMove(state, "right", "right"));
            }
        }
        return nextStates;
    }

    private GameState applyMove(GameState state, String playerHand, String opponentHand) {
        GameState newState = new GameState(
                state.p1Left, state.p1Right,
                state.p2Left, state.p2Right,
                state.currentPlayer
        );

        int attackValue, targetValue;
        if (newState.currentPlayer == 1) {
            attackValue = playerHand.equals("left") ? newState.p1Left : newState.p1Right;
            if (opponentHand.equals("left")) {
                targetValue = newState.p2Left;
                if (targetValue == 0) return null;
                newState.p2Left = (targetValue + attackValue) % MAX_FINGERS;
                if (newState.p2Left == 0) newState.p2Left = 0;
            } else {
                targetValue = newState.p2Right;
                if (targetValue == 0) return null;
                newState.p2Right = (targetValue + attackValue) % MAX_FINGERS;
                if (newState.p2Right == 0) newState.p2Right = 0;
            }
        } else {
            attackValue = playerHand.equals("left") ? newState.p2Left : newState.p2Right;
            if (opponentHand.equals("left")) {
                targetValue = newState.p1Left;
                if (targetValue == 0) return null;
                newState.p1Left = (targetValue + attackValue) % MAX_FINGERS;
                if (newState.p1Left == 0) newState.p1Left = 0;
            } else {
                targetValue = newState.p1Right;
                if (targetValue == 0) return null;
                newState.p1Right = (targetValue + attackValue) % MAX_FINGERS;
                if (newState.p1Right == 0) newState.p1Right = 0;
            }
        }

        newState.currentPlayer = 3 - newState.currentPlayer;
        return newState;
    }

    private static class GameState {
        int p1Left, p1Right, p2Left, p2Right;
        int currentPlayer;

        public GameState(int p1Left, int p1Right, int p2Left, int p2Right, int currentPlayer) {
            this.p1Left = p1Left;
            this.p1Right = p1Right;
            this.p2Left = p2Left;
            this.p2Right = p2Right;
            this.currentPlayer = currentPlayer;
        }
    }
}
//getBestMove:
//    - Gets all valid moves using getAllValidMoves.
//    - Checks each move using the minimax  algorithm.
//    - Picks the move with the highest score.

// . minimax:
//    - Simulates all possible moves for both players and calculates a score for each one.
//    - Gives a score if there’s a winner (positive for AI win, negative for opponent win).
//    - Looks ahead to possible future moves, trying to maximize the AI's score and minimize the opponent’s score.
//    - Stops when no moves are left or when depth limit is reached.

// . evaluateState:
//    - Adds up the AI's fingers and subtracts the opponent’s fingers.
//    - Returns a score: positive if AI has more fingers, negative if opponent has more.

// . getAllValidMoves:
//    - Checks every combination of the AI’s hands (left and right) and the opponent’s hands.
//    - Adds valid moves to the list.

// . applyMove:
//    - Takes the current game state and applies the move.
//    - Updates the number of fingers for both players’ hands and switches to the other player’s turn.

//When I refer to the AI in the context of the code,
// I mean the computer-controlled player in the game that makes decisions based on the current game state.
// The AI uses algorithms, like minimax, to choose the best possible move during its turn.
//so basically when i say ai its just player one which is the computer