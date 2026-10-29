package nl.tudelft.jpacman.board;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;

import nl.tudelft.jpacman.level.PhasePulse;

/**
 * A circuit shared by gates and fixed switches on a board.
 *
 * A circuit stores the last commanded state; each gate decides locally whether
 * it can close immediately or must wait for its occupants to leave.
 */
public final class Circuit {

    private final int id;
    private final List<GateSquare> gates;
    private final ScheduledExecutorService schedule; 
    private boolean open;
    private boolean onPhasePulse;
    private int switchCount;
    private long remainingPhasePulseTime;
    private ScheduledFuture<?> endPhasePulseTask;

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
        this.onPhasePulse = false;
        this.switchCount = 0;
        this.schedule = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * @return The identifier of this circuit.
     */
    public int getId() {
        return id;
    }

    /**
     * Stops the current scheduled endPhasePulseTask.
     */
    public void stopEndPhasePulseTask() {
        if (onPhasePulse) {
            this.remainingPhasePulseTime = this.endPhasePulseTask.getDelay(TimeUnit.MILLISECONDS);
            this.endPhasePulseTask.cancel(false);
        }
    }

    /**
     * Resume the effects of the PhasePulse.
     */
    public void resumePhasePulseTask() {
        if (onPhasePulse) {
            this.createEndPhasePulseTask(this.remainingPhasePulseTime);
        }
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

    /**
     * Inverts the open/close command of all gates in this circuit.
     */
    public void consumedAPhasePulse(PhasePulse phasePulse) {
        if (!this.onPhasePulse) {
            this.onPhasePulse = true;
            this.toggle();
        
            this.endPhasePulseTask = createEndPhasePulseTask(phasePulse.getDuration());
        } else {
            this.endPhasePulseTask.cancel(false);
            this.endPhasePulseTask = createEndPhasePulseTask(phasePulse.getDuration());
        }
    }

    /**
     * Ends the effect of the phase pulse in this circuit.
     */
    public void endPhasePulse() {
        this.toggle();
        this.onPhasePulse = false;
    }

    /**
     * Creates the task that ends the effect of the phase pulse in this circuit.
     */
    public ScheduledFuture<?> createEndPhasePulseTask(long time) {
        return this.schedule.schedule(() -> {
            this.endPhasePulse();
        }, time, TimeUnit.MILLISECONDS);
    }


}
