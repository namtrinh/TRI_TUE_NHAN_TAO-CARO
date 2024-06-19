package com.caro.TicTacToe;

import com.caro.EasyBot;
import com.caro.JFrameMain;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;


import static com.caro.JFrameMain.jFrame;

public class PlayWithAI extends Play2Players {
    public enum Bot {
        EASY_BOT, HEURISTIC_BOT
    }

    public static int rowBotPreSelected = -1;
    public static int colBotPreSelected = -1;

    protected JButton btnExit;
    public static Bot GameBot;


    protected JButton btnNewgame;
    private JLabel lblPlayer1, lblPlayer2; // Labels for player names

    private Timer timer;
    private int timeLeft; // Remaining time in seconds
    private JLabel lblTimer;


    public PlayWithAI(String name) {
        super(name, "");
        initTimer(); // Initialize the countdown timer
    }

    protected void PlayGame(String name1, String name2) {
        SetUpBoard(newRow);
        Player1Name = name1;
        Player2Name = "Máy";
        canvas = new DrawCanvas();
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        // Mouse event listener for canvas
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                int rowSelected = mouseY / CELL_SIZE;
                int colSelected = mouseX / CELL_SIZE;

                if (currentState == GameState.PLAYING) {
                    if (isMoveValid(rowSelected, colSelected)) {
                        processPlayerMove(rowSelected, colSelected);
                        processAIMove();
                    }
                } else {
                    initGame(); // restart the game
                }
                repaint();
            }
        });

        setupWindowListener();
        setupStatusBar();
        setupButtons();

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        // Create panels for player names
        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel leftInnerPanel = new JPanel(new GridLayout(2, 1, 0, 160)); // Use GridLayout to stack components vertically with 20 pixel gap

        JPanel rightPanel = new JPanel(new BorderLayout());
        JPanel rightInnerPanel = new JPanel(new GridLayout(2, 1, 0, 160)); // Use GridLayout to stack components vertically with 20 pixel gap
        lblPlayer1 = new JLabel(Player1Name, SwingConstants.CENTER);
        player1ScoreLabel = new JLabel("Score: " + Player1Score, SwingConstants.CENTER);

        lblPlayer2 = new JLabel(Player2Name, SwingConstants.CENTER);
        player2ScoreLabel = new JLabel("Score: " + Player2Score, SwingConstants.CENTER);

        lblPlayer1.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        lblPlayer2.setFont(new Font(Font.DIALOG, Font.BOLD, 20));

        // Player 1 Labels
        lblPlayer1.setFont(new Font("Arial", Font.BOLD, 24));
        lblPlayer1.setForeground(Color.BLUE);

        player1ScoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        player1ScoreLabel.setForeground(Color.DARK_GRAY);

// Player 2 Labels
        lblPlayer2.setFont(new Font("Arial", Font.BOLD, 24));
        lblPlayer2.setForeground(Color.RED);

        player2ScoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        player2ScoreLabel.setForeground(Color.DARK_GRAY);


        leftInnerPanel.add(lblPlayer1); // Add name label
        leftInnerPanel.add(player1ScoreLabel); // Add score label
        rightInnerPanel.add(lblPlayer2); // Add name label
        rightInnerPanel.add(player2ScoreLabel); // Add score label
        setUndecorated(true);
        leftPanel.add(leftInnerPanel, BorderLayout.PAGE_START); // Add the inner panel to the center of the left panel
        rightPanel.add(rightInnerPanel, BorderLayout.PAGE_START); // Add the inner panel to the center of the right panel

// Set preferred size for panels
        leftPanel.setPreferredSize(new Dimension(100, CANVAS_HEIGHT));
        rightPanel.setPreferredSize(new Dimension(100, CANVAS_HEIGHT));

