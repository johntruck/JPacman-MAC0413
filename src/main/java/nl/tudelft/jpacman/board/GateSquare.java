package nl.tudelft.jpacman.board;

import nl.tudelft.jpacman.sprite.Sprite;

/**
 * A gate square whose accessibility is controlled by a {@link Circuit}.
 */
public final class GateSquare extends Square {

    /** The visible state of a gate. */
    public enum State {
        /** The passage is open to all units. */
        OPEN,
        /** The passage is closed and unoccupied. */
        CLOSED,
        /** The gate is commanded closed, but occupants may still leave. */
        PENDING_CLOSURE
    }

    private final Circuit circuit;
    private final Sprite openSprite;
    private final Sprite closedSprite;
    private boolean open;
    private boolean pendingClosure;

    /**
     * Creates a gate that starts closed.
     *
     * @param circuit The circuit controlling this gate.
     * @param openSprite Sprite used while the gate is open.
     * @param closedSprite Sprite used while the gate is closed.
     */
    public GateSquare(Circuit circuit, Sprite openSprite, Sprite closedSprite) {
        if (circuit == null || openSprite == null || closedSprite == null) {
            throw new IllegalArgumentException("Gate circuit and sprites cannot be null.");
        }
        this.circuit = circuit;
        this.openSprite = openSprite;
        this.closedSprite = closedSprite;
        this.open = false;
        this.pendingClosure = false;
        circuit.registerGate(this);
    }

    /**
     * @return The circuit identifier for this gate.
     */
    public int getCircuitId() {
        return circuit.getId();
    }

    /**
     * @return The current visible and physical gate state.
     */
    public State getState() {
        if (open) {
            return State.OPEN;
        }
        if (pendingClosure) {
            return State.PENDING_CLOSURE;
        }
        return State.CLOSED;
    }

    /**
     * @return Whether this gate is open.
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * @return Whether closing is waiting for occupants to leave.
     */
    public boolean isPendingClosure() {
        return pendingClosure;
    }

    @Override
    public boolean isAccessibleTo(Unit unit) {
        return open || (pendingClosure && getOccupants().contains(unit));
    }

    @Override
    public Sprite getSprite() {
        if (open) {
            return openSprite;
        }
        return closedSprite;
    }

    /**
     * Applies a circuit command while preserving occupied-gate semantics.
     *
     * @param shouldOpen True to open the gate or cancel a pending closure.
     */
    void applyCommand(boolean shouldOpen) {
        if (shouldOpen) {
            open = true;
            pendingClosure = false;
        } else {
            open = false;
            pendingClosure = !getOccupants().isEmpty();
        }
    }

    @Override
    void remove(Unit occupant) {
        super.remove(occupant);
        if (pendingClosure && getOccupants().isEmpty()) {
            pendingClosure = false;
        }
    }
}
