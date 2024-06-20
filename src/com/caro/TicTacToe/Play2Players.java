package com.caro.TicTacToe;

import com.caro.JFrameMain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Play2Players extends JFrame {
    private Timer timer;
    private int timeLeft;
    private int savedPlayer1Score = 0;
    private int savedPlayer2Score = 0;



    public static int newRow = 3;
    protected Seed PlayerReRun;
    public static int rowPreSelected = -1;
    public static int colPreSelected = -1;
    public static int ROWS = 3;
    public static int COLS = 3;
    public static String Player1Name;
    public static String Player2Name;
    public static int Player1Score = 0; // Player 1 score
    public static int Player2Score = 0; // Player 2 score
    public static boolean Player1TwoMove;
    public static int STEPS = 0;
    public static int CELL_SIZE = 100;
    public static int CANVAS_WIDTH = CELL_SIZE * COLS;
    public static int CANVAS_HEIGHT = CELL_SIZE * ROWS;
    public static int GRID_WIDTH = 2;
    public static int GRID_WIDHT_HALF = GRID_WIDTH / 2;
    public static int CELL_PADDING = CELL_SIZE / 6;
    public static int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2;
    public static int SYMBOL_STROKE_WIDTH = 8;


    public enum GameState {
        PLAYING, DRAW, CROSS_WON, NOUGHT_WON
    }
    protected GameState currentState;

    public enum Seed {
        EMPTY, CROSS, NOUGHT
    }
    protected Seed currentPlayer;
    public Seed[][] board;
    protected DrawCanvas canvas;
    protected JLabel statusBar;
    protected JPanel pnButton;
    protected JButton btnNewgame;
    protected JLabel player1Label;
    protected JLabel player2Label;
    protected JLabel player1TimeLabel;
    protected JLabel player2TimeLabel;
    protected JLabel player1ScoreLabel; // Label to display Player 1 score
    protected JLabel player2ScoreLabel;
    protected JButton btnExit;
    public Play2Players(String name1, String name2) {

        STEPS = 0;
        Player1Name = name1;
        Player2Name = name2;
        // Initialize the labels
        player1TimeLabel = new JLabel("", SwingConstants.CENTER);
        player2TimeLabel = new JLabel("", SwingConstants.CENTER);
        player1ScoreLabel = new JLabel("Score: " + Player1Score, SwingConstants.CENTER);
        player2ScoreLabel = new JLabel("Score: " + Player2Score, SwingConstants.CENTER);
        PlayGame(name1, name2);
        loadPlayerScores();
    }

    public void SetUpBoard(int row) {
        ROWS = row;
        COLS = row;
        if (row <= 10) {
            CELL_SIZE = (20 / row) * 25;
            CANVAS_WIDTH = CELL_SIZE * COLS;
            CANVAS_HEIGHT = CELL_SIZE * ROWS;
            CELL_PADDING = CELL_SIZE / 6;
            SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2;
        } else {
            CELL_SIZE = (20 / row) * 45;
            CANVAS_WIDTH = CELL_SIZE * COLS;
            CANVAS_HEIGHT = CELL_SIZE * ROWS;
            CELL_PADDING = CELL_SIZE / 6;
            SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2;
        }
    }

    public void resetTimer() {
        if (timer != null) {
            timer.stop();
        }
        timeLeft = 30; // hoặc giá trị mặc định bạn muốn
        updatePlayerTimeLabels(); // Cập nhật label ngay từ đầu
        timer = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                if (timeLeft < 0) {
                    handleTimeout();
                } else {
                    updateStatusBar();
                    updatePlayerTimeLabels();
                }
            }
        });
        timer.start();
    }

    protected void PlayGame(String name1, String name2) {
        SetUpBoard(newRow);
        Player1Name = name1;
        Player2Name = name2;
        canvas = new DrawCanvas();
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        btnNewgame = new JButton("NewGame");
        btnNewgame.setForeground(Color.WHITE);
        btnNewgame.setBackground(new Color(59, 89, 182));
        Font btnFont = new Font(btnNewgame.getFont().getName(), Font.BOLD, 20);
        btnNewgame.setFont(btnFont);
        btnNewgame.setPreferredSize(new Dimension(140, 40));
        btnNewgame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showOptionDialog(null, "Bạn có muốn bắt đầu trò chơi mới không?", "New Game", JOptionPane.YES_NO_OPTION
                        , JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (result == JOptionPane.YES_OPTION) {
                    if (timer != null && timer.isRunning()) {
                        timer.stop();
                    }
                    initGame();
                    repaint();
                }
            }
        });

        btnExit = new JButton("Exit Game");
        btnExit.setForeground(Color.WHITE);
        btnExit.setBackground(new Color(59, 89, 182));
        btnExit.setFont(btnFont);
        btnExit.setPreferredSize(new Dimension(140, 40));
        btnExit.addActionListener(e -> {
            int confirmed = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to exit the game?", "Exit Game",
                    JOptionPane.YES_NO_OPTION);

            if (confirmed == JOptionPane.YES_OPTION) {
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
                JFrameMain.jFrame.setVisible(true);
                dispose();
            }
        });

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                int rowSelected = mouseY / CELL_SIZE;
                int colSelected = mouseX / CELL_SIZE;

                if (currentState == GameState.PLAYING) {
                    if (rowSelected >= 0 && rowSelected < ROWS && colSelected >= 0
                            && colSelected < COLS && board[rowSelected][colSelected] == Seed.EMPTY) {
                        rowPreSelected = rowSelected;
                        colPreSelected = colSelected;
                        board[rowSelected][colSelected] = currentPlayer;
                        updateGame(currentPlayer, rowSelected, colSelected);
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                        STEPS++;

                        resetTimer();
                        // Cập nhật statusBar sau khi chuyển lượt chơi
                        updateStatusBar();
                    }
                } else {
                    initGame();
                }
                repaint();
            }
        });

        statusBar = new JLabel("  ");
        statusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));

        pnButton = new JPanel();
        pnButton.setLayout(new FlowLayout(FlowLayout.CENTER));
        pnButton.add(btnNewgame);
        pnButton.add(btnExit);
        setUndecorated(true);

        // LEFT
        player1Label = new JLabel(name1, SwingConstants.CENTER);
        player1Label.setFont(new Font("Arial", Font.BOLD, 36));
        player1Label.setPreferredSize(new Dimension(200, CANVAS_HEIGHT));
        player1Label.setForeground(new Color(0, 128, 0)); // Xanh lá cây

        player1TimeLabel = new JLabel(timeLeft +"", SwingConstants.CENTER);
        player1TimeLabel.setFont(new Font("Arial", Font.PLAIN, 60));
        player1TimeLabel.setPreferredSize(new Dimension(200, CANVAS_HEIGHT));
        player1TimeLabel.setForeground(new Color(255, 0, 0)); // Đỏ

        player1ScoreLabel = new JLabel("Score: " + Player1Score, SwingConstants.CENTER);
        player1ScoreLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        player1ScoreLabel.setPreferredSize(new Dimension(200, CANVAS_HEIGHT));
        player1ScoreLabel.setForeground(new Color(0, 0, 128)); // Xanh dương

        player2Label = new JLabel(name2, SwingConstants.CENTER);
        player2Label.setFont(new Font("Arial", Font.BOLD, 36));
        player2Label.setPreferredSize(new Dimension(200, CANVAS_HEIGHT));
        player2Label.setForeground(new Color(255, 165, 0)); // Cam

        player2TimeLabel = new JLabel(timeLeft +"", SwingConstants.CENTER);
        player2TimeLabel.setFont(new Font("Arial", Font.PLAIN, 60));
        player2TimeLabel.setPreferredSize(new Dimension(200, CANVAS_HEIGHT));
        player2TimeLabel.setForeground(new Color(255, 0, 0)); // Đỏ

        player2ScoreLabel = new JLabel("Score: " + Player2Score, SwingConstants.CENTER);
        player2ScoreLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        player2ScoreLabel.setPreferredSize(new Dimension(200, CANVAS_HEIGHT));
        player2ScoreLabel.setForeground(new Color(0, 0, 128)); // Xanh dương

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

