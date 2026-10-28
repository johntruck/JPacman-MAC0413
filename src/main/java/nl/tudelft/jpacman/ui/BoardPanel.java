package nl.tudelft.jpacman.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.GateSquare;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.SwitchSquare;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.game.Game;

/**
 * Panel displaying a game.
 *
 * @author Jeroen Roosen 
 *
 */
class BoardPanel extends JPanel {

    /**
     * Default serialisation ID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The background colour of the board.
     */
    private static final Color BACKGROUND_COLOR = Color.BLACK;

    /**
     * The size (in pixels) of a square on the board. The initial size of this
     * panel will scale to fit a board with square of this size.
     */
    private static final int SQUARE_SIZE = 16;

    /**
     * Height reserved for the circuit legend below the board.
     */
    private static final int LEGEND_HEIGHT = 30;

    private static final int LEGEND_TEXT_SIZE = 9;
    private static final int LEGEND_MARGIN = 4;
    private static final int LEGEND_FIRST_BASELINE = 11;
    private static final int LEGEND_SECOND_BASELINE = 23;
    private static final int GATE_LABEL_FONT_SIZE = 10;
    private static final int LABEL_RIGHT_OFFSET = 7;
    private static final int LABEL_BOTTOM_OFFSET = 3;
    private static final int PENDING_MARK_X_OFFSET = 2;
    private static final int PENDING_MARK_BASELINE = 10;

    /**
     * The game to display.
     */
    private final Game game;

    /**
     * Creates a new board panel that will display the provided game.
     *
     * @param game
     *            The game to display.
     */
    BoardPanel(Game game) {
        super();
        assert game != null;
        this.game = game;

        Board board = game.getLevel().getBoard();

        int w = board.getWidth() * SQUARE_SIZE;
        int h = board.getHeight() * SQUARE_SIZE + LEGEND_HEIGHT;

        Dimension size = new Dimension(w, h);
        setMinimumSize(size);
        setPreferredSize(size);
    }

    @Override
    public void paint(Graphics g) {
        assert g != null;
        render(game.getLevel().getBoard(), g, getSize());
    }

    /**
     * Renders the board on the given graphics context to the given dimensions.
     *
     * @param board
     *            The board to render.
     * @param graphics
     *            The graphics context to draw on.
     * @param window
     *            The dimensions to scale the rendered board to.
     */
    private void render(Board board, Graphics graphics, Dimension window) {
        int boardHeight = Math.max(1, window.height - LEGEND_HEIGHT);
        int cellW = window.width / board.getWidth();
        int cellH = boardHeight / board.getHeight();

        graphics.setColor(BACKGROUND_COLOR);
        graphics.fillRect(0, 0, window.width, window.height);

        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                int cellX = x * cellW;
                int cellY = y * cellH;
                Square square = board.squareAt(x, y);
                render(square, graphics, cellX, cellY, cellW, cellH);
            }
        }
        renderLegend(graphics, boardHeight);
    }

    /**
     * Draws a legend for switches and the three gate states.
     *
     * @param graphics The graphics context.
     * @param y The first pixel below the board.
     */
    private void renderLegend(Graphics graphics, int y) {
        Font original = graphics.getFont();
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, LEGEND_TEXT_SIZE));
        graphics.drawString("Key + number: circuit 1 or 2",
            LEGEND_MARGIN, y + LEGEND_FIRST_BASELINE);
        graphics.drawString("Gate: floor=open, wall=closed, ~=pending closure",
            LEGEND_MARGIN, y + LEGEND_SECOND_BASELINE);
        graphics.setFont(original);
    }

    /**
     * Renders a single square on the given graphics context on the specified
     * rectangle.
     *
     * @param square
     *            The square to render.
     * @param graphics
     *            The graphics context to draw on.
     * @param x
     *            The x position to start drawing.
     * @param y
     *            The y position to start drawing.
     * @param width
     *            The width of this square (in pixels.)
     * @param height
     *            The height of this square (in pixels.)
     */
    private void render(Square square, Graphics graphics, int x, int y, int width, int height) {
        if (square instanceof SwitchSquare) {
            SwitchSquare switchSquare = (SwitchSquare) square;
            switchSquare.getBackgroundSprite().draw(graphics, x, y, width, height);
            switchSquare.getSwitchSprite().draw(graphics, x, y, width, height);
        } else {
            square.getSprite().draw(graphics, x, y, width, height);
        }
        for (Unit unit : square.getOccupants()) {
            unit.getSprite().draw(graphics, x, y, width, height);
        }

        if (square instanceof GateSquare) {
            GateSquare gate = (GateSquare) square;
            drawCircuitNumber(graphics, gate.getCircuitId(), x, y, width, height);
            if (gate.isPendingClosure()) {
                graphics.setColor(Color.WHITE);
                graphics.drawString("~", x + PENDING_MARK_X_OFFSET,
                    y + Math.min(height - 2, PENDING_MARK_BASELINE));
            }
        } else if (square instanceof SwitchSquare) {
            SwitchSquare switchSquare = (SwitchSquare) square;
            drawCircuitNumber(graphics, switchSquare.getCircuitId(), x, y, width, height);
        }
    }

    /**
     * Draws the circuit number over a gate or switch.
     *
     * @param graphics The graphics context.
     * @param circuitId The circuit number.
     * @param x The horizontal position.
     * @param y The vertical position.
     * @param width The square width.
     * @param height The square height.
     */
    private void drawCircuitNumber(Graphics graphics, int circuitId, int x, int y,
                                   int width, int height) {
        Font original = graphics.getFont();
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, GATE_LABEL_FONT_SIZE));
        graphics.setColor(Color.WHITE);
        graphics.drawString(Integer.toString(circuitId), x + width - LABEL_RIGHT_OFFSET,
            y + height - LABEL_BOTTOM_OFFSET);
        graphics.setFont(original);
    }
}
