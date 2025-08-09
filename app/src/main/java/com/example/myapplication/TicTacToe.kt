package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class Player {
    X, O, NONE
}

data class CellState(val player: Player = Player.NONE) // Represents a single cell on the board

enum class GameStatus {
    ONGOING,
    X_WINS,
    O_WINS,
    DRAW
}

data class GameState(
    val board: List<List<CellState>> = List(3) { List(3) { CellState() } },
    val currentPlayer: Player = Player.X,
    val gameStatus: GameStatus = GameStatus.ONGOING
)

@Composable
fun TicTacToeScreen() {
    var gameState by remember { mutableStateOf(GameState()) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when (gameState.gameStatus) {
                GameStatus.X_WINS -> "X Wins!"
                GameStatus.O_WINS -> "O Wins!"
                GameStatus.DRAW -> "Draw!"
                GameStatus.ONGOING -> "Current Player: ${gameState.currentPlayer}"
            },
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        BoardView(gameState = gameState) { row, col ->
            if (gameState.board[row][col].player == Player.NONE && gameState.gameStatus == GameStatus.ONGOING) {
                val newBoard = gameState.board.map { it.toMutableList() }.toMutableList()
                newBoard[row][col] = CellState(gameState.currentPlayer)

                val newGameStatus = checkGameStatus(newBoard, gameState.currentPlayer)
                val nextPlayer = if (gameState.currentPlayer == Player.X) Player.O else Player.X

                gameState = gameState.copy(
                    board = newBoard.map { it.toList() },
                    currentPlayer = if (newGameStatus == GameStatus.ONGOING) nextPlayer else gameState.currentPlayer,
                    gameStatus = newGameStatus
                )
            }
        }

        if (gameState.gameStatus != GameStatus.ONGOING) {
            Button(
                onClick = { gameState = GameState() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Play Again")
            }
        }
    }
}

@Composable
fun BoardView(gameState: GameState, onCellClick: (row: Int, col: Int) -> Unit) {
    Column {
        gameState.board.forEachIndexed { rowIndex, row ->
            Row {
                row.forEachIndexed { colIndex, cell ->
                    Button(
                        onClick = { onCellClick(rowIndex, colIndex) },
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp),
                        enabled = cell.player == Player.NONE && gameState.gameStatus == GameStatus.ONGOING
                    ) {
                        Text(text = cell.player.name.takeIf { it != "NONE" } ?: "", fontSize = 40.sp)
                    }
                }
            }
        }
    }
}

fun checkGameStatus(board: List<List<CellState>>, currentPlayer: Player): GameStatus {
    // Check rows
    for (row in board) {
        if (row.all { it.player == currentPlayer }) {
            return if (currentPlayer == Player.X) GameStatus.X_WINS else GameStatus.O_WINS
        }
    }

    // Check columns
    for (col in 0..2) {
        if (board.all { it[col].player == currentPlayer }) {
            return if (currentPlayer == Player.X) GameStatus.X_WINS else GameStatus.O_WINS
        }
    }

    // Check diagonals
    if (board[0][0].player == currentPlayer && board[1][1].player == currentPlayer && board[2][2].player == currentPlayer) {
        return if (currentPlayer == Player.X) GameStatus.X_WINS else GameStatus.O_WINS
    }
    if (board[0][2].player == currentPlayer && board[1][1].player == currentPlayer && board[2][0].player == currentPlayer) {
        return if (currentPlayer == Player.X) GameStatus.X_WINS else GameStatus.O_WINS
    }

    // Check for draw
    if (board.all { row -> row.all { it.player != Player.NONE } }) {
        return GameStatus.DRAW
    }

    return GameStatus.ONGOING
}

@Preview(showBackground = true)
@Composable
fun TicTacToeScreenPreview() {
    TicTacToeScreen()
}