// Set preferred size for panels
        leftPanel.setPreferredSize(new Dimension(100, CANVAS_HEIGHT));
        rightPanel.setPreferredSize(new Dimension(100, CANVAS_HEIGHT));


        cp.add(leftPanel, BorderLayout.WEST);
        cp.add(canvas, BorderLayout.CENTER);
        cp.add(rightPanel, BorderLayout.EAST);
        cp.add(statusBar, BorderLayout.PAGE_END);
        cp.add(pnButton, BorderLayout.PAGE_START);

        pack();
        setTitle("Chơi với Máy");
        setLocationRelativeTo(null);
        setVisible(true);

        board = new Seed[ROWS][COLS];
        initGame();
    }

    private boolean isMoveValid(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS && board[row][col] == Seed.EMPTY;
    }

    private void processPlayerMove(int row, int col) {
        rowPreSelected = row;
        colPreSelected = col;
        board[row][col] = currentPlayer;
        updateGame(currentPlayer, row, col);
        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
        STEPS++;
        resetTimer();
        resetAndStartTimer();
        if (currentState != GameState.PLAYING) {
            stopTimer(); // Stop the timer if the game is over
        } else {
            resetTimer();
            startTimer(); // Start the timer for the next player's turn
            processAIMove(); // Process AI move after resetting the timer
        }
    }

    private void processAIMove() {
        if (currentPlayer == Seed.NOUGHT && currentState == GameState.PLAYING) {
            int rowSelected = -1;
            int colSelected = -1;
            if (GameBot == Bot.EASY_BOT) {
                EasyBot botRun = new EasyBot();
                String[] move = botRun.getPosFrBrd(board).split(" ");
                rowSelected = Integer.parseInt(move[0]);
                colSelected = Integer.parseInt(move[1]);
            } else if (GameBot == Bot.HEURISTIC_BOT) {
                HeuristicBot botRun = new HeuristicBot(ROWS, COLS, Seed.NOUGHT, Seed.CROSS);
                String[] move = botRun.getPoint(board).split(" ");
                if (!move[0].isEmpty()) {
                    rowSelected = Integer.parseInt(move[0]);
                }
                if (move.length > 1) {
                    colSelected = Integer.parseInt(move[1]);
                }
            }

            if (isMoveValid(rowSelected, colSelected)) {
                rowBotPreSelected = rowSelected;
                colBotPreSelected = colSelected;
                board[rowSelected][colSelected] = currentPlayer;
                updateGame(currentPlayer, rowSelected, colSelected);
                currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                if (currentState != GameState.PLAYING) {
                    stopTimer(); // Stop the timer if the game is over
                } else {
                    resetAndStartTimer(); // Reset and start the timer for the next player's turn
                }
            }
        }
    }

    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                jFrame.setVisible(true);
            }
        });
    }

    private void setupStatusBar() {
        statusBar = new JLabel("  ");
        statusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));
    }

    private void setupButtons() {
        btnNewgame = new JButton("NewGame");
        btnNewgame.setForeground(Color.WHITE);
        btnNewgame.setBackground(new Color(59, 89, 182));

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

        // Timer label
        lblTimer = new JLabel("Time left: " + timeLeft + " seconds", SwingConstants.CENTER);
        lblTimer.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
        lblTimer.setForeground(Color.RED);

        btnExit = new JButton("Exit Game");
        btnExit.setForeground(Color.WHITE);
        btnExit.setBackground(new Color(59, 89, 182));
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirmed = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to exit the game?", "Exit Game",
                        JOptionPane.YES_NO_OPTION);

                if (confirmed == JOptionPane.YES_OPTION) {

                    stopTimer();

                    resetTimerr();
                    dispose();
                    JFrameMain.jFrame.setVisible(true);
                    // Optionally, you might want to reset the game state
                    initGame();
                    repaint();
                }
            }
        });


        pnButton = new JPanel();
        pnButton.setLayout(new GridLayout(1, 2));
        pnButton.add(btnNewgame);
        pnButton.add(lblTimer);
        pnButton.add(btnExit);
    }

    private void initTimer() {
        timeLeft = 30; // Initial time left is 10 seconds
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeLeft > 0) {
                    timeLeft--;
                    lblTimer.setText("Time left: " + timeLeft + " seconds");
                } else {

                    stopTimer(); // Stop the timer when time runs out
                    processAIMove(); // Proceed with AI move when time runs out
                    resetTimer();
                }
            }
        });
    }

    private void startTimer() {
        if (timer != null && !timer.isRunning()) {
            timer.start();
        }
    }

    private void stopTimer() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    public void resetTimerr() {
        timeLeft = 30; // Reset time to initial value
        lblTimer.setText("Time left: " + timeLeft + " seconds");
    }

    private void resetAndStartTimer() {
        stopTimer(); // Stop the timer if it's running
        timeLeft = 30; // Reset time to the initial value
        lblTimer.setText("Time left: " + timeLeft + " seconds"); // Update the timer label
        startTimer(); // Start the timer
    }

    // Bot đi theo thuật toán Heuristic

    public static class HeuristicBot {
        static int dong, cot;
        static Seed bot;
        static Seed player;
        static int[] mangTC = new int[]{0, 200, 10000, 30000, 80000000, 140000, 2000000};
        static int[] mangPN = new int[]{0, 7, 700, 10000, 100000, 67000, 500000};
        //2 mảng tc pt lưu giá trị tương ứng với những trường hợp trong trò chơi
        static long MAX_INT = 100000000;
        static int MAX_DEPTH = 2;

        public HeuristicBot(int dongg, int cott, Seed Bott, Seed Playerr) {
            dong = dongg;
            cot = cott;
            bot = Bott;
            player = Playerr;
        }

        public static boolean isAttackingMove(int i, int j, Seed[][] board, Seed type) {
            // Kiểm tra xem nước đi này có giúp kéo dài chuỗi tấn công hay không
            return CheckNgang(j, i, board, type) > 0 ||
                    CheckDoc(j, i, board, type) > 0 ||
                    CheckCheoPhai(i, j, board, type) > 0 ||
                    CheckCheoTrai(i, j, board, type) > 0;
        }

        //  nhận vào một bảng trạng thái của trò chơi và trả về tọa độ của nước đi tốt nhất dựa trên thuật toán minimax và hàm đánh giá.
        public static String getPoint(Seed[][] board) {
            long bestScore = -MAX_INT;
            String bestMove = "";
            List<String> moves = getPossibleMoves(board);
            for (String move : moves) {
                int i = Integer.parseInt(move.split(" ")[0]);
                int j = Integer.parseInt(move.split(" ")[1]);
                if (isValid(i, j, board)) {
                    board[i][j] = bot;
                    long score = minimax(board, 0, false, -MAX_INT, MAX_INT);
                    board[i][j] = Seed.EMPTY;

                    if (isAttackingMove(i, j, board, bot)) {
                        score += 10000; // Tăng trọng số cho các nước đi tấn công
                    }

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = move;
                    }
                }
            }
            return bestMove;
        }

        //Trả về một danh sách các nước đi có thể thực hiện trên bảng trạng thái hiện tại.
        public static List<String> getPossibleMoves(Seed[][] board) {
            List<String> possibleMoves = new ArrayList<>();
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    if (board[i][j] == Seed.EMPTY && hasNeighborMove(i, j, board)) {
                        possibleMoves.add(i + " " + j);
                    }
                }
            }
            return possibleMoves;
        }

        public static boolean hasNeighborMove(int row, int col, Seed[][] board) {
            // Kiểm tra các ô xung quanh ô hiện tại để xác định xem có nước đi nào xung quanh không
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                if (isValid(newRow, newCol, board) && board[newRow][newCol] != Seed.EMPTY) {
                    return true;
                }
            }
            return false;
        }

        public static boolean isValid(int row, int col, Seed[][] board) {
            return row >= 0 && row < board.length && col >= 0 && col < board[0].length;
        }

        public static long minimax(Seed[][] board, int depth, boolean isMaximizing, long alpha, long beta) {
            long boardVal = evaluateBoard(board);
            if (Math.abs(boardVal) >= MAX_INT || depth == MAX_DEPTH) {
                return boardVal;
            }
            if (isMaximizing) {
                long maxEval = -MAX_INT;
                List<String> moves = getPossibleMoves(board);
                for (String move : moves) {
                    int i = Integer.parseInt(move.split(" ")[0]);
                    int j = Integer.parseInt(move.split(" ")[1]);
                    if (isValid(i, j, board)) {
                        board[i][j] = bot;
                        long eval = minimax(board, depth + 1, false, alpha, beta);
                        board[i][j] = Seed.EMPTY;
                        maxEval = Math.max(maxEval, eval);
                        alpha = Math.max(alpha, eval);
                        if (beta <= alpha) break;
                    }
                }
                return maxEval;
            } else {
                long minEval = MAX_INT;
                List<String> moves = getPossibleMoves(board);
                for (String move : moves) {
                    int i = Integer.parseInt(move.split(" ")[0]);
                    int j = Integer.parseInt(move.split(" ")[1]);
                    if (isValid(i, j, board)) {
                        board[i][j] = player;
                        long eval = minimax(board, depth + 1, true, alpha, beta);
                        board[i][j] = Seed.EMPTY;
                        minEval = Math.min(minEval, eval);
                        beta = Math.min(beta, eval);
                        if (beta <= alpha) break;
                    }
                }
                return minEval;
            }

        }

        // Hàm đánh giá giá trị của bảng trạng thái.dựa trên các quân cờ hiện có trên bàn cờ
        public static long evaluateBoard(Seed[][] board) {
            long Score = 0;

            for (int i = 0; i < dong; i++) {
                for (int j = 0; j < cot; j++) {
                    if (board[i][j] == bot) {
                        Score += evaluatePosition(i, j, board, bot);
                    } else if (board[i][j] == player) {
                        Score -= evaluatePosition(i, j, board, player);
                    }
                }
            }
            return Score;
        }


        //Đánh giá giá trị của một vị trí trên bảng dựa trên loại quân cờ và vị trí đó.
        public static long evaluatePosition(int i, int j, Seed[][] board, Seed type) {
            long checkTC = CheckDoc(j, i, board, bot) + CheckNgang(j, i, board, bot) + CheckCheoPhai(i, j, board, bot) + CheckCheoTrai(i, j, board, bot);
            long checkPT = PTDoc(j, i, board, player) + PTNgang(j, i, board, player) + PTPhai(i, j, board, player) + PTTrai(i, j, board, player);
            return checkTC + checkPT;
        }

        public static long CheckNgang(int pos, int rowNow, Seed[][] board, Seed type) {
            int ta = 0;
            int count = 0;
            int dich = 0;
            boolean flag = false;
            for (int i = pos + 1; i < dong; i++) {
                if (board[rowNow][i] == type) ta++;
                else if (board[rowNow][i] == Seed.EMPTY && flag == false) {
                    count++;
                    flag = true;
                    break;
                } else if (board[rowNow][i] == player) {
                    dich++;
                    break;
                } else {
                    break;
                }
            }
            flag = false;
            for (int i = pos - 1; i >= 0; i--) {
                if (board[rowNow][i] == type) ta++;
                else if (board[rowNow][i] == Seed.EMPTY && flag == false) {
                    count++;
                    flag = true;
                    break;
                } else if (board[rowNow][i] == player) {
                    dich++;
                    break;
                } else {
                    break;
                }
            }
            if (ta == 0) return 0;
            if (ta >= 4) return MAX_INT;
            long score = (mangTC[ta] * 3);
            if (ta >= 2 && count == 1 && dich == 0) {
                score += (mangTC[ta] * 2);
            }
            return score;
        }

        public static long CheckDoc(int colNow, int pos, Seed[][] board, Seed type) {
            int ta = 0;
            int count = 0;
            int dich = 0;
            boolean flag = false;
            for (int i = pos + 1; i < dong; i++) {
                if (board[i][colNow] == type) ta++;
                else if (board[i][colNow] == Seed.EMPTY && flag == false) {
                    count++;
                    flag = true;
                    break;
                } else if (board[i][colNow] == player) {
                    dich++;
                    break;
                } else {
                    break;
                }
            }
            flag = false;
            for (int i = pos - 1; i >= 0; i--) {
                if (board[i][colNow] == type) ta++;
                else if (board[i][colNow] == Seed.EMPTY && flag == false) {
                    count++;
                    flag = true;
                    break;
                } else if (board[i][colNow] == player) {
                    dich++;
                    break;
                } else {
                    break;
                }
            }
            if (ta == 0) return 0;
            if (ta >= 4) return MAX_INT;
            long score = (mangTC[ta] * 3);

            if (ta >= 2 && count == 1 && dich == 0) {
                score += (mangTC[ta] * 2);
            }
            return score;
        }

        // \ \ \ \ \ \ [n++][n++]
        //pos_col => dòng hiện tại, pos_row => cột hiện tại
        public static long CheckCheoPhai(int pos_col, int pos_row, Seed[][] board, Seed type) {
            int ta = 0;
            int count = 0;
            int dich = 0;
            boolean flag = false;
            int i = pos_col;
            int ii = pos_row;
            //check xuống
            while (i + 1 < dong && ii + 1 < dong) {
                if (board[i + 1][ii + 1] == type) ta++;
                else if (board[i + 1][ii + 1] == Seed.EMPTY && flag == false) {
                    count++;
                    flag = true;
                    break;
                } else if (board[i + 1][ii + 1] == player) {
                    dich++;
                    break;
                } else {
                    break;
                }
                i = i + 1;
                ii = ii + 1;
            }
            i = pos_col;
            ii = pos_row;
            flag = false;
            //check lên
            while (i - 1 >= 0 && ii - 1 >= 0) {
                if (board[i - 1][ii - 1] == type) ta++;
                else if (board[i - 1][ii - 1] == Seed.EMPTY && flag == false) {
                    count++;
                    flag = true;
                    break;
                } else if (board[i - 1][ii - 1] == player) {
                    dich++;
                    break;
                } else {
                    break;
                }
                i = i - 1;
                ii = ii - 1;
            }
            if (ta == 0) return 0;
            if (ta >= 4) return MAX_INT;
            long score = (mangTC[ta] * 3);

            if (ta >= 2 && count == 1 && dich == 0) {
                score += (mangTC[ta] * 2);
            }
            return score;
        }

        //he
        // pos_col => dòng hiện tại, pos_row => cột hiện tại
        public static long CheckCheoTrai(int pos_col, int pos_row, Seed[][] board, Seed type) {
            int ta = 0;
            int count = 0;
            int dich = 0;
            boolean flag = false;
            int i = pos_col;
            int ii = pos_row;
            //check xuống
            while (i + 1 < dong && ii - 1 >= 0) {
                if (board[i + 1][ii - 1] == type) ta++;
                else if (board[i + 1][ii - 1] == Seed.EMPTY && flag == false) {
                    count++;
                    flag = true;
                    break;
                } else if (board[i + 1][ii - 1] == player) {
                    dich++;
                    break;
                } else {
                    break;
                }
                i = i + 1;
                ii = ii - 1;
            }
            flag = false;
            i = pos_col;
            ii = pos_row;
            //check lên
            while (i - 1 >= 0 && ii + 1 < dong) {
                if (board[i - 1][ii + 1] == type) ta++;
                else if (board[i - 1][ii + 1] == Seed.EMPTY && flag == false) {
                    count++;
                    flag = true;
                    break;
                } else if (board[i - 1][ii + 1] == player) {
                    dich++;
                    break;
                } else {
                    break;
                }
                i = i - 1;
                ii = ii + 1;
            }
            if (ta == 0) return 0;
            if (ta >= 4) return MAX_INT;
            long score = (mangTC[ta] * 3);

            if (ta >= 2 && count == 1 && dich == 0) {
                score += (mangTC[ta] * 2);
            }
            return score;
        }

        public static long PTNgang(int pos, int rowNow, Seed[][] board, Seed type) {
            int ta = 0;
            int count = 1;
            int dich = 0;
            for (int i = pos + 1; i < dong; i++) {
                if (board[rowNow][i] == type) ta++;
                else if (board[rowNow][i] == Seed.EMPTY) break;
                else if (board[rowNow][i] == bot) {
                    dich++;
                    break;
                } else {
                    break;
                }
            }
            for (int i = pos - 1; i >= 0; i--) {
                if (board[rowNow][i] == type) ta++;
                else if (board[rowNow][i] == Seed.EMPTY) break;
                else if (board[rowNow][i] == bot) {
                    dich++;
                    break;
                } else {
                    break;
                }
            }
            if (ta == 0) return 0;
            if (ta >= 4) return MAX_INT;
            long score = (mangPN[ta] * 100);

            if (ta >= 2 && count == 1 && dich == 0) {
                score += (mangPN[ta] * 2);
            }
            return score;
        }

        //^ ^ ^ ^ or | | | | [n++][i]
        public static long PTDoc(int colNow, int pos, Seed[][] board, Seed type) {
            int ta = 0;
            int count = 1;
            int dich = 0;
            for (int i = pos + 1; i < dong; i++) {
                if (board[i][colNow] == type) ta++;
                else if (board[i][colNow] == Seed.EMPTY) break;
                else if (board[i][colNow] == bot) {
                    dich++;
                    break;
                } else {
                    break;
                }
            }
            for (int i = pos - 1; i >= 0; i--) {
                if (board[i][colNow] == type) ta++;
                else if (board[i][colNow] == Seed.EMPTY) break;
                else if (board[i][colNow] == bot) {
                    dich++;
                    break;
                } else {
                    break;
                }
            }
            if (ta == 0) return 0;
            if (ta >= 4) return MAX_INT;
            long score = (mangPN[ta] * 100);

            if (ta >= 2 && count == 1 && dich == 0) {
                score += (mangPN[ta] * 2);
            }
            return score;
        }

        // \ \ \ \ \ \ [n++][n++]
        public static long PTPhai(int pos_col, int pos_row, Seed[][] board, Seed type) {
            int ta = 0;
            int count = 1;
            int dich = 0;
            int i = pos_col;
            int ii = pos_row;
            //check xuống
            while (i + 1 < dong && ii + 1 < dong) {
                if (board[i + 1][ii + 1] == type) ta++;
                else if (board[i + 1][ii + 1] == Seed.EMPTY) break;
                else {
                    dich++;
                    break;
                }
                i = i + 1;
                ii = ii + 1;
            }
            i = pos_col;
            ii = pos_row;
            //check lên
            while (i - 1 >= 0 && ii - 1 >= 0) {
                if (board[i - 1][ii - 1] == type) ta++;
                else if (board[i - 1][ii - 1] == Seed.EMPTY) break;
                else {
                    dich++;
                    break;
                }
                i = i - 1;
                ii = ii - 1;
            }
            if (ta == 0) return 0;
            if (ta >= 4) return MAX_INT;
            long score = (mangPN[ta] * 100);

            if (ta >= 2 && count == 1 && dich == 0) {
                score += (mangPN[ta] * 2);
            }
            return score;
        }

        public static long PTTrai(int pos_col, int pos_row, Seed[][] board, Seed type) {
            int ta = 0;
            int count = 1;
            int dich = 0;
            int i = pos_col;
            int ii = pos_row;
            //check xuống
            while (i + 1 < dong && ii - 1 >= 0) {
                if (board[i + 1][ii - 1] == type) ta++;
                else if (board[i + 1][ii - 1] == Seed.EMPTY) break;
                else {
                    dich++;
                    break;
                }
                i = i + 1;
                ii = ii - 1;
            }
            i = pos_col;
            ii = pos_row;
            //check lên
            while (i - 1 >= 0 && ii + 1 < dong) {
                if (board[i - 1][ii + 1] == type) ta++;
                else if (board[i - 1][ii + 1] == Seed.EMPTY) break;
                else {
                    dich++;
                    break;
                }
                i = i - 1;
                ii = ii + 1;
            }
            if (ta == 0) return 0;
            if (ta >= 4) return MAX_INT;
            long score = (mangPN[ta] * 100);

            if (ta >= 2 && count == 1 && dich == 0) {
                score += (mangPN[ta] * 2);
            }
            return score;
        }
    }

}

