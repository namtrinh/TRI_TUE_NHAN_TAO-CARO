package com.caro.Point;

import com.caro.TicTacToe.PlayWithAI;

public class PlayWithAiCaro extends PlayWithAI {


    /**
     * Constructor to setup the game and the GUI components
     *
     * @param name
     */

    public PlayWithAiCaro(String name) {
        super(name);
    }

    @Override
    public boolean hasWon(Seed theSeed, int rowSelected, int colSelected) {
        int countRowWin = 0;
        int countColWin = 0;
        int countDiaWin = 0;
        int countOpDiaWin = 0;
        // Kiểm tra hàng ngang
        for (int i = colSelected+1; i<COLS; i++){
            if(board[rowSelected][i] != theSeed) break;
            else if(board[rowSelected][i] == theSeed)  countRowWin++;
        }
        for (int i = colSelected-1; i>=0; i--){
            if(board[rowSelected][i] != theSeed) break;
            else  if(board[rowSelected][i] == theSeed) countRowWin++;
        }
        if(countRowWin == 4) return true;
        // Kiểm tra hàng dọc
        for (int i = rowSelected+1; i<ROWS; i++){
            if(board[i][colSelected] != theSeed) break;
            else  if(board[i][colSelected] == theSeed) countColWin++;
        }
        for (int i = rowSelected-1; i>=0; i--){
            if(board[i][colSelected] != theSeed) break;
            else  if(board[i][colSelected] == theSeed) countColWin++;
        }
        if(countColWin == 4) return true;
        // Kiểm tra hàng chéo
        int j = colSelected;
        for (int i = rowSelected+1; i<ROWS; i++){
            j++;
            if(j>=COLS) break;
            if(board[i][j] != theSeed) break;
            else  if(board[i][j] == theSeed) countDiaWin++;
        }
        j = colSelected;
        for (int i = rowSelected-1; i>=0; i--){
            j--;
            if(j<0) break;
            if(board[i][j] != theSeed) break;
            else if(board[i][j] == theSeed) countDiaWin++;
        }
        if(countDiaWin==4) return true;
        // Kiểm tra hàng chéo đối
        j = colSelected;
        for (int i = rowSelected+1; i<ROWS; i++){
            j--;
            if(j<0) break;
            if(board[i][j] != theSeed) break;
            else if(board[i][j] == theSeed) countOpDiaWin++;
        }
        j = colSelected;
        for (int i = rowSelected-1; i>=0; i--){
            j++;
            if(j>=COLS) break;
            if(board[i][j] != theSeed) break;
            else if(board[i][j] == theSeed) countOpDiaWin++;
        }
        if(countOpDiaWin==4) return true;
        return false;
    }
}
