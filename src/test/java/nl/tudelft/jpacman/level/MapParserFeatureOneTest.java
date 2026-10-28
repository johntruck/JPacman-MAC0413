package nl.tudelft.jpacman.level;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import nl.tudelft.jpacman.PacmanConfigurationException;
import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.GateSquare;
import nl.tudelft.jpacman.board.SwitchSquare;
import nl.tudelft.jpacman.npc.Ghost;
import nl.tudelft.jpacman.npc.ghost.GhostFactory;
import nl.tudelft.jpacman.npc.ghost.Navigation;
import nl.tudelft.jpacman.points.DefaultPointCalculator;
import nl.tudelft.jpacman.sprite.PacManSprites;

/**
 * Integration tests for maps, player switch activation, and collision behavior.
 */
class MapParserFeatureOneTest {

    @Test
    void playerTogglesOnlyTheCircuitOfTheSwitchAndStateSurvivesPause() {
        PacManSprites sprites = new PacManSprites();
        Level level = createParser(sprites).parseMap(Arrays.asList(
            "########",
            "#P1a   #",
            "#2b    #",
            "########"));
        Player player = new PlayerFactory(sprites).createPacMan();
        Board board = level.getBoard();
        GateSquare gateOne = (GateSquare) board.squareAt(3, 1);
        GateSquare gateTwo = (GateSquare) board.squareAt(2, 2);

        level.registerPlayer(player);
        level.start();
        try {
            level.move(player, Direction.EAST);
            assertThat(gateOne.getState()).isEqualTo(GateSquare.State.OPEN);
            assertThat(gateTwo.getState()).isEqualTo(GateSquare.State.CLOSED);

            level.stop();
            level.start();
            assertThat(gateOne.getState()).isEqualTo(GateSquare.State.OPEN);

            level.move(player, Direction.EAST);
            assertThat(player.getSquare()).isSameAs(gateOne);
            level.move(player, Direction.WEST);
            assertThat(gateOne.getState()).isEqualTo(GateSquare.State.CLOSED);
        } finally {
            level.stop();
        }
    }

    @Test
    void blockedMoveDoesNotActivateSwitch() {
        PacManSprites sprites = new PacManSprites();
        Level level = createParser(sprites).parseMap(Arrays.asList(
            "########",
            "#P1a   #",
            "#2b    #",
            "########"));
        Player player = new PlayerFactory(sprites).createPacMan();
        GateSquare gateOne = (GateSquare) level.getBoard().squareAt(3, 1);

        level.registerPlayer(player);
        level.start();
        try {
            level.move(player, Direction.WEST);
            assertThat(gateOne.getState()).isEqualTo(GateSquare.State.CLOSED);
            assertThat(((SwitchSquare) level.getBoard().squareAt(2, 1))
                .getCircuitId()).isEqualTo(1);
        } finally {
            level.stop();
        }
    }

    @Test
    void ghostDoesNotActivateSwitchAndFatalCollisionDoesNotToggleIt() {
        PacManSprites sprites = new PacManSprites();
        Level parsed = createParser(sprites).parseMap(Arrays.asList(
            "#######",
            "#P1G a#",
            "#######"));
        Board board = parsed.getBoard();
        Ghost ghost = Navigation.findUnitInBoard(Ghost.class, board);
        Player player = new PlayerFactory(sprites).createPacMan();
        GateSquare gate = (GateSquare) board.squareAt(5, 1);
        SwitchSquare circuitSwitch = (SwitchSquare) board.squareAt(2, 1);

        Level level = new Level(board, Collections.<Ghost>emptyList(),
            Collections.singletonList(board.squareAt(1, 1)),
            new PlayerCollisions(new DefaultPointCalculator()));
        level.registerPlayer(player);
        level.start();
        try {
            level.move(ghost, Direction.WEST);
            assertThat(ghost.getSquare()).isSameAs(circuitSwitch);
            assertThat(gate.getState()).isEqualTo(GateSquare.State.CLOSED);

            level.move(player, Direction.EAST);
            assertThat(player.isAlive()).isFalse();
            assertThat(player.getKiller()).isSameAs(ghost);
            assertThat(gate.getState()).isEqualTo(GateSquare.State.CLOSED);
        } finally {
            level.stop();
        }
    }

    @Test
    void ghostCanLeavePendingGateButCannotEnterItAgain() {
        PacManSprites sprites = new PacManSprites();
        Level parsed = createParser(sprites).parseMap(Arrays.asList(
            "########",
            "#P1aG  #",
            "#      #",
            "########"));
        Board board = parsed.getBoard();
        Ghost ghost = Navigation.findUnitInBoard(Ghost.class, board);
        Player player = new PlayerFactory(sprites).createPacMan();
        GateSquare gate = (GateSquare) board.squareAt(3, 1);

        Level level = new Level(board, Collections.<Ghost>emptyList(),
            Collections.singletonList(board.squareAt(1, 1)),
            new PlayerCollisions(new DefaultPointCalculator()));
        level.registerPlayer(player);
        level.start();
        try {
            level.move(player, Direction.EAST);
            assertThat(gate.getState()).isEqualTo(GateSquare.State.OPEN);

            level.move(ghost, Direction.WEST);
            assertThat(ghost.getSquare()).isSameAs(gate);

            level.move(player, Direction.WEST);
            level.move(player, Direction.EAST);
            assertThat(gate.getState()).isEqualTo(GateSquare.State.PENDING_CLOSURE);
            assertThat(gate.getOccupants()).contains(ghost);

            level.move(ghost, Direction.EAST);
            assertThat(ghost.getSquare()).isSameAs(board.squareAt(4, 1));
            assertThat(gate.getState()).isEqualTo(GateSquare.State.CLOSED);
            assertThat(gate.getOccupants()).doesNotContain(ghost);

            level.move(ghost, Direction.WEST);
            assertThat(ghost.getSquare()).isSameAs(board.squareAt(4, 1));
            assertThat(gate.getOccupants()).doesNotContain(ghost);
        } finally {
            level.stop();
        }
    }

    @Test
    void rejectsAnIncompleteCircuitWithAnActionableMessage() {
        MapParser parser = createParser(new PacManSprites());

        assertThatThrownBy(() -> parser.parseMap(Arrays.asList(
            "#####",
            "#P1 #",
            "#####")))
            .isInstanceOf(PacmanConfigurationException.class)
            .hasMessageContaining("Circuit 1")
            .hasMessageContaining("switch")
            .hasMessageContaining("gate");
    }

    @Test
    void rejectsUnknownMapSymbolsWithTheirCoordinates() {
        MapParser parser = createParser(new PacManSprites());

        assertThatThrownBy(() -> parser.parseMap(Arrays.asList(
            "####",
            "#PX#",
            "####")))
            .isInstanceOf(PacmanConfigurationException.class)
            .hasMessageContaining("2,1")
            .hasMessageContaining("X");
    }

    @Test
    void mapsWithoutCircuitSymbolsRemainCompatible() {
        Level level = createParser(new PacManSprites()).parseMap(Arrays.asList(
            "###",
            "#P#",
            "###"));

        assertThat(level.getBoard().squareAt(1, 1)).isNotNull();
    }

    private MapParser createParser(PacManSprites sprites) {
        LevelFactory levelFactory = new LevelFactory(sprites, new GhostFactory(sprites),
            new DefaultPointCalculator());
        return new MapParser(levelFactory, new BoardFactory(sprites));
    }
}
