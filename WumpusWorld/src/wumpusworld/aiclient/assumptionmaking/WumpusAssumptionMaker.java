package wumpusworld.aiclient.assumptionmaking;


import wumpusworld.aiclient.Action;
import wumpusworld.aiclient.Direction;
import wumpusworld.aiclient.model.Chunk;
import wumpusworld.aiclient.model.PerceptChanged;
import wumpusworld.aiclient.model.Point;
import wumpusworld.aiclient.model.WorldModel;
import wumpusworld.aiclient.util.EventHandler;
import wumpusworld.aiclient.util.EventInterface;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import static wumpusworld.aiclient.Action.SHOOT;
import static wumpusworld.aiclient.Direction.*;
import static wumpusworld.aiclient.Percept.WUMPUS;
import static wumpusworld.aiclient.model.TFUValue.FALSE;
import static wumpusworld.aiclient.model.TFUValue.TRUE;




/**
 * Makes assumptions associated with pits.
 * <p>
 * Created by Nejc on 14. 10. 2016.
 */
public class WumpusAssumptionMaker implements AssumptionMaker {




    private final WorldModel worldModel;
    private HashMap<EventHandler<?>, EventInterface<?>> listeners;
    private boolean done;
    private boolean wumpusAlive;
    private boolean hasArrow;
    private boolean wumpusLocated;
    private int chunksWithNoWumpus;




    /**
     * Gets associated {@link WorldModel}.
     *
     * @return Associated {@link WorldModel}.
     */
    public WorldModel getWorldModel() {
        return worldModel;
    }


    /**
     * Checks if this component has finished with its work.
     *
     * @return Whether or not this component has finished with its work.
     */
    @Override
    public boolean isDone() {
        return done;
    }


    /**
     * Checks is Wumpus as been located.
     *
     * @return {@code true} if Wumpus has been located or killed, false otherwise.
     */
    public boolean isWumpusLocated() {
        return wumpusLocated;
    }




    /**
     * Creates a new instance of {@link WumpusAssumptionMaker}.
     *
     * @param worldModel Associated {@link WorldModel}.
     */
    public WumpusAssumptionMaker(WorldModel worldModel) {
        Objects.requireNonNull(worldModel);
        this.worldModel = worldModel;
    }




    /**
     * Initializes the component.
     */
    @Override
    public void init() {
        update();
    }


    /**
     * Forces component to update its knowledge base and subscriptions to events.
     */
    @Override
    public void update() {
        done = false;
        wumpusLocated = false;
        unsubscribe(); // re-subscribe
        subscribe();

        initArrowShot();
        if (done) return;
        initWumpusKilled();
        if (done) return;
        initWumpusLocated();
        if (done) return;
        initChunkWithNoStenchDiscovered();
        if (done) return;
        initChunkWithStenchDiscovered();
        if (done) return;
        initChunkWithStenchHasOnlyOneAdjacentChunkWithWumpusLeft();
        if (done) return;
        initOnlyOneChunkWithWumpusRemaining();
    }


    /**
     * Disposes the component.
     */
    @Override
    public void dispose() {
        unsubscribe();
    }


    /**
     * Subscribes to appropriate events.
     */
    private void subscribe() {
        if (listeners != null)
            unsubscribe();

        Chunk[] chunks = worldModel.getChunks();
        listeners = new HashMap<>(2 + chunks.length);

        EventHandler<Action> actionEventHandler = (sender, arg) -> onAction(arg);
        worldModel.getActionEvent().subscribe(actionEventHandler);
        listeners.put(actionEventHandler, worldModel.getActionEvent());

        EventHandler<Chunk> chunkExploredEventHandler = (sender, arg) -> onChunkExplored(arg);
        worldModel.getChunkExploredEvent().subscribe(chunkExploredEventHandler);
        listeners.put(chunkExploredEventHandler, worldModel.getChunkExploredEvent());

        for (final Chunk chunk : worldModel.getChunks()) {
            EventHandler<PerceptChanged> perceptChangedEventHandler = (sender, arg) -> onPerceptChanged(chunk, arg);
            chunk.getPercepts().getPerceptChangedEvent().subscribe(perceptChangedEventHandler);
        }
    }


