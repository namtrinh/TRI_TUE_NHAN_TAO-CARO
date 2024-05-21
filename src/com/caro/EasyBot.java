package com.caro;

import com.caro.TicTacToe.Play2Players;

import java.util.Random;

public class EasyBot {



    public String getPosFrBrd(Play2Players.Seed[][] board){
        String vTri = new String();
        int pos1, pos2;
        do {
            Random rand = new Random();
            pos1 = rand.nextInt(Play2Players.COLS - 2) + 1;
            pos2 = rand.nextInt(Play2Players.COLS - 2) + 1;
            vTri = pos1 + " " + pos2;
        }
        while (board[pos1][pos2] != Play2Players.Seed.EMPTY);
        return vTri;
    }
}