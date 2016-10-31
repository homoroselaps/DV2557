# Introduction

This document will explain:

* how we represent and implement an internal model of the environment (package `wumpusworld.aiclient.model`)
* how we make assumptions (we optimize first order logic by introducing *actions*)
* how assumption-making is implemented in our solution (package `wumpusworld.aiclient.assumptionmaking`)


# Internal Model of the Environment

The Wumpus World can be represented as a grid of cells/fields/chunks, where each chunk contains some (or none) following properties:

* Stench    (St)
* Wumpus    (W)
* Breeze    (B)
* Pit       (P)
* Glitter   (Gt)
* Gold      (Gd)

Each of these can be represented with a boolean value of **true** or **false**. However, such representation is only suitable when the environment is fully observable. If the environment is only partially observable (as it is in our case), all of these properties can be unknown at a certain point in time.

Therefore, we should introduce another state of each property for every chunk, which is **unknown**. This means each property of every chunk can be represented with exactly one of the following:

* **false** (deterministic value) - represents absence of the property in a chunk (eg. "There is no Wumpus in this chunk. It's safe!")
* **true** (deterministic value) - represents presence of the property in a chunk (eg. "There is Wumpus in this chunk. Beware!")
* **unknown** (non-deterministic value) - represents the absence of knowing whether a chunk is or is not present in a chunk (eg. "Wumpus might be in this chunk. Beware!") Not that this state should not be considered as a state, that holds, a *secret* or unknown  value, which will be (or may be) known in the future. While this is (in our case) true, it would be more accurate to say that this value represents a state which's status (presence or absence of a property) cannot be determined.

These values represent their own three-value (3V) type, which cannot be converted to a boolean type. They introduce a three-valued logic (3VL) strategy to our problem.

If we used first order logic and boolean values to represent states of chunks, we could accumulate known facts in a collection we would call our Knowledge Base (KB). It would also need to contain information about which chunks have already been visited. But our 3VL approach to the problem would mean our KB would need to store a 3V state for each property for every chunk from the beginning. According to this, it would be easier to store these states in a table, which can also be considered an internal representation of the environment. This is better because:

* Data is organized.
* Requesting data associated with a certain chunk does not require iterating through a whole collection of facts.
* No need to store complex facts or logic sentences of relations between chunks (eg. no breeze => no pit in all adjacent chunks) - due to easy chunk access (see previous paragraph), these can simply be evaluated when requested.

Note: stating "A chunk contains X" means X is true for that chunk and "A chunk does not contain X" means X is false for that chunk.