// Player 1 Panel
        JPanel player1Panel = new JPanel();
        player1Panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.fill = GridBagConstraints.HORIZONTAL;
        gbc1.anchor = GridBagConstraints.NORTH;
        gbc1.weightx = 1.0;
        gbc1.insets = new Insets(5, 5, 5, 5);

        gbc1.gridy = 0; // Name label
        player1Panel.add(player1Label, gbc1);

        gbc1.gridy = 1; // Score label
        player1Panel.add(player1ScoreLabel, gbc1);

        gbc1.gridy = 2; // Time label
        player1Panel.add(player1TimeLabel, gbc1);

        player1Panel.setPreferredSize(new Dimension(200, CANVAS_HEIGHT));
        player1Panel.setMinimumSize(new Dimension(200, CANVAS_HEIGHT));

// Player 2 Panel
        JPanel player2Panel = new JPanel();
        player2Panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.anchor = GridBagConstraints.NORTH;
        gbc2.weightx = 1.0;
        gbc2.insets = new Insets(5, 5, 5, 5);

        gbc2.gridy = 0; // Name label
        player2Panel.add(player2Label, gbc2);

        gbc2.gridy = 1; // Score label
        player2Panel.add(player2ScoreLabel, gbc2);

        gbc2.gridy = 2; // Time label
        player2Panel.add(player2TimeLabel, gbc2);

        player2Panel.setPreferredSize(new Dimension(200, CANVAS_HEIGHT));
        player2Panel.setMinimumSize(new Dimension(200, CANVAS_HEIGHT));

        cp.add(player1Panel, BorderLayout.WEST);
        cp.add(player2Panel, BorderLayout.EAST);
        cp.add(canvas, BorderLayout.CENTER);
        cp.add(statusBar, BorderLayout.PAGE_END);
        cp.add(pnButton, BorderLayout.PAGE_START);
        pack();
        setTitle("Chơi với bạn");
        setLocationRelativeTo(null);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Player1Score = 0;
                Player2Score = 0;
                savePlayerScores();
                JFrameMain.jFrame.setVisible(true);
                dispose();
            }
        });

        board = new Seed[ROWS][COLS];
        initGame();
    }

    public void initGame() {
        // Khởi tạo hoàn toàn trò chơi mới

        STEPS = 0;
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                board[row][col] = Seed.EMPTY;
            }
        }
        currentState = GameState.PLAYING;
        currentPlayer = Seed.CROSS;

        // Khởi động đếm ngược
        resetTimer();
        updateStatusBar();
        updatePlayerTimeLabels();
    }

    private void updateStatusBar() {
        if (currentState == GameState.PLAYING) {
            statusBar.setForeground(new Color(59, 89, 182));
            String currentPlayerName = (currentPlayer == Seed.CROSS) ? Player1Name : Player2Name;
            statusBar.setText("Lượt của " + currentPlayerName );
            // Hiển thị hoặc ẩn thời gian cho người chơi tương ứng
            if (currentPlayer == Seed.CROSS) {
                player1TimeLabel.setVisible(true);
                player2TimeLabel.setVisible(false);
            } else {
                player1TimeLabel.setVisible(false);
                player2TimeLabel.setVisible(true);
            }
        }
    }
    private void updatePlayerTimeLabels() {
        player1TimeLabel.setText(timeLeft + "");
        player2TimeLabel.setText(timeLeft + "");
    }

    private void updatePlayerScores() {
        player1ScoreLabel.setText("Score: " + Player1Score);
        player2ScoreLabel.setText("Score: " + Player2Score);
        repaint();
    }
    private void loadPlayerScores() {
        // Điều này có thể thực hiện thông qua tệp dữ liệu hoặc các cài đặt khác
        // Ở đây, tôi chỉ giả định rằng bạn đã lưu điểm số của hai người chơi trong savedPlayer1Score và savedPlayer2Score
        Player1Score = savedPlayer1Score;
        Player2Score = savedPlayer2Score;
        updatePlayerScores();
    }
    private void savePlayerScores() {
        // Lưu điểm số của hai người chơi vào savedPlayer1Score và savedPlayer2Score
        savedPlayer1Score = Player1Score;
        savedPlayer2Score = Player2Score;
        // Điều này có thể thực hiện thông qua tệp dữ liệu hoặc các cài đặt khác
    }




    private void handleTimeout() {
        // Dừng timer
        timer.stop();
        // Hiển thị thông báo và xử thua cho người chơi hiện tại
        String loserName = (currentPlayer == Seed.CROSS) ? Player1Name : Player2Name;
        JOptionPane.showMessageDialog(null, loserName + " hết thời gian! " + loserName + " thua! Click chuột để chơi lại");
        if (currentPlayer == Seed.CROSS) {
            Player2Score++; // Increment Player 2's score if Player 1 loses on timeout
        } else {
            Player1Score++; // Increment Player 1's score if Player 2 loses on timeout
        }

        updatePlayerScores(); // Update scores in the GUI
        currentState = (currentPlayer == Seed.CROSS) ? GameState.NOUGHT_WON : GameState.CROSS_WON;
        repaint();
    }
    // Trong phương thức updateGame của lớp Play2Players
    public void updateGame(Seed theSeed, int rowSelected, int colSelected) {
        if (hasWon(theSeed, rowSelected, colSelected)) {
            currentState = (theSeed == Seed.CROSS) ? GameState.CROSS_WON : GameState.NOUGHT_WON;
            String winnerName = (theSeed == Seed.CROSS) ? Player1Name : Player2Name;
            JOptionPane.showMessageDialog(null, winnerName + " thắng rồi! Click chuột để chơi lại");
            if (theSeed == Seed.CROSS) {
                Player1Score++; // Increment Player 1's score if they win
            } else {
                Player2Score++; // Increment Player 2's score if they win
            }
            updatePlayerScores(); // Update scores in the GUI
        } else if (isDraw()) {
            currentState = GameState.DRAW;
            JOptionPane.showMessageDialog(null, "Hòa rồi! Click chuột để chơi lại");
        }
    }


    public boolean isDraw() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (board[row][col] == Seed.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean hasWon(Seed theSeed, int rowSelected, int colSelected) {
        return (board[rowSelected][0] == theSeed
                && board[rowSelected][1] == theSeed
                && board[rowSelected][2] == theSeed
                || board[0][colSelected] == theSeed
                && board[1][colSelected] == theSeed
                && board[2][colSelected] == theSeed
                || rowSelected == colSelected
                && board[0][0] == theSeed
                && board[1][1] == theSeed
                && board[2][2] == theSeed
                || rowSelected + colSelected == 2
                && board[0][2] == theSeed
                && board[1][1] == theSeed
                && board[2][0] == theSeed);
    }

    class DrawCanvas extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.WHITE);
            g.setColor(new Color(149, 149, 161));

            for (int row = 1; row < ROWS; ++row) {
                g.fillRoundRect(0, CELL_SIZE * row - GRID_WIDHT_HALF,
                        CANVAS_WIDTH - 1, GRID_WIDTH, GRID_WIDTH, GRID_WIDTH);
            }
            for (int col = 1; col < COLS; ++col) {
                g.fillRoundRect(CELL_SIZE * col - GRID_WIDHT_HALF, 0,
                        GRID_WIDTH, CANVAS_HEIGHT - 1, GRID_WIDTH, GRID_WIDTH);
            }

            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    int x1 = col * CELL_SIZE + CELL_PADDING;
                    int y1 = row * CELL_SIZE + CELL_PADDING;
                    if (board[row][col] == Seed.CROSS) {
                        g2d.setColor(new Color(239, 28, 65));
                        int x2 = (col + 1) * CELL_SIZE - CELL_PADDING;
                        int y2 = (row + 1) * CELL_SIZE - CELL_PADDING;
                        g2d.drawLine(x1, y1, x2, y2);
                        g2d.drawLine(x2, y1, x1, y2);
                    } else if (board[row][col] == Seed.NOUGHT) {
                        g2d.setColor(new Color(8, 208, 208));
                        g2d.drawOval(x1, y1, SYMBOL_SIZE, SYMBOL_SIZE);
                    }
                }
            }
            if (currentState == GameState.PLAYING) {
                statusBar.setForeground(new Color(59, 89, 182));
                if (currentPlayer == Seed.CROSS) {
                    statusBar.setText("Lượt của " + Player1Name );
                } else {
                    statusBar.setText("Lượt của " + Player2Name );
                }
            }
            else if (currentState == GameState.DRAW) {
                statusBar.setForeground(new Color(220, 20, 60));
                statusBar.setText("Hòa! Click chuột để chơi lại.");
            } else if (currentState == GameState.CROSS_WON) {
                statusBar.setForeground(new Color(220, 20, 60));
                statusBar.setText(Player1Name + " (X) thắng! Click chuột để chơi lại.");
            } else if (currentState == GameState.NOUGHT_WON) {
                statusBar.setForeground(new Color(0, 128, 128));
                statusBar.setText(Player2Name + " (O) thắng! Click chuột để chơi lại.");
            }
        }
    }
}

