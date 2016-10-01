package ai.impl.dev;


import kalaha.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.round;




/**
 * Handles multi-debugging.
 * Created by Nejc on 1. 10. 2016.
 */
public class MultiDebugging implements Runnable {




	public static final int INSTANCE_COUNT = 4;
	public static final int ITERATION_COUNT = 5;
	public static final int TOTAL_COUNT = INSTANCE_COUNT * ITERATION_COUNT;




	public static void main(String[] args) {
		MultiDebugging multiDebugging = new MultiDebugging();
		multiDebugging.run();
	}



	public void run() {
		Client player1 = gs -> Clients.getAIMoveAdvanced(gs, false);
		Client player2 = Clients::getRandomMoveAdvanced;

		try {
			runAll(player1, player2);
		} catch (InterruptedException e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
	}


	public void runAll(final Client player1, final Client player2) throws InterruptedException {
		System.out.println("Starting " + TOTAL_COUNT + " game sessions.\n");

		final RunningResult[] runningResults = new RunningResult[TOTAL_COUNT];
		final Thread[] threads = new Thread[INSTANCE_COUNT];
		AtomicInteger finishedThreadsCount = new AtomicInteger();

		for (int j = 0; j < ITERATION_COUNT; j++) {
			int iteration = j;
			for (int i = 0; i < INSTANCE_COUNT; i++) {
				final int index = i;

				Thread thread = new Thread() {
					@Override
					public void run() {
						RunningResult runningResult = MultiDebugging.run(player1, player2);
						int totalIndex = (iteration * INSTANCE_COUNT) + index;
						runningResults[totalIndex] = runningResult;
						System.out.println("Thread finished: " + totalIndex + " (" + finishedThreadsCount.incrementAndGet() + "/" + INSTANCE_COUNT + ")");
					}
				};

				threads[i] = thread;
				thread.start();
			}

			// wait for all the threads to finish
			for (Thread thread : threads) {
				thread.join();
			}
		}

		System.out.println("All threads finished.");
		processResults(runningResults);
	}


	private static RunningResult run(Client player1, Client player2) {
		GameState gameState = new GameState();

		List<PlayerMoveResult> p1res = new ArrayList<>();
		List<PlayerMoveResult> p2res = new ArrayList<>();

		while (!gameState.gameEnded()) {
			int nextPlayer = gameState.getNextPlayer();

			PlayerMoveResult selectedMove = nextPlayer == 1
					? player1.makeMove(gameState)
					: player2.makeMove(gameState);

			(nextPlayer == 1 ? p1res : p2res).add(selectedMove);

			gameState.makeMove(selectedMove.selectedAmbo);
		}

		int winner = gameState.getWinner();

		RunningResult result = new RunningResult();
		result.draw = winner < 1;
		result.winner = winner < 1 ? -1 : winner;
		result.player1Results = p1res.toArray(new PlayerMoveResult[p1res.size()]);
		result.player2Results = p2res.toArray(new PlayerMoveResult[p2res.size()]);
		result.p1score = gameState.getScore(1);
		result.p2score = gameState.getScore(2);
		return result;
	}


	private void processResults(RunningResult[] results) {

		System.out.println("\nProcessing results...");

		int drawCount = count(results, result -> result.draw);
		int player1WinCount = count(results, item -> item.winner == 1);
		int player2WinCount = count(results, item -> item.winner == 2);

		double averageP1Seeds = correctPrecision(getAverage(results, item -> (double) item.p1score));
		double averageP2Seeds = correctPrecision(getAverage(results, item -> (double) item.p2score));

		double depthAverage1 = correctPrecision(getAverage(results, item -> getAverage(item.player1Results, item1 -> (double) item1.depthReached)));
		double uvAverage1 = correctPrecision(getAverage(results, item -> getAverage(item.player1Results, item1 -> (double) item1.utilityValue)));
		double depthChangeAverage1 = correctPrecision(getAverage(results, item -> getAverageChange(item.player1Results, item1 -> (double) item1.depthReached)));
		double uvChangeAverage1 = correctPrecision(getAverage(results, item -> getAverageChange(item.player1Results, item1 -> (double) item1.utilityValue)));

		double depthAverage2 = correctPrecision(getAverage(results, item -> getAverage(item.player2Results, item1 -> (double) item1.depthReached)));
		double uvAverage2 = correctPrecision(getAverage(results, item -> getAverage(item.player2Results, item1 -> (double) item1.utilityValue)));
		double depthChangeAverage2 = correctPrecision(getAverage(results, item -> getAverageChange(item.player2Results, item1 -> (double) item1.depthReached)));
		double uvChangeAverage2 = correctPrecision(getAverage(results, item -> getAverageChange(item.player2Results, item1 -> (double) item1.utilityValue)));

		System.out.println();
		System.out.println("Games played: " + results.length);
		System.out.println("Draw games count: " + drawCount);
		System.out.println("Player1 wins: " + player1WinCount);
		System.out.println("Player2 wins: " + player2WinCount);
		System.out.println("Average score: " + round(averageP1Seeds) + "/" + round(averageP2Seeds));
		System.out.println("-----------------------------------------------------");
		System.out.println("Player1 average depth: " + depthAverage1);
		System.out.println("Player1 average depth change per level: " + depthChangeAverage1);
		System.out.println("Player1 average utility value: " + uvAverage1);
		System.out.println("Player1 average utility value change per level: " + uvChangeAverage1);
		System.out.println("-----------------------------------------------------");
		System.out.println("Player2 average depth: " + depthAverage2);
		System.out.println("Player2 average depth change per level: " + depthChangeAverage2);
		System.out.println("Player2 average utility value: " + uvAverage2);
		System.out.println("Player2 average utility value change per level: " + uvChangeAverage2);
		System.out.println();

	}




	private double correctPrecision(double value) {
		return round(value * 10) / 10D;
	}


	private static <T> double getAverage(T[] collection, ValueProvider<Double, T> valueProvider) {
		double sum = 0;

		for (T t : collection) {
			sum += valueProvider.getValue(t);
		}

		return sum / collection.length;
	}


	private static <T> double getAverageChange(T[] collection, ValueProvider<Double, T> valueProvider) {
		double previousValue = 0;
		double valueChangeSum = 0;
		boolean first = true;

		for (T t : collection) {
			if (first) {
				previousValue = valueProvider.getValue(t);
				first  = false;
			} else {
				double value = valueProvider.getValue(t);
				valueChangeSum += (value - previousValue);
				previousValue = value;
			}
		}

		return valueChangeSum / collection.length;
	}


	private static <T> int count(T[] collection, ValueProvider<Boolean, T> valueProvider) {
		int count = 0;
		for (T t : collection) {
			if (valueProvider.getValue(t))
				count++;
		}
		return count;
	}







	@FunctionalInterface
	private interface Client {


		PlayerMoveResult makeMove(GameState currentBoard);

	}




	private static class RunningResult {


		public boolean draw;
		public int winner;
		public int p1score;
		public int p2score;
		public PlayerMoveResult[] player1Results;
		public PlayerMoveResult[] player2Results;


	}




	public static class PlayerMoveResult {


		public int selectedAmbo = -1;
		public int depthReached = -1;
		public int utilityValue = -1;


		public PlayerMoveResult() {
		}


		public PlayerMoveResult(int selectedAmbo) {
			this.selectedAmbo = selectedAmbo;
		}


	}




	@FunctionalInterface
	private interface ValueProvider<R, T> {


		R getValue(T item);


	}


}
