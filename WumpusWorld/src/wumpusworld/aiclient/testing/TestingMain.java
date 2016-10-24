package wumpusworld.aiclient.testing;


import wumpusworld.World;
import wumpusworld.aiclient.assumptionmaking.AssumptionManager;
import wumpusworld.aiclient.assumptionmaking.GoldAssumptionMaker;
import wumpusworld.aiclient.assumptionmaking.PitAssumptionMaker;
import wumpusworld.aiclient.assumptionmaking.WumpusAssumptionMaker;
import wumpusworld.aiclient.model.Point;
import wumpusworld.aiclient.model.WorldModel;

import java.util.Arrays;
import java.util.stream.IntStream;

import static wumpusworld.aiclient.Action.*;
import static wumpusworld.aiclient.Percept.*;
import static wumpusworld.aiclient.model.TFUValue.*;
import static wumpusworld.aiclient.testing.UnitTests.*;




/**
 * Unit Testing
 * Created by Nejc on 17. 10. 2016.
 */
public class TestingMain {


	public static void main(String[] args) {

		init("TFU value")
				.test(() -> {
					it("should negate correctly")
							.expectTo(() -> TRUE    .negate()       == FALSE)
							.expectTo(() -> UNKNOWN .negate()       == UNKNOWN)
							.expectTo(() -> FALSE   .negate()       == TRUE);
					it("should and correctly")
							.expectTo(() -> TRUE    .and(TRUE)      == TRUE)
							.expectTo(() -> TRUE    .and(UNKNOWN)   == UNKNOWN)
							.expectTo(() -> TRUE    .and(FALSE)     == FALSE)
							.expectTo(() -> UNKNOWN .and(TRUE)      == UNKNOWN)
							.expectTo(() -> UNKNOWN .and(UNKNOWN)   == UNKNOWN)
							.expectTo(() -> UNKNOWN .and(FALSE)     == FALSE)
							.expectTo(() -> FALSE   .and(TRUE)      == FALSE)
							.expectTo(() -> FALSE   .and(UNKNOWN)   == FALSE)
							.expectTo(() -> FALSE   .and(FALSE)     == FALSE);
					it("should or correctly")
							.expectTo(() -> TRUE    .or(TRUE)       == TRUE)
							.expectTo(() -> TRUE    .or(UNKNOWN)    == TRUE)
							.expectTo(() -> TRUE    .or(FALSE)      == TRUE)
							.expectTo(() -> UNKNOWN .or(TRUE)       == TRUE)
							.expectTo(() -> UNKNOWN .or(UNKNOWN)    == UNKNOWN)
							.expectTo(() -> UNKNOWN .or(FALSE)      == UNKNOWN)
							.expectTo(() -> FALSE   .or(TRUE)       == TRUE)
							.expectTo(() -> FALSE   .or(UNKNOWN)    == UNKNOWN)
							.expectTo(() -> FALSE   .or(FALSE)      == FALSE);
					it("should imply correctly")
							.expectTo(() -> TRUE    .imply(TRUE)    == TRUE)
							.expectTo(() -> TRUE    .imply(UNKNOWN) == UNKNOWN)
							.expectTo(() -> TRUE    .imply(FALSE)   == FALSE)
							.expectTo(() -> UNKNOWN .imply(TRUE)    == TRUE)
							.expectTo(() -> UNKNOWN .imply(UNKNOWN) == TRUE)
							.expectTo(() -> UNKNOWN .imply(FALSE)   == UNKNOWN)
							.expectTo(() -> FALSE   .imply(TRUE)    == TRUE)
							.expectTo(() -> FALSE   .imply(UNKNOWN) == TRUE)
							.expectTo(() -> FALSE   .imply(FALSE)   == TRUE);
				});

		init("world model", new World(4))
				.clone("cloneWorld")
				.test(world -> {
					WorldModel worldModel = new WorldModel(world);
					init("chunk")
							.test(() -> it("should not allow out-of-bounds indexes")
									.expect(() -> worldModel.getChunk(new Point(-1, 3)))
									.toThrow()
									.and()
									.expect(() -> worldModel.getChunk(new Point(0, 4)))
									.toThrow())
							.test(() -> init("adjacency list", worldModel.getChunk(new Point(0, 0)))
									.test(chunk -> it("should check if adjacent")
											.expect(chunk.isAdjacent(worldModel.getChunk(new Point(1, 0))))
											.toEqual(true)
											.and()
											.expect(chunk.isAdjacent(worldModel.getChunk(new Point(1, 1))))
											.toEqual(false)
											.and()
											.expect(chunk.isAdjacent(worldModel.getChunk(new Point(2, 3))))
											.toEqual(false))
									.test(chunk -> it("should get correct adjacent chunks")
											.expect(chunk.getAdjacent().length)
											.toEqual(2)
											.and()
											.expect(Arrays.stream(chunk.getAdjacent())
													.anyMatch(c -> c.getLocation().equals(new Point(1, 0)))
													&& Arrays.stream(chunk.getAdjacent())
													.anyMatch(c -> c.getLocation().equals(new Point(0, 1))))
											.toEqual(true)));
					world.addWumpus(2, 1);
					init("percept collection", new WorldModel(world).getChunk(new Point(0, 0)).getPercepts())
							.test(perceptCollection -> it("should return corresponding percept value")
									.expect(perceptCollection.getPercept(STENCH))
									.toEqual(TRUE));
				});

		init("assumption making", new World(4))
				.clone("cloneWorld")
				.test(world -> {
					world.addWumpus(2, 2);
					init("Wumpus assumption maker", new WorldModel(world))
							.clone()
							.test(worldModel -> {
								WumpusAssumptionMaker wumpusAssumptionMaker = new WumpusAssumptionMaker(worldModel);
								wumpusAssumptionMaker.init();
								it("should not locate wumpus")
										.expect(wumpusAssumptionMaker.isWumpusLocated())
										.toEqual(false);
								it("should not be done")
										.expect(wumpusAssumptionMaker.isDone())
										.toEqual(false);
								worldModel.doAction(MOVE);
								worldModel.doAction(TURN_RIGHT);
								worldModel.doAction(TURN_RIGHT);
								worldModel.doAction(MOVE);
								worldModel.doAction(TURN_RIGHT);
								worldModel.doAction(MOVE);
								it("should locate wumpus")
										.expect(wumpusAssumptionMaker.isWumpusLocated())
										.toEqual(true);
								it("should not be done")
										.expect(wumpusAssumptionMaker.isDone())
										.toEqual(false);
								worldModel.doAction(TURN_RIGHT);
								worldModel.doAction(SHOOT);
								it("should be done")
										.expect(wumpusAssumptionMaker.isDone())
										.toEqual(true);
							})
							.test(worldModel -> {
								WumpusAssumptionMaker wumpusAssumptionMaker = new WumpusAssumptionMaker(worldModel);
								wumpusAssumptionMaker.init();
								worldModel.doAction(MOVE);
								worldModel.doAction(TURN_RIGHT);
								worldModel.doAction(TURN_RIGHT);
								worldModel.doAction(MOVE);
								worldModel.doAction(SHOOT);
								worldModel.doAction(TURN_RIGHT);
								it("should not be done")
										.expect(wumpusAssumptionMaker.isDone())
										.toEqual(false);
								worldModel.doAction(MOVE);
								it("should be done")
										.expect(wumpusAssumptionMaker.isDone())
										.toEqual(true);
							});
				})
				.test(world -> {
					world.addWumpus(3, 3);
					init("Aw0: Arrow has been shoot through a cell", new WorldModel(world))
							.test(worldModel -> {
								new AssumptionManager(worldModel).initAll();
								worldModel.doAction(SHOOT);
								it("should detect Wumpus' absence")
										.expect(worldModel.getChunk(new Point(2, 0)).getPercepts().getWumpus())
										.toEqual(FALSE);
							});
				})
				.test(world -> {
					world.addWumpus(2, 1);
					init("Aw1: Wumpus has been killed", new WorldModel(world))
							.test(worldModel -> {
								new AssumptionManager(worldModel).initAll();
								worldModel.doAction(SHOOT);
								it("should detect wumpus killed")
										.expect(worldModel.getChunk(new Point(1, 0)).getPercepts().getWumpus())
										.toEqual(FALSE)
										.and()
										.expect(worldModel.getChunk(new Point(2, 2)).getPercepts().getWumpus())
										.toEqual(FALSE);
							});
				})
				.test(world -> {
					world.addWumpus(1, 1);
					init("Aw2: Wumpus has been located in a difference cell", new WorldModel(world))
							.test(worldModel -> {
										new AssumptionManager(worldModel).initAll();
										it("should locate Wumpus")
												.expect(worldModel.getChunk(new Point(0, 0)))
												.to(obj -> obj.getPercepts().getWumpus() == TRUE)
												.and()
												.expect(worldModel.getChunk(new Point(2, 2)))
												.to(obj -> obj.getPercepts().getWumpus() == FALSE);
									}
							);
				})
				.test(world -> {
					world.addWumpus(3, 3);
					init("Aw3: An adjacent cell does not contain stench", new WorldModel(world))
							.test(worldModel -> {
								new AssumptionManager(worldModel).initAll();
								it("should detect Wumpus' absence in spawn")
										.expect(worldModel.getChunk(new Point(1, 0)).getPercepts().getWumpus())
										.toEqual(FALSE);
								worldModel.doAction(MOVE);
								it("should detect Wumpus' absence")
										.expect(worldModel.getChunk(new Point(1, 1)).getPercepts().getWumpus())
										.toEqual(FALSE);
							});
				})
				.test(world -> {
					world.addWumpus(3, 1);
					init("Aw4: The cell is not an adjacent cell of a cell with stench", new WorldModel(world))
							.test(worldModel -> {
								new AssumptionManager(worldModel).initAll();
								worldModel.doAction(MOVE);
								it("should detect Wumpus' absence")
										.expect(worldModel.getChunk(new Point(0, 1)).getPercepts().getWumpus())
										.toEqual(FALSE);
							});
				})
				.test(world -> {
					world.addWumpus(1, 2);
					init("Aw5: A cell with stench property has only one adjacent cell that may contain Wumpus", new WorldModel(world))
							.test(worldModel -> {
								new AssumptionManager(worldModel).initAll();
								worldModel.doAction(MOVE);
								it("should locate Wumpus")
										.expect(worldModel.getChunk(new Point(0, 1)).getPercepts().getWumpus())
										.toEqual(TRUE)
										.and()
										.expect(worldModel.getChunk(new Point(2, 2)).getPercepts().getWumpus())
										.toEqual(FALSE);
							});
				})
				.test(world -> {
					world.addWumpus(4, 4);
					init("Aw6: All cells but one may not contain Wumpus", new WorldModel(world))
							.test(worldModel -> {
								new AssumptionManager(worldModel).initAll();
								IntStream.range(0, 3).forEach(i -> worldModel.doAction(MOVE));
								worldModel.doAction(TURN_LEFT);
								worldModel.doAction(MOVE);
								worldModel.doAction(TURN_LEFT);
								IntStream.range(0, 3).forEach(i -> worldModel.doAction(MOVE));
								worldModel.doAction(TURN_RIGHT);
								worldModel.doAction(MOVE);
								worldModel.doAction(TURN_RIGHT);
								IntStream.range(0, 2).forEach(i -> worldModel.doAction(MOVE));
								it("should locate Wumpus")
										.expect(worldModel.getChunk(new Point(3, 3)).getPercepts().getWumpus())
										.toEqual(TRUE);
							});
				})
				.test(world -> {
					world.addWumpus(2, 3);
					init("Aw: combination", new WorldModel(world))
							.test(worldModel -> {
								new AssumptionManager(worldModel).initAll();
								worldModel.doAction(TURN_LEFT);
								worldModel.doAction(MOVE);
								worldModel.doAction(MOVE);
								worldModel.doAction(TURN_LEFT);
								worldModel.doAction(TURN_LEFT);
								worldModel.doAction(MOVE);
								worldModel.doAction(MOVE);
								worldModel.doAction(TURN_LEFT);
								worldModel.doAction(MOVE);
								worldModel.doAction(MOVE);
								worldModel.doAction(TURN_LEFT);
								worldModel.doAction(MOVE);
								worldModel.doAction(MOVE);
								it("should locate Wumpus")
										.expect(worldModel.getChunk(new Point(1, 2)).getPercepts().getWumpus())
										.toEqual(TRUE);
							});
				})
				.test(world -> {
					world.addWumpus(1, 2);
					init("Aw: detect Wumpus and kill wumpus", new WorldModel(world))
							.test(worldModel -> {
								new AssumptionManager(worldModel).initAll();
								worldModel.doAction(MOVE);
								worldModel.doAction(TURN_LEFT);
								worldModel.doAction(MOVE);
								it("should detect wumpus")
										.expect(worldModel.getChunk(new Point(0, 1)).getPercepts().getWumpus())
										.toEqual(TRUE);
								worldModel.doAction(TURN_LEFT);
								worldModel.doAction(SHOOT);
								it("should assume wumpus is dead")
										.expect(worldModel.getChunk(new Point(0, 1)).getPercepts().getWumpus())
										.toEqual(FALSE);
							});
				})
				.test(world -> {
					world.addWumpus(1, 2);
					init("Aw: shooting and not detecting Wumpus", new WorldModel(world))
							.test(worldModel -> {
								new AssumptionManager(worldModel).initAll();
								worldModel.doAction(SHOOT);
								it("should detect wumpus")
										.expect(worldModel.getChunk(new Point(0, 1)).getPercepts().getWumpus())
										.toEqual(TRUE);
							});
				})
				.test(world -> {
					world.addPit(1, 3);
					WorldModel worldModel = new WorldModel(world);
					init("pit assumption maker", new PitAssumptionMaker(worldModel))
							.test(pitAssumptionMaker -> {
								pitAssumptionMaker.init();
								IntStream.range(0, 3).forEach(i -> worldModel.doAction(MOVE));
								worldModel.doAction(TURN_LEFT);
								IntStream.range(0, 3).forEach(i -> worldModel.doAction(MOVE));
								worldModel.doAction(TURN_LEFT);
								IntStream.range(0, 2).forEach(i -> worldModel.doAction(MOVE));
								it("should not be done")
										.expect(pitAssumptionMaker.isDone())
										.toEqual(false);
								worldModel.doAction(MOVE);
								it("should be done")
										.expect(pitAssumptionMaker.isDone())
										.toEqual(true);
							});
				})
				.test(world -> init("Ap1: An adjacent cell does not contain breeze", new WorldModel(world))
						.test(worldModel -> {
							new AssumptionManager(worldModel).initAll();
							it("should detect absence of pi #1")
									.expect(worldModel.getChunk(new Point(0, 1)).getPercepts().getPit())
									.toEqual(FALSE)
									.and()
									.expect(worldModel.getChunk(new Point(0, 2)).getPercepts().getPit())
									.toEqual(UNKNOWN);
							worldModel.doAction(MOVE);
							it("should detect absence of pit #2")
									.expect(worldModel.getChunk(new Point(2, 0)).getPercepts().getPit())
									.toEqual(FALSE)
									.and()
									.expect(worldModel.getChunk(new Point(2, 2)).getPercepts().getPit())
									.toEqual(UNKNOWN);
						}))
				.test(world -> {
					world.addGold(4, 1);
					WorldModel worldModel = new WorldModel(world);
					init("gold assumption maker", new GoldAssumptionMaker(worldModel))
							.test(goldAssumptionMaker -> {
								goldAssumptionMaker.init();
								IntStream.range(0, 2).forEach(i -> worldModel.doAction(MOVE));
								it("should not be done")
										.expect(goldAssumptionMaker.isDone())
										.toEqual(false);
								worldModel.doAction(MOVE);
								it("should be done")
										.expect(goldAssumptionMaker.isDone())
										.toEqual(true);
							});
				})
				.test(world -> {
					world.addGold(1, 1);
					init("Ag1: When glitter is detected.", new WorldModel(world))
							.test(worldModel -> {
								new AssumptionManager(worldModel).initAll();
								it("should locate gold in spawn")
										.expect(worldModel.getChunk(new Point(0, 0)).getPercepts().getGold())
										.toEqual(TRUE)
										.and()
										.expect(worldModel.getChunk(new Point(2, 2)).getPercepts().getGold())
										.toEqual(FALSE);
							});
				})
				.test(world -> {
					world.addGold(2, 1);
					init("Ag1: When glitter is detected", new WorldModel(world))
							.test(worldModel -> {
								new AssumptionManager(worldModel).initAll();
								worldModel.doAction(MOVE);
								it("should locate gold")
										.expect(worldModel.getChunk(new Point(1, 0)).getPercepts().getGold())
										.toEqual(TRUE)
										.and()
										.expect(worldModel.getChunk(new Point(2, 3)).getPercepts().getGold())
										.toEqual(FALSE);
							});
				})
				.test(world -> {
					world.addWumpus(1, 3);
					world.addPit(3, 1);
					init("Sample test", new WorldModel(world))
							.test(worldModel -> {
								new AssumptionManager(worldModel).initAll();
								worldModel.doAction(MOVE);
								worldModel.doAction(TURN_LEFT);
								worldModel.doAction(TURN_LEFT);
								worldModel.doAction(MOVE);
								worldModel.doAction(TURN_RIGHT);
								worldModel.doAction(MOVE);
								it("should predict safeness")
										.expect(worldModel.getChunk(new Point(1, 1)).getPercepts().isSafe())
										.toEqual(true);
								it("should locate Wumpus")
										.expect(worldModel.getChunk(new Point(0, 2)).getPercepts().getWumpus())
										.toEqual(TRUE);
								it("should locate pit")
										.expect(worldModel.getChunk(new Point(2, 0)).getPercepts().getPit())
										.toEqual(TRUE);
							});
				});

		printResults();

	}


}