For more information on three-valued logic, please refer to [this Wikipedia page](https://en.wikipedia.org/wiki/Three-valued_logic).


# Assumptions

As the environment changes, we need to search for locations of pits, gold and Wumpus. As all properties are unknown at the beginning unknown, we need to check if we can assign a deterministic value to any of properties. Therefore, we need to analyze situations, in which properties can be assigned a deterministic value.

## Locating Wumpus

Possibilities of assuming the absence of Wumpus in a certain chunk:

* Arrow has been shot through a chunk (but Wumpus has not been killed)
* Wumpus has been killed
* Wumpus has been located in a different chunk
* An adjacent chunk does not contain stench
* The chunk is not an adjacent chunk of a chunk with stench

Possibilities of assuming the presence of Wumpus:

* A chunk with stench property has only one adjacent chunk that may contain Wumpus
* All chunks but one may not contain Wumpus

## Locating Gold

Possibilities of assuming the absence of gold in a certain chunk:

* Gold has already been picked up
* Gold has been located in a different chunk
* Chunk with no glitter is detected

Possibilities of assuming the presence of gold:

* A chunk with glitter property has been located
* All chunks but one may contain gold

## Locating Pits

Possibilities of assuming the absence of pits in a certain chunk:

* An adjacent chunk does not contain breeze

Possibilities of assuming the presence of pits:

* A chunk with breeze property has only one adjacent chunk that may contain a pit


# Assumption Functions

Note: since possibilities of assuming absence of presence of pits and gold are simpler or event same as the ones for Wumpus, we are only going to cover those related to Wumpus.

Assumptions are logic statements, which are always tautologies.

We can represent properties as **property functions**. Property functions are functions, that return related property's value of a chunk, which is passed as the function's argument. For example, W(X) returns a value indicating the state of Wumpus in chunk X.

To be able to make correct assumptions, we need to define a few other functions as well:

* SHOT_THROUGH(X) - Whether cell X has been shot through
* ADJ(X, Y) - Whether X and Y are adjacent chunks
* HAS(X) - For X being Wumpus or gold, this function returns whether Wumpus is still alive or Gold is still present (has not been picked up yet)
* HAS_LOC(X) - For X being Wumpus or gold, this function returns whether X has been located on the map
* CHUNK(X) - For X being Wumpus or gold, this function returns the chunk in which Wumpus or gold is located (note that this can only be used if the HAS_LOC(X) condition is satisfied)
* ONLY_ADJ(X, Y, Z) - Whether Y is the only adjacent chunk of X which satisfies the condition of Z
* HAS_ONLY(X) - Whether there is only one chunk on the map that satisfies the condition X
* ONLY_CELL(X) - Returns the only chunk, which satisfies the condition of X (note that this can only be used if HAS_ONLY(X) condition is satisfied)

Note: we say that a 3V variable satisfies a certain condition if and only if the value of the variable is either true or unknown.

With all these functions, we can write statements, similar to Boolean's algebra. Since we are using 3VL, we need to define our operations of conjunction, disjunction, negation and implication in 3VL, which shall be the same as in Łukasiewicz's 3Ł logic. This way, all assumptions can be converted to implications, which are true even if all both LHS and RHS of the implication are unknown. For more information on Łukasiewicz Ł3, please refer to [this Wikipedia page](https://en.wikipedia.org/wiki/Three-valued_logic).

Let's define the following operators:

* !X - not X
* X & Y - X and Y
* X | Y - X or Y
* X => Y - implication, X implies Y

Additionally, 3VL values cannot be converted to Boolean values, but Boolean values can be converted to 3VL values as following:

* true [Boolean] -> true [3VL]
* false [Boolean] -> false [3VL]

We cannot get unknown value when converting boolean value to 3LV value. 

Let's define some terms:

* **Simple property statements** are statements F(X) or !F(X), where F is a property function.
* **Assumption statements** are statements, where only one implication is present. The conclusion of the implication must be a simple property statement. Remember that assumptions (and and therefore assumption statements as well) are always tautologies.

Previously mentioned assumptions (for locating Wumpus) can be rewritten as assumption statements:

* SHOT_THROUGH(X) => !W(X)
* !HAS(W) => !W(X)
* HAS_LOC(W) & (CHUNK(W) != X) => !W(X)
* ADJ(X, Y) & !S(X) => !W(Y)
* !ADJ(X, Y) & S(X) => !W(Y)

* S(X) & ONLY_ADJ(X, Y, W) => W(Y)
* HAS_ONLY(!W) & (ONLY_CELL(!W) == X) => W(X)


# Entailment and Actions

Let's define |= as entailment operator (X |= Y means Y entails from X).

Since A => B <=> A |= B, we can convert assumption statements to **entailment statements** by simply replacing the implication sign with entailment sign. (LHS is called the premise and RHS is called the conclusion of the entailment statement.) Entailment statements are only useful predicates if the premise is true (and not false or undefined). Therefore entailment statements with premise's value other that true should be ignored. Entailment statements are different from assumption statement because they tell us under which conditions the conclusion should be added to KB.

To simplify the premise of entailment statements, we can divide the premise into an optional predicate and none or more pre-predicates. Pre-predicates describe situations, in which it would make sense to evaluate the entailment statement. Predicate (if present) needs to be a simple property statement that represents the condition, from which, if true, we can entail the entailment statement's conclusion. We shall call statements of such form **actions**. We can present actions as functions, which take a certain number of parameters and include a pre-predicate and a simplified entailment statement.

Note: When converting entailment statement to actions, the whole premise of the entailment statement can be moved to pre-predicates, in which case the predicate of the action is empty. This is fine and means that the conclusion of the action is always true (of course, the pre-predicates requirements must still be met).

Let's look at an example assumption statement ADJ(X, Y) & !S(X) => !W(Y) ("If X and Y are adjacent chunks and there is no stench in X, then there is not Wumpus in Y."):

* First, let's convert the assumption statement into an entailment statement: ADJ(X, Y) & !S(X) |= !W(Y) ("If X and Y are adjacent and there is no stench in X, then we've just learned that there is no Wumpus in Y.")
* Let's convert the entailment statement to an action: Aw(X, Y): !S(X) |= !W(Y), ADJ(X, Y). We can now analyze the action:
    * Aw(X, Y) is the action, that takes two chunks as arguments.
        * ADJ(X, Y) is the pre-predicate, which tells us the actions can only be called parameters X and Y, which are adjacent chunks
            * Note: if there are more pre-predicates available, they are separated by commas
        * !S(X) |= !W(Y) is the simplified entailment statement (!S(X) is the predicate and !W(Y) is the conclusion)
            * Note: if there is no predicate in the action, we would write it as: |= F(X)
    * What this action does, is it adds !W(Y) to our KB, when !S(X) is true for adjacent chunks X and Y. Another equivalent description would be: "If there is no stench in a chunk X, then there is no Wumpus in the (adjacent) chunk Y."

Actions are called the way they are because they actually do something - they broaden the horizon of our KB by adding facts to it.

We can convert previous assumption statements to actions:

* Aw0(X): |= !W(X), SHOT_THROUGH(X)
* Aw1(X): |= !W(X), !HAS(W)
* Aw2(X): |= !W(X), HAS_LOC(W), CHUNK(W) != X
* Aw3(X, Y): !S(X) |= !W(Y), ADJ(X, Y)
* Aw4(X, Y): S(X) |= !W(Y), !ADJ(X, Y)

* Aw5(X, Y): S(X) |= W(Y), ONLY_ADJ(X, Y, W)
* Aw6(X): |= W(X), HAS_ONLY(!W), ONLY_CELL(!W) == X


# Taking Actions

To evaluate the entailment statement in an action, we need to invoke the action. Doing so may expand out KB. But we do not need to invoke every action every turn of the game for every possible input. Instead, according to their pre-predicates, we can determine when certain actions need to be invoked.

In our example, we need to invoke actions in one or both of the following scenarios:

* When the value of any pre-predicate changes to true
* When a property, that is used in any predicate, changes in any of chunks, that are included in the pre-predicate

We can now summarize the list of actions and figure out, when a certain action needs to be invoked and with which parameters

| Action | Invocation | Parameters |
| ------ | ---------- | ---------- |
| Aw0      | When the arrow is shot (but Wumpus isn't killed) | All chunks the arrow was shot through |
| Aw1    | When Wumpus gets killed | All chunks |
| Aw2    | When Wumpus is located  | All chunks except the chunk with Wumpus |
| Aw3    | When a chunk with no stench is discovered | X = the chunk the player moved into <br> Y = all (undiscovered) adjacent chunks |
| Aw4    | When a chunk with stench is discovered | X = the chunk with the stench <br> Y = all (undiscovered) chunks that aren't adjacent to X |
| Aw5    | When a chunk with stench has only one adjacent chunk where Wumpus may be located left | X = the chunk with the stench <br> Y = the only adjacent chunk, where Wumpus may be located |
| Aw6    | When there is only one chunk remaining, where Wumpus may be located | The remaining chunk |

Similar actions apply for locating gold and pits.


# Conclusion

Probably the easiest way of storing our KB is with an internal model of the map, where we represent properties with 3V values.

By transforming our assumptions to actions, we have managed to analyze, when we need to perform certain checks, which may lead to useful conclusions of locating Wumpus, gold or pits.


# Implementation

Should we use any logic-languages to describe our actions? Our implementation needs to fulfil the following two sections: invoking actions and carrying out action's entailment sentence. The first is implementation-dependant and it would make no sense to use any logic-languages to implement it. The second part only contains a simple entailment statement. Checking if it's premise is true only takes one line of code and adding the conclusion to our KB is also implementation-dependant. Therefore it makes no sense to use any logic-languages to implement our actions.

In our case, there are several classes and interfaces that exist in order to implement all the actions. They can be found in `wumpusworld.aiclient.assumptionmaking` package.

* `AssumptionMaker` - (interface) describes the general idea of which functionalities assumption-making classes should have. Please refer to code documentation for more information.
* `WumpusAssumptionMaker` - manages and executes actions related to Wumpus.
* `PitAssumptionMaker` - manages and executes actions related to pits.
* `GoldAssumptionMaker` - manages and executes actions related to gold.
* `AssumptionManager` - contains and manages all three assumption-making classes listed above.

Each of the three assumption-making classes contain three private methods for each action associated with it. All methods have parameters that are related to the context of associated action. Methods' names contain a prefix, which determines what the method does.

* `*init*SomeAction` - checks if a deterministic value of the associated property (Wumpus, Pit or Gold) can already be assigned to any chunks. It also initializes any data associated with the action.
* `*invoke*SomeAction` - checks if premise of the associated action is true. If is is, it calls the `*on*SomeAction` method.
* `*on*SomeAction` - adds the conclusion of the associated action's premise to KB

These methods are *only* called when it makes sense to call them.

* `*init*SomeAction` - when the assumption-making class is initializing (or resetting).
* `*invoke*SomeAction` - when the pre-predicates are met. This is either when a new chunk has been explored, a percept of any chunk has changed, or an action has been made by the player (such as *move* or *shoot*).
* `*on*SomeAction` - when pre-predicates and premise of the associated action are true

Note: the last two methods may sometimes also be called from the first one, if that makes sense. (This is only a matter of implementation - we don't need same code in two methods).

For example, let's take a look at the methods associated with Wumpus and Aw3 action.

* `initChunkWithNoStenchIsDiscovered` - invokes `onChunkWithNoStenchIsDiscovered` for every chunk for which we already know it does not contain stench (this might usually be the chunk at player's starting position).
* `invokeChunkWithNoStenchIsDiscovered(Chunk chunk)` - checks if the `chunk` does not contain stench. If it does not, calls `onChunkWithNoStenchIsDiscovered(chunk)` for that chunk.
* `onChunkWithNoStenchIsDiscovered(Chunk chunk)` - add !W(X) to KB, for every X which is an adjacent chunk of the argument `chunk`.

Note: these methods in the assumption-making classes are grouped by their prefix and are listed in the same order as actions are listed in this document.