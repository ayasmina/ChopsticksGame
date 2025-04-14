
import java.util.*;
//computer part
public class ChopsticksAI {
    private Random random = new Random();

    public String[] getRandomMove(ChopsticksGame game) {
        List<String[]> validMoves = getAllValidMoves(game);
        if (validMoves.isEmpty()) {
            return null;
        }
        return validMoves.get(random.nextInt(validMoves.size()));
    }

    public List<String[]> getAllValidMoves(ChopsticksGame game) {
        List<String[]> validMoves = new ArrayList<>();

        String[] playerHands = {"left", "right"};
        String[] opponentHands = {"left", "right"};

        for (String playerHand : playerHands) {
            for (String opponentHand : opponentHands) {
                if (game.isValidMove(playerHand, opponentHand)) {
                    validMoves.add(new String[]{playerHand, opponentHand});
                }
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

        String[] bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (String[] move : getAllValidMoves(game)) {
            // Create a copy of the game state to simulate the move
            GameState nextState = applyMove(currentState, move[0], move[1]);
            int score = minimax(nextState, depth - 1, false);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int minimax(GameState state, int depth, boolean isMaximizing) {
        int winner = checkWinner(state);
        if (winner == 1) {
            return 10;
        } else if (winner == 2) {
            return -10;
        } else if (depth == 0) {
            return evaluateState(state);
        }

        List<GameState> nextStates = generateNextStates(state);

        if (nextStates.isEmpty()) {
            return isMaximizing ? -10 : 10;
        }

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (GameState nextState : nextStates) {
                int score = minimax(nextState, depth - 1, false);
                bestScore = Math.max(bestScore, score);
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (GameState nextState : nextStates) {
                int score = minimax(nextState, depth - 1, true);
                bestScore = Math.min(bestScore, score);
            }
            return bestScore;
        }
    }

    private int evaluateState(GameState state) {
        int p1Score = state.p1Left + state.p1Right;
        int p2Score = state.p2Left + state.p2Right;

        return p1Score - p2Score;
    }

    private int checkWinner(GameState state) {
        boolean p1Alive = (state.p1Left > 0) || (state.p1Right > 0);
        boolean p2Alive = (state.p2Left > 0) || (state.p2Right > 0);

        if (!p1Alive) {
            return 2;  // Player 2 wins
        } else if (!p2Alive) {
            return 1;  // Player 1 wins
        } else {
            return 0;  // No winner yet
        }
    }

    private List<GameState> generateNextStates(GameState state) {
        List<GameState> nextStates = new ArrayList<>();

        if (state.currentPlayer == 1) {
            // Player 1's turn
            if (state.p1Left > 0) {
                if (state.p2Left > 0) {
                    nextStates.add(applyMove(state, "left", "left"));
                }
                if (state.p2Right > 0) {
                    nextStates.add(applyMove(state, "left", "right"));
                }
            }
            if (state.p1Right > 0) {
                if (state.p2Left > 0) {
                    nextStates.add(applyMove(state, "right", "left"));
                }
                if (state.p2Right > 0) {
                    nextStates.add(applyMove(state, "right", "right"));
                }
            }
        } else {
            // Player 2's turn
            if (state.p2Left > 0) {
                if (state.p1Left > 0) {
                    nextStates.add(applyMove(state, "left", "left"));
                }
                if (state.p1Right > 0) {
                    nextStates.add(applyMove(state, "left", "right"));
                }
            }
            if (state.p2Right > 0) {
                if (state.p1Left > 0) {
                    nextStates.add(applyMove(state, "right", "left"));
                }
                if (state.p1Right > 0) {
                    nextStates.add(applyMove(state, "right", "right"));
                }
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

        if (newState.currentPlayer == 1) {
            int attackValue = playerHand.equals("left") ? newState.p1Left : newState.p1Right;

            if (opponentHand.equals("left")) {
                newState.p2Left += attackValue;
                if (newState.p2Left >= 5) {
                    newState.p2Left = 0;
                }
            } else {
                newState.p2Right += attackValue;
                if (newState.p2Right >= 5) {
                    newState.p2Right = 0;
                }
            }
        } else {
            int attackValue = playerHand.equals("left") ? newState.p2Left : newState.p2Right;

            if (opponentHand.equals("left")) {
                newState.p1Left += attackValue;
                if (newState.p1Left >= 5) {
                    newState.p1Left = 0;
                }
            } else {
                newState.p1Right += attackValue;
                if (newState.p1Right >= 5) {
                    newState.p1Right = 0;
                }
            }
        }

        newState.currentPlayer = (newState.currentPlayer == 1) ? 2 : 1;

        return newState;
    }

    private class GameState {
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