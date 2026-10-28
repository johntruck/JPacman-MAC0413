package nl.tudelft.jpacman.board;

import nl.tudelft.jpacman.sprite.Sprite;

/**
 * A fixed switch that remains on the board and toggles its associated circuit
 * when activated by the player.
 */
public final class SwitchSquare extends Square {

    private final Circuit circuit;
    private final Sprite backgroundSprite;
    private final Sprite switchSprite;

    /**
     * Creates a fixed switch.
     *
     * @param circuit The circuit controlled by this switch.
     * @param backgroundSprite The ground sprite beneath the key.
     * @param switchSprite The key sprite.
     */
    public SwitchSquare(Circuit circuit, Sprite backgroundSprite, Sprite switchSprite) {
        if (circuit == null || backgroundSprite == null || switchSprite == null) {
            throw new IllegalArgumentException("Switch circuit and sprites cannot be null.");
        }
        this.circuit = circuit;
        this.backgroundSprite = backgroundSprite;
        this.switchSprite = switchSprite;
        circuit.registerSwitch();
    }

    /**
     * Toggles the circuit command. The level invokes this only after a
     * successful player move and collision handling.
     */
    public void activate() {
        circuit.toggle();
    }

    /**
     * @return The circuit identifier for this switch.
     */
    public int getCircuitId() {
        return circuit.getId();
    }

    /**
     * @return The ground sprite beneath the key icon.
     */
    public Sprite getBackgroundSprite() {
        return backgroundSprite;
    }

    /**
     * @return The key icon sprite.
     */
    public Sprite getSwitchSprite() {
        return switchSprite;
    }

    @Override
    public boolean isAccessibleTo(Unit unit) {
        return true;
    }

    @Override
    public Sprite getSprite() {
        return switchSprite;
    }
}