    /**
     * Unsubscribes from all subscribed events.
     */
    private void unsubscribe() {
        if (listeners != null)
            listeners.entrySet().forEach(pair -> pair.getValue().unsubscribe(pair.getKey()));
        this.listeners = null; // let GC do its work
    }


    /**
     * Handles the logic just before the component is about to finish its work.
     */
    private void onPreDone() {
        unsubscribe();
    }


    /**
     * Handles the logic of component finishing its work.
     */
    private void onDone() {
        wumpusLocated = true;
        if (!worldModel.isWumpusAlive() || !worldModel.hasArrow()) { // we don't want to terminate the process if Wumpus is still alive or the player, since the player may still shoot it
            unsubscribe();
            done = true;
        }
    }




    /**
     * Event Handler. Called whenever an action is played.
     *
     * @param action Action played.
     */
    private void onAction(Action action) {
        if (action == SHOOT) {
            invokeArrowShot();
            invokeWumpusKilled();
        }
    }


    /**
     * Event Handler. Called whenever a new {@link Chunk} is explored.
     *
     * @param chunk {@link Chunk} explored.
     */
    private void onChunkExplored(Chunk chunk) {
        invokeWumpusLocated(chunk);
        invokeChunkWithNoStenchDiscovered(chunk);
        invokeChunkWithStenchDiscovered(chunk);
        invokeChunkWithStenchHasOnlyOneAdjacentChunkWithWumpusLeft(chunk, true);
    }


    /**
     * Event Handler. Called whenever a {@link wumpusworld.aiclient.Percept} of a {@link wumpusworld.aiclient.model.PerceptCollection} in a {@link Chunk} changes.
     *
     * @param chunk          {@link Chunk} in which the change has occurred.
     * @param perceptChanged Data associated with the event.
     */
    private void onPerceptChanged(Chunk chunk, PerceptChanged perceptChanged) {
        if (done) return;

        if (perceptChanged.getPercept() == WUMPUS) {
            invokeWumpusLocated(chunk);
            invokeOnlyOneChunkWithWumpusRemaining();
        }
    }




    private void initArrowShot() {
        this.hasArrow = worldModel.hasArrow();
    }


    private void initWumpusKilled() {
        this.wumpusAlive = true;
        invokeWumpusKilled();
    }


    private void initWumpusLocated() {
        for (Chunk chunk : worldModel.getChunks()) {
            if (chunk.getPercepts().getWumpus() == TRUE) {
                onWumpusLocated(chunk);
                return;
            }
        }
    }


    private void initChunkWithNoStenchDiscovered() {
        Arrays.stream(worldModel.getChunks())
                .forEach(this::invokeChunkWithNoStenchDiscovered);
    }


    private void initChunkWithStenchDiscovered() {
        Arrays.stream(worldModel.getChunks())
                .filter(chunk -> chunk.getPercepts().getStench() == TRUE)
                .forEach(this::invokeChunkWithStenchDiscovered);
    }


    private void initChunkWithStenchHasOnlyOneAdjacentChunkWithWumpusLeft() {
        Arrays.stream(worldModel.getChunks())
                .filter(chunk -> chunk.getPercepts().getStench() == TRUE)
                .forEach(chunk -> invokeChunkWithStenchHasOnlyOneAdjacentChunkWithWumpusLeft(chunk, true));
    }


    private void initOnlyOneChunkWithWumpusRemaining() {
        invokeOnlyOneChunkWithWumpusRemaining();
    }




    private void invokeArrowShot() {
        if (hasArrow && !worldModel.hasArrow()) { // arrow has actually been shot now
            this.hasArrow = false;
            onArrowShot();
        }
    }


    private void invokeWumpusKilled() {
        boolean wumpusAlive = worldModel.isWumpusAlive();
        if (wumpusAlive != this.wumpusAlive) {
            if (wumpusAlive) throw new IllegalStateException("Wumpus added in mid-game.");
            this.wumpusAlive = false;
            onWumpusKilled();
        }
    }


    private void invokeWumpusLocated(Chunk chunk) {
        if (chunk.getPercepts().getWumpus() == TRUE)
            onWumpusLocated(chunk);
    }


    private void invokeChunkWithNoStenchDiscovered(Chunk chunk) {
        if (chunk.getPercepts().getStench() == FALSE)
            onChunkWithNoStenchDiscovered(chunk);
    }


