package com.caro.TicTacToe;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import static com.caro.JFrameMain.jFrame;

/**
 * Tic-Tac-Toe: Two-player Graphics version with Simple-OO
 */
@SuppressWarnings("serial")
public class Play2Players extends JFrame {
    public static int newRow =3;
    protected Seed PlayerReRun;
    public static int rowPreSelected = -1;
    public static int colPreSelected = -1;
    public static int rowPreDiLai;
    public static int colPreDiLai;
    // Named-constants for the game board
    public static  int ROWS = 3;  // ROWS by COLS cells
    public static  int COLS = 3;
    public static String Player1Name;
    public static String Player2Name;
    public static boolean Player1TwoMove;
    public static int STEPS=0;

    // Named-constants of the various dimensions used for graphics drawing
    public static  int CELL_SIZE = 100; // cell width and height (square)
    public static  int CANVAS_WIDTH = CELL_SIZE * COLS;  // the drawing canvas
    public static  int CANVAS_HEIGHT = CELL_SIZE * ROWS;
    public static  int GRID_WIDTH = 2;                   // Grid-line's width
    public static  int GRID_WIDHT_HALF = GRID_WIDTH / 2; // Grid-line's half-width
    // Symbols (cross/nought) are displayed inside a cell, with padding from border
    public static  int CELL_PADDING = CELL_SIZE / 6;
    public static  int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2; // width/height
    public static  int SYMBOL_STROKE_WIDTH = 8; // pen's stroke width
    private JPanel jhjh;
    private JList list1;
    private JEditorPane ụuuEditorPane;

    // Use an enumeration (inner class) to represent the various states of the game
    public enum GameState {
        PLAYING, DRAW, CROSS_WON, NOUGHT_WON
    }
    protected GameState currentState;  // the current game state

    // Use an enumeration (inner class) to represent the seeds and cell contents
    public enum Seed {
        EMPTY, CROSS, NOUGHT
    }
    protected Seed currentPlayer;  // the current player

    public Seed[][] board   ; // Game board of ROWS-by-COLS cells
    protected DrawCanvas canvas; // Drawing canvas (JPanel) for the game board
    protected JLabel statusBar;  // Status Bar
    protected JPanel pnButton;
    protected Button btnDiLai;
    protected Button btnBoDiLai;


    /** Constructor to setup the game and the GUI components */
    public Play2Players(String name1, String name2) {
        STEPS=0;
        PlayGame(name1,name2);
    }

    // Set up lại kích cỡ bàn cờ.
    public void SetUpBoard(int row){
        ROWS = row;
        COLS = row;
        if ( row <= 10 ) {
            CELL_SIZE = (20 / row) * 25;
            CANVAS_WIDTH = CELL_SIZE * COLS;
            CANVAS_HEIGHT = CELL_SIZE * ROWS;
            CELL_PADDING = CELL_SIZE / 6;
            SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2;
        }else {
            CELL_SIZE = (20 / row) * 45;
            CANVAS_WIDTH = CELL_SIZE * COLS;
            CANVAS_HEIGHT = CELL_SIZE * ROWS;
            CELL_PADDING = CELL_SIZE / 6;
            SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2;
        }
    }

