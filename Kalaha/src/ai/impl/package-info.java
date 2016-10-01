/**
 *  <h1>AI logic</h1>
 *  <hr>
 *  <h2>Quick links</h2>
 *  <p>
 *      For <b>Iterative Deepening</b>, see {@link ai.impl.AIClientManager#run(ai.impl.DepthLevelSupplier)},
 *      which instructs {@link ai.impl.NodeBuilder} to run depth-first search to a certain depth-level,
 *      and {@link ai.impl.NodeBuilder#step(ai.impl.Node, ai.impl.PruningCallback)},
 *      which performs the depth-first search.
 *  </p>
 *  <p>
 *      For <b>Pruning</b>, see {@link ai.impl.PruningManager}, which handles the pruning logic that
 *      gets implemented in {@link ai.impl.NodeBuilder#step(ai.impl.Node, ai.impl.PruningCallback)}.
 *  </p>
 *  <p>
 *      For <b>Utility Value</b> related things, see {@link ai.impl.UtilityValueManager}, which's
 *      logic is implemented in {@link ai.impl.NodeBuilder#step(ai.impl.Node, ai.impl.PruningCallback)}.
 *  </p>
 *  <h2>Notes</h2>
 *  <p>
 *      The API holds an internal representation of player. It uses {@link java.lang.Boolean} type rather than
 *      then @link {@link java.lang.Integer}, which is used by {@link kalaha.GameState}.
 *  </p>
 *  <p>
 *      This is because {@link kalaha.GameState} needs to distinguish before first and second player, but
 *      our API needs to distinguish between the AI player (maximizer) and the opponent (minimizer). Therefore,
 *      the {@code true} value represents the first and {@code false} represents the latter.
 *  </p>
 *  <p>
 *      To convert the the {@link java.lang.Boolean} player representation to the {@link java.lang.Integer}
 *      player representation, the following methods are used:
 *  </p>
 *  <ul>
 *      <li>{@link ai.impl.AIClientManager#getMaxPlayer()}</li>
 *      <li>{@link ai.impl.AIClientManager#getMinPlayer()}</li>
 *  </ul>
 *  <h2>Workflow</h2>
 *  <ol>
 *      <li>An instance of {@link ai.impl.AIClientManager} is created - it holds the whole logic together.</li>
 *      <li>An instance of {@link ai.impl.StartArrayDepthLevelSupplier} is created - it will tell us how many
 *          levels of the MiniMaxTree do we need to form in each iteration of Iterative Deepening.</li>
 *      <li>{@link ai.impl.AIClientManager#run(ai.impl.DepthLevelSupplier)} is called.
 *          <ol>
 *              <li>An instance of {@link ai.impl.NodeBuilder} is created - it will allow us to explore the
 *                  MiniMaxTree in a depth-first-search manner.</li>
 *              <li>The {@link ai.impl.NodeBuilder#run(int)} is called, which calls the recursively-called method
 *                  {@link ai.impl.NodeBuilder#step(ai.impl.Node, ai.impl.PruningCallback)}.</li>
 *              <li>{@link ai.impl.NodeBuilder#step(ai.impl.Node, ai.impl.PruningCallback)} handles all the logic
 *                  for expanding nodes, retrieving their utility values and pruning (yes, double-moves are taken
 *                  into account in the pruning logic).</li>
 *              <li>{@link ai.impl.AIClientManager} also creates a {@link ai.impl.util.CancellationTimer}, which
 *                  cancels the search performed by {@link ai.impl.NodeBuilder}.</li>
 *          </ol>
 *      </li>
 *      <li>We obtain and return the selected ambo index by calling the {@link ai.impl.AIClientManager#getSelectedMove()}.</li>
 *  </ol>
 *  <hr>
 *  <p>Created by Nejc on 1. 10. 2016.</p>
 */
package ai.impl;
