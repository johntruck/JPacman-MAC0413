package nl.tudelft.jpacman.board;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import nl.tudelft.jpacman.sprite.EmptySprite;
import nl.tudelft.jpacman.sprite.Sprite;

/**
 * Tests circuit commands and the local state transitions of gates.
 */
class CircuitTest {

    private final Sprite floor = new EmptySprite();
    private final Sprite wall = new EmptySprite();

    @Test
    void gatesStartClosedAndCircuitsAreIndependent() {
        Circuit first = new Circuit(1);
        Circuit second = new Circuit(2);
        GateSquare gateOne = new GateSquare(first, floor, wall);
        GateSquare anotherGateOne = new GateSquare(first, floor, wall);
        GateSquare gateTwo = new GateSquare(second, floor, wall);
        new SwitchSquare(first, floor, wall);
        new SwitchSquare(first, floor, wall);
        TestUnit unit = new TestUnit();

        assertThat(gateOne.getState()).isEqualTo(GateSquare.State.CLOSED);
        assertThat(anotherGateOne.getState()).isEqualTo(GateSquare.State.CLOSED);
        assertThat(gateTwo.getState()).isEqualTo(GateSquare.State.CLOSED);
        assertThat(gateOne.isAccessibleTo(unit)).isFalse();
        assertThat(anotherGateOne.isAccessibleTo(unit)).isFalse();
        assertThat(gateTwo.isAccessibleTo(unit)).isFalse();

        first.toggle();

        assertThat(gateOne.getState()).isEqualTo(GateSquare.State.OPEN);
        assertThat(anotherGateOne.getState()).isEqualTo(GateSquare.State.OPEN);
        assertThat(gateOne.isAccessibleTo(unit)).isTrue();
        assertThat(anotherGateOne.isAccessibleTo(unit)).isTrue();
        assertThat(gateTwo.getState()).isEqualTo(GateSquare.State.CLOSED);
        assertThat(gateTwo.isAccessibleTo(unit)).isFalse();
    }

    @Test
    void occupiedGateWaitsForEveryOccupantBeforeClosing() {
        Circuit circuit = new Circuit(1);
        GateSquare gate = new GateSquare(circuit, floor, wall);
        Square ground = new BasicSquare();
        TestUnit firstOccupant = new TestUnit();
        TestUnit secondOccupant = new TestUnit();
        TestUnit newEntrant = new TestUnit();

        circuit.setOpen(true);
        firstOccupant.occupy(gate);
        secondOccupant.occupy(gate);
        circuit.setOpen(false);

        assertThat(gate.getState()).isEqualTo(GateSquare.State.PENDING_CLOSURE);
        assertThat(gate.isAccessibleTo(firstOccupant)).isTrue();
        assertThat(gate.isAccessibleTo(newEntrant)).isFalse();

        firstOccupant.occupy(ground);
        assertThat(gate.getState()).isEqualTo(GateSquare.State.PENDING_CLOSURE);

        secondOccupant.occupy(ground);
        assertThat(gate.getState()).isEqualTo(GateSquare.State.CLOSED);
        assertThat(gate.isAccessibleTo(newEntrant)).isFalse();
    }

    @Test
    void reopeningCancelsPendingClosure() {
        Circuit circuit = new Circuit(1);
        GateSquare gate = new GateSquare(circuit, floor, wall);
        TestUnit occupant = new TestUnit();

        circuit.setOpen(true);
        occupant.occupy(gate);
        circuit.setOpen(false);
        assertThat(gate.isPendingClosure()).isTrue();

        circuit.setOpen(true);

        assertThat(gate.getState()).isEqualTo(GateSquare.State.OPEN);
        assertThat(gate.isPendingClosure()).isFalse();
        assertThat(gate.isAccessibleTo(new TestUnit())).isTrue();
    }

    @Test
    void pendingGateUsesFloorSpriteUntilItsLastOccupantLeaves() {
        Circuit circuit = new Circuit(1);
        GateSquare gate = new GateSquare(circuit, floor, wall);
        TestUnit occupant = new TestUnit();
        Square ground = new BasicSquare();

        circuit.setOpen(true);
        occupant.occupy(gate);
        circuit.setOpen(false);

        assertThat(gate.getState()).isEqualTo(GateSquare.State.PENDING_CLOSURE);
        assertThat(gate.getSprite()).isSameAs(floor);

        occupant.occupy(ground);

        assertThat(gate.getState()).isEqualTo(GateSquare.State.CLOSED);
        assertThat(gate.getSprite()).isSameAs(wall);
    }

    @Test
    void circuitRecordsItsSwitchesAndGates() {
        Circuit circuit = new Circuit(1);
        assertThat(circuit.hasGate()).isFalse();
        assertThat(circuit.hasSwitch()).isFalse();

        new GateSquare(circuit, floor, wall);
        new SwitchSquare(circuit, floor, wall);

        assertThat(circuit.hasGate()).isTrue();
        assertThat(circuit.hasSwitch()).isTrue();
    }

    private static final class TestUnit extends Unit {
        @Override
        public Sprite getSprite() {
            return new EmptySprite();
        }
    }
}