    private void invokeChunkWithStenchDiscovered(Chunk chunk) {
        if (chunk.getPercepts().getStench() == TRUE)
            onChunkWithStenchDiscovered(chunk);
    }


    private void invokeChunkWithStenchHasOnlyOneAdjacentChunkWithWumpusLeft(Chunk chunk, boolean subscribe) {
        if (chunk.getPercepts().getStench() != TRUE) return;

        Optional<Chunk> theOnlyOne = chunk.getOnlyAdjacentChunkWithSatisfiablePercept(WUMPUS);
        if (theOnlyOne.isPresent()) {
            onChunkWithStenchHasOnlyOneAdjacentChunkWithWumpusLeft(theOnlyOne.get());
        } else if (subscribe) {
            // We need to subscribe to appropriate events to detect when this chunk will have only one adjacent chunk with breeze property left
            Arrays.stream(chunk.getAdjacent())
                    .filter(c -> c.getPercepts().getStench().isSatisfiable())
                    .forEach(c -> {
                        EventHandler<PerceptChanged> perceptChangedEventHandler = (sender, arg) -> {
                            if (arg.getPercept() == WUMPUS && arg.getNewValue() == FALSE)
                                invokeChunkWithStenchHasOnlyOneAdjacentChunkWithWumpusLeft(chunk, false);
                        };
                        c.getPercepts().getPerceptChangedEvent().subscribe(perceptChangedEventHandler);
                        listeners.put(perceptChangedEventHandler, c.getPercepts().getPerceptChangedEvent());
                    });
        }
    }


    private void invokeOnlyOneChunkWithWumpusRemaining() {
        Chunk onlyWithWumpus = null;
        for (Chunk chunk : worldModel.getChunks()) {
            if (chunk.getPercepts().getWumpus().isSatisfiable()) {
                if (onlyWithWumpus == null)
                    onlyWithWumpus = chunk;
                else return;
            }
        }

        if (onlyWithWumpus != null)
            onOnlyOneChunkWithWumpusRemaining(onlyWithWumpus);
    }




    private void onArrowShot() {
        Point location = worldModel.getPlayerLocation();
        Direction direction = worldModel.getPlayerDirection();

        Point pointToModify = location;
        while (worldModel.isValidPosition(pointToModify)) {
            worldModel.getPercepts(pointToModify).setWumpus(FALSE);
            int dx = direction == RIGHT ? 1
                    : direction == LEFT ? -1
                    : 0;
            int dy = direction == UP ? 1
                    : direction == DOWN ? -1
                    : 0;
            pointToModify = pointToModify.translate(dx, dy);
        }
    }


    private void onWumpusKilled() {
        onPreDone();
        this.wumpusLocated = true;
        Arrays.stream(worldModel.getChunks())
                .forEach(chunk -> chunk.getPercepts().setWumpus(FALSE));
        onDone();
    }


    private void onWumpusLocated(Chunk chunk) {
        if (!worldModel.hasArrow())
            onPreDone(); // Wumpus has been located and cannot be shot => we're done
        this.wumpusLocated = true;
        Arrays.stream(worldModel.getChunks())
                .filter(c -> !c.equals(chunk))
                .forEach(c -> c.getPercepts().setWumpus(FALSE));
        if (!worldModel.hasArrow())
            onDone(); // Wumpus has been located and cannot be shot => we're done
    }


    private void onChunkWithNoStenchDiscovered(Chunk chunk) {
        Arrays.stream(chunk.getAdjacent())
                .forEach(c -> c.getPercepts().setWumpus(FALSE));
    }


    private void onChunkWithStenchDiscovered(Chunk chunk) {
        Arrays.stream(worldModel.getChunks())
                .filter(c -> !c.isAdjacent(chunk))
                .forEach(c -> c.getPercepts().setWumpus(FALSE));
    }


    private void onChunkWithStenchHasOnlyOneAdjacentChunkWithWumpusLeft(Chunk adjacentChunk) {
        adjacentChunk.getPercepts().setWumpus();
    }


    private void onOnlyOneChunkWithWumpusRemaining(Chunk chunk) {
        chunk.getPercepts().setWumpus();
    }


}