    protected void PlayGame(String name1, String name2){
        SetUpBoard(newRow);
        Player1Name = name1;
        Player2Name = name2;
        canvas = new DrawCanvas();  // Construct a drawing canvas (a JPanel)
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGameMenuItem = new JMenuItem("Chơi ván mới");
        JMenuItem exitMenuItem = new JMenuItem("Thoát");
        Font menuFont = new Font("Arial", Font.PLAIN, 16); // Thiết lập kiểu chữ và kích thước mong muốn
        gameMenu.setFont(menuFont); // Thiết lập kiểu chữ và kích thước cho menu "Game"
        newGameMenuItem.setFont(menuFont); // Thiết lập kiểu chữ và kích thước cho mục "Chơi ván mới"
        exitMenuItem.setFont(menuFont); // Thiết lập kiểu chữ và kích thước cho mục "Thoát"

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {  // mouse-clicked handler
                int mouseX = e.getX();
                int mouseY = e.getY();
                // Get the row and column clicked
                int rowSelected = mouseY / CELL_SIZE;
                int colSelected = mouseX / CELL_SIZE;

                if (currentState == GameState.PLAYING) {
                    if (rowSelected >= 0 && rowSelected < ROWS && colSelected >= 0
                            && colSelected < COLS && board[rowSelected][colSelected] == Seed.EMPTY) {
                        rowPreSelected = rowSelected;
                        colPreSelected = colSelected;
                        board[rowSelected][colSelected] = currentPlayer; // Make a move
                        updateGame(currentPlayer, rowSelected, colSelected); // update state
                        // Switch player
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                        STEPS++;
                    }
                    btnDiLai.setEnabled(true);
                    btnBoDiLai.setEnabled(false);
                } else {       // game over
                    initGame(); // restart the game
                }
                // Refresh the drawing canvas
                repaint();  // Call-back paintComponent().
            }
        });

        // Setup the status bar (JLabel) to display status message
        statusBar = new JLabel("  ");
        statusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));

        //Thêm Button
        btnDiLai = new Button("Đi lại");
        btnDiLai.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
        btnDiLai.setEnabled(false);
        //btnDiLai.setSize(10,10);

        btnDiLai.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentState != GameState.PLAYING) return;
                if(STEPS==0) return;
                rowPreDiLai =rowPreSelected;
                colPreDiLai =colPreSelected;
                PlayerReRun = board[rowPreSelected][colPreSelected];
                currentPlayer = PlayerReRun;
                board[rowPreSelected][colPreSelected] = Seed.EMPTY;
                btnBoDiLai.setEnabled(true);
                btnDiLai.setEnabled(false);
                repaint();
            }
        });

        btnBoDiLai = new Button("Bỏ đi lại");
        btnBoDiLai.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
        btnBoDiLai.setEnabled(false);
        //btnDiLai.setSize(10,10);

        btnBoDiLai.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentState != GameState.PLAYING) return;
                if(STEPS == 0 || PlayerReRun == null ) return;
                board[rowPreDiLai][colPreDiLai] = PlayerReRun;
                currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                btnBoDiLai.setEnabled(false);
                repaint();
            }
        });

        newGameMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                initGame(); // Bắt đầu ván mới
                repaint(); // Vẽ lại bàn cờ
            }
        });

        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Thoát ứng dụng
            }
        });

        gameMenu.add(newGameMenuItem);
        gameMenu.add(exitMenuItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);

        pnButton = new JPanel();
        pnButton.setLayout(new FlowLayout(FlowLayout.CENTER));
        pnButton.add(btnDiLai);
        pnButton.add(btnBoDiLai);

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(canvas, BorderLayout.CENTER);
        cp.add(statusBar, BorderLayout.PAGE_END); // same as SOUTH
        cp.add(pnButton, BorderLayout.PAGE_START);

        pack();  // pack all the components in this JFrame
        setTitle("Tic Tac Toe 2 người");
        setLocationRelativeTo(null);
        setVisible(true);  // show this JFrame

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                jFrame.setVisible(true);
            }
        });

        board = new Seed[ROWS][COLS]; // allocate array
        initGame(); // initialize the game board contents and game variables
    }

    /** Initialize the game-board contents and the status */
    public void initGame() {
        btnDiLai.setEnabled(false);
        btnBoDiLai.setEnabled(false);
        STEPS = 0;
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                board[row][col] = Seed.EMPTY; // all cells empty
            }
        }
        currentState = GameState.PLAYING; // ready to play
        currentPlayer = Seed.CROSS;       // cross plays first
    }

    /** Update the currentState after the player with "theSeed" has placed on
     (rowSelected, colSelected). */
    public void updateGame(Seed theSeed, int rowSelected, int colSelected) {
        if (hasWon(theSeed, rowSelected, colSelected)) {  // check for win
            currentState = (theSeed == Seed.CROSS) ? GameState.CROSS_WON : GameState.NOUGHT_WON;

            if(theSeed == Seed.CROSS){

                JOptionPane.showMessageDialog(null,Player1Name+" thắng rồi! Click chuột để chơi lại");
            }
            else {
                JOptionPane.showMessageDialog(null, Player2Name + " thắng rồi! Click chuột để chơi lại");
                }
        } else if (isDraw()) {  // check for draw
            currentState = GameState.DRAW;
            JOptionPane.showMessageDialog(null,"Hòa rồi! Click chuột để chơi lại");
        }
        // Otherwise, no change to current state (still GameState.PLAYING).
    }

    /** Return true if it is a draw (i.e., no more empty cell) */
    public boolean isDraw() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (board[row][col] == Seed.EMPTY) {
                    return false; // an empty cell found, not draw, exit
                }
            }
        }
        return true;  // no more empty cell, it's a draw
    }

    /** Return true if the player with "theSeed" has won after placing at
     (rowSelected, colSelected) */
    public boolean hasWon(Seed theSeed, int rowSelected, int colSelected) {
        return (board[rowSelected][0] == theSeed  // 3-in-the-row
                && board[rowSelected][1] == theSeed
                && board[rowSelected][2] == theSeed
                || board[0][colSelected] == theSeed      // 3-in-the-column
                && board[1][colSelected] == theSeed
                && board[2][colSelected] == theSeed
                || rowSelected == colSelected            // 3-in-the-diagonal
                && board[0][0] == theSeed
                && board[1][1] == theSeed
                && board[2][2] == theSeed
                || rowSelected + colSelected == 2  // 3-in-the-opposite-diagonal
                && board[0][2] == theSeed
                && board[1][1] == theSeed
                && board[2][0] == theSeed);
    }
    /**
     *  Inner class DrawCanvas (extends JPanel) used for custom graphics drawing.
     */
    public class DrawCanvas extends JPanel {

        @Override
        public void paintComponent(Graphics g) {  // invoke via repaint()
            super.paintComponent(g);    // fill background
            setBackground(Color.WHITE); // set its background color

            // Draw the grid-lines
            g.setColor(Color.LIGHT_GRAY);
            for (int row = 1; row < ROWS; ++row) {
                g.fillRoundRect(0, CELL_SIZE * row - GRID_WIDHT_HALF,
                        CANVAS_WIDTH-1, GRID_WIDTH, GRID_WIDTH, GRID_WIDTH);
            }
            for (int col = 1; col < COLS; ++col) {
                g.fillRoundRect(CELL_SIZE * col - GRID_WIDHT_HALF, 0,
                        GRID_WIDTH, CANVAS_HEIGHT-1, GRID_WIDTH, GRID_WIDTH);
            }

            // Draw the Seeds of all the cells if they are not empty
            // Use Graphics2D which allows us to set the pen's stroke
            Graphics2D g2d = (Graphics2D)g;
            g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));  // Graphics2D only
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    int x1 = col * CELL_SIZE + CELL_PADDING;
                    int y1 = row * CELL_SIZE + CELL_PADDING;
                    if (board[row][col] == Seed.CROSS) {
                        g2d.setColor(Color.RED);
                        int x2 = (col + 1) * CELL_SIZE - CELL_PADDING;
                        int y2 = (row + 1) * CELL_SIZE - CELL_PADDING;
                        g2d.drawLine(x1, y1, x2, y2);
                        g2d.drawLine(x2, y1, x1, y2);
                    } else if (board[row][col] == Seed.NOUGHT) {
                        g2d.setColor(Color.BLUE);
                        g2d.drawOval(x1, y1, SYMBOL_SIZE, SYMBOL_SIZE);
                    }
                }
            }

            // Print status-bar message
            if (currentState == GameState.PLAYING) {
                statusBar.setForeground(Color.BLACK);
                if (currentPlayer == Seed.CROSS) {
                    statusBar.setText("Lượt của "+Player1Name);
                } else {
                    statusBar.setText("Lượt của "+Player2Name);
                }
            } else if (currentState == GameState.DRAW) {
                statusBar.setForeground(Color.RED);
                statusBar.setText("Hòa rồi! Click chuột để chơi lại");
            } else if (currentState == GameState.CROSS_WON) {
                statusBar.setForeground(Color.RED);
                statusBar.setText(Player1Name+" thắng rồi! Click chuột để chơi lại");
            } else if (currentState == GameState.NOUGHT_WON) {
                statusBar.setForeground(Color.RED);
                statusBar.setText(Player2Name+" thắng rồi! Click chuột để chơi lại");
            }
        }
    }
    protected boolean CheckEmptyBoard(){
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (board[row][col] != Seed.EMPTY) {
                    return false; // an empty cell found, not draw, exit
                }
            }
        }
        return true;
    }
}
