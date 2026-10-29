package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.sprite.Sprite;
import nl.tudelft.jpacman.board.Circuit;

/**
 * A phase pulse, an item that inverts the gates and switches of a given circuit for 5 seconds.
 */
public class PhasePulse extends Unit {

    /**
     * The sprite of this unit.
     */
    private final Sprite image;

    /**
     * The duration of the effect of this unit.
     */
    private final long duration;

    /**
     * The circuit associated with this phase pulse.
     */
    private final Circuit circuit;

    /**
     * Creates a new phase pulse.
     * @param sprite The sprite of this phase pulse.
     * @param circuit The circuit associated with this phase pulse.
     */
    public PhasePulse(Circuit circuit, Sprite sprite) {
        this.image = sprite;
        this.circuit = circuit;
        this.duration = 5000;
    }

    /**
     * Returns the circuit of this phase pulse.
     * @return The circuit of this phase pulse.
     */
    public Circuit getCircuit() {
        return this.circuit;
    }

    /**
     * Returns the id of the circuit of this phase pulse.
     * @return The id of the circuit of this phase pulse.
     */
    public int getCircuitId() {
        return this.circuit.getId();
    }

    /**
     * Returns the duration of the the phase pulse.
     * @return The duration of the phase pulse.
     */
    public long getDuration() {
        return this.duration;
    }

    @Override
    public Sprite getSprite() {
        return image;
    }
}
