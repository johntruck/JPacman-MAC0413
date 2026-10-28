package nl.tudelft.jpacman.board;

import java.util.ArrayList;
import java.util.List;

/**
 * A circuit shared by gates and fixed switches on a board.
 *
 * A circuit stores the last commanded state; each gate decides locally whether
 * it can close immediately or must wait for its occupants to leave.
 */
public final class Circuit {

    private final int id;
    private final List<GateSquare> gates;
    private boolean open;
    private int switchCount;

    /**
     * Creates a circuit, closed by default.
     *
     * @param id Circuit identifier (1 or 2).
     */
    public Circuit(int id) {
        if (id != 1 && id != 2) {
            throw new IllegalArgumentException("Circuit id must be 1 or 2.");
        }
        this.id = id;
        this.gates = new ArrayList<>();
        this.open = false;
        this.switchCount = 0;
    }

    /**
     * @return The identifier of this circuit.
     */
    public int getId() {
        return id;
    }

    /**
     * @return Whether the circuit's current command is to keep gates open.
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Reverses the current command for every gate in this circuit.
     */
    public void toggle() {
        setOpen(!open);
    }

    /**
     * Sends the same open/close command to all gates in this circuit.
     *
     * @param shouldOpen True to open, false to close.
     */
    public void setOpen(boolean shouldOpen) {
        open = shouldOpen;
        for (GateSquare gate : gates) {
            gate.applyCommand(shouldOpen);
        }
    }

    /**
     * Registers a gate with this circuit and applies its current command.
     *
     * @param gate Gate to register.
     */
    void registerGate(GateSquare gate) {
        gates.add(gate);
        gate.applyCommand(open);
    }

    /**
     * Records another fixed switch belonging to this circuit.
     */
    void registerSwitch() {
        switchCount++;
    }

    /**
     * @return Whether this circuit has at least one gate.
     */
    public boolean hasGate() {
        return !gates.isEmpty();
    }

    /**
     * @return Whether this circuit has at least one switch.
     */
    public boolean hasSwitch() {
        return switchCount > 0;
    }
}
