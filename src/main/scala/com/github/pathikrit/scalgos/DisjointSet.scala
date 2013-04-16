package com.github.pathikrit.scalgos

import collection.mutable

/**
 * Each internal node in DisjointSet
 */
private class Node[A](val entry: A) {
  /**
   * parent - the pointer to root node (by default itself)
   * rank - depth if we did not do path compression in find - else its upper bound on the distance from node to parent
   */
  var (parent, rank) = (this, 0)

  def root: Node[A] = {
    if (parent != this) {
      parent = parent.root     // path compression
    }
    parent
  }
}

/**
 * A disjoint-set data structure (also called union-find data structure)
 * Has efficient union and find operations in amortised O(a(n)) time (where a is the inverse-Ackermann function)
 * TODO: Support delete
 *
 * @tparam A types of things in set
 */
class DisjointSet[A] {
  private val index = mutable.Map.empty[A, Node[A]]

  private implicit def toNode(x: A) = {
    assume(contains(x))
    index(x)
  }

  /**
   * @return true iff x is known
   */
  def contains(x: A) = index contains x

  /**
   * Add a new singleton set with only x in it (assuming x is not already known)
   */
  def makeSet(x: A) {
    assume(!contains(x))
    index(x) = new Node(x)
  }

  /**
   * Union the sets containing x and y
   */
  def union(x: A, y: A) {
    val (xRoot, yRoot) = (x.root, y.root)
    if (xRoot != yRoot) {
      if (xRoot.rank < yRoot.rank) {        // change the root of the shorter/less-depth one
        xRoot.parent = yRoot
      } else if (xRoot.rank > yRoot.rank) {
        yRoot.parent = xRoot
      } else {
        yRoot.parent = xRoot
        xRoot.rank += 1   // else if there is tie, increment
      }
    }
  }

  /**
   * @return the root (or the canonical element that contains x)
   */
  def find(x: A) = x.root.entry

  /**
   * @return Iterator over groups of items in same set
   */
  def sets = index.keys groupBy {_.root.entry} values
}
