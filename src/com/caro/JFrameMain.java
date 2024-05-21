package com.caro;


import com.caro.Caro.Play2PlayersCaro;
import com.caro.Caro.PlayWithAiCaro;
import com.caro.TicTacToe.Play2Players;
import com.caro.TicTacToe.PlayWithAI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class JFrameMain {
    public static JFrame jFrame;
    private JPanel JPanelMain;
    private JButton btnPlay2Players;
    private JButton btnPlayVSAI;
    private JTextField txtPlayer1Name;
    private JTextField txtPlayer2Name;
    private JButton btnExit;
    private JSpinner spinnerRow;
    private JLabel tênNgườiChơi1Label;
    private JPanel caroPanel;
    private JPanel menuPanel;

    public JFrameMain() {
        // Cài đặt model cho spinner nhập dòng, cột.
        SpinnerModel spinnerModel =
                new SpinnerNumberModel(10, //initial value
                        3, //min
                        15, //max
                        1);//step
        spinnerRow.setModel(spinnerModel);

        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        btnPlayVSAI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String Player1Name = txtPlayer1Name.getText();
                if(Player1Name.isEmpty()){
                    JOptionPane.showMessageDialog(null,"Bạn chưa nhập tên người chơi!!!");
                    return;
                }
                int row = (int)spinnerRow.getValue();
                if( row<3 || row >15){
                    JOptionPane.showMessageDialog(null,"Bạn phải nhập số dòng, cột nằm trong khoảng" +
                            " giá trị từ 3 đến 20!!!");
                    return;
                }
                // Chọn độ khó máy
                Object[] options1 = {"Dễ",
                        "Bình thường"};
                int resultLevel = JOptionPane.showOptionDialog(null,"Chọn độ khó","Chọn độ khó" +
                        "",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options1,options1[1]);

                row = (int) spinnerRow.getValue();
                if(row == 4) {
                    JOptionPane.showMessageDialog(null, "Hiện tại không hỗ trợ chơi trên bàn cờ 4x4!");
                    return;
                }
                if(row>=5){
                    if(resultLevel == JOptionPane.YES_OPTION){
                        PlayWithAiCaro.GameBot = PlayWithAI.Bot.EASY_BOT;
                    }
                    else PlayWithAiCaro.GameBot = PlayWithAI.Bot.HEURISTIC_BOT;
                    PlayWithAiCaro.newRow =row;
                    PlayWithAiCaro heuristicBotCaro = new PlayWithAiCaro(Player1Name);
                }
                else if (row ==3){
                    if(resultLevel == JOptionPane.YES_OPTION){
                        PlayWithAI.GameBot = PlayWithAI.Bot.EASY_BOT;
                    }
                    else PlayWithAI.GameBot = PlayWithAI.Bot.HEURISTIC_BOT;
                    PlayWithAI.newRow = row;
                    new PlayWithAI(Player1Name);
                }


                jFrame.setVisible(false);
            }
        });
        btnPlay2Players.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String Player1Name = txtPlayer1Name.getText();
                String Player2Name = txtPlayer2Name.getText();
                if(Player1Name.isEmpty() || Player2Name.isEmpty()) {
                    JOptionPane.showMessageDialog(null,"Bạn chưa nhập đủ tên 2 người chơi!!!");
                    return;
                }
                int row = (int)spinnerRow.getValue();
                if( row<3 || row >15){
                    JOptionPane.showMessageDialog(null,"Bạn phải nhập số dòng, cột nằm trong khoảng" +
                            " giá trị từ 3 đến 15!!!");
                    return;
                }
                row = (int) spinnerRow.getValue();
                if(row == 4) {
                    JOptionPane.showMessageDialog(null, "Hiện tại không hỗ trợ chơi trên bàn cờ 4x4!");
                    return;
                }

                if(row>=5){
                    Play2PlayersCaro.newRow = row;
                    new Play2PlayersCaro(Player1Name,Player2Name);
                }
                else if(row==3) {
                    Play2Players.newRow = row;
                    new Play2Players(Player1Name,Player2Name);
                }


                jFrame.setVisible(false);
            }
        });
    }

    public void CreateAndShow() {
        SpinnerModel spinnerModel =
                new SpinnerNumberModel(10, //initial value
                        3, 15, 1);//step

        spinnerRow.setModel(spinnerModel);
        jFrame = new JFrame("Caro");

        jFrame.setContentPane(new JFrameMain().JPanelMain);

        jFrame.setSize(400, 350);



        jFrame.setVisible(true);

        jFrame.setLocationRelativeTo(null);
    }
}
