/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jimple.toolkits.callgraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import soot.MethodOrMethodContext;
import soot.util.queue.ChunkedQueue;
import soot.util.queue.QueueReader;

/**
 * Keeps track of the methods transitively reachable from the specified entry points through the given call graph edges.
 *
 * @author Ondrej Lhotak
 */
public class ReachableMethods {
  protected CallGraph cg;
  protected Iterator<Edge> edgeSource;
  protected final ChunkedQueue<MethodOrMethodContext> reachables = new ChunkedQueue<>();
  protected final Set<MethodOrMethodContext> set = new HashSet<>();
  protected QueueReader<MethodOrMethodContext> unprocessedMethods;
  protected final QueueReader<MethodOrMethodContext> allReachables = reachables.reader();
  protected Filter filter;

  public ReachableMethods(CallGraph graph, Iterator<? extends MethodOrMethodContext> entryPoints) {
    this(graph, entryPoints, null);
  }

  public ReachableMethods(CallGraph graph, Iterator<? extends MethodOrMethodContext> entryPoints, Filter filter) {
    this.filter = filter;
    this.cg = graph;
    addMethods(entryPoints);
    unprocessedMethods = reachables.reader();
    this.edgeSource = graph.listener();
    if (filter != null) {
      this.edgeSource = filter.wrap(this.edgeSource);
    }
  }

  public ReachableMethods(CallGraph graph, Collection<? extends MethodOrMethodContext> entryPoints) {
    this(graph, entryPoints.iterator());
  }

  protected void addMethods(Iterator<? extends MethodOrMethodContext> methods) {
    while (methods.hasNext()) {
      addMethod(methods.next());
    }
  }

  protected void addMethod(MethodOrMethodContext m) {
    if (set.add(m)) {
      reachables.add(m);
    }
  }

  /**
   * Causes the QueueReader objects to be filled up with any methods that have become reachable since the last call.
   */
  public void update() {
    while (edgeSource.hasNext()) {
      Edge e = edgeSource.next();
      if (set.contains(e.getSrc())) {
        addMethod(e.getTgt());
      }
    }
    while (unprocessedMethods.hasNext()) {
      MethodOrMethodContext m = unprocessedMethods.next();
      Iterator<Edge> targets = cg.edgesOutOf(m);
      if (filter != null) {
        targets = filter.wrap(targets);
      }
      addMethods(new Targets(targets));
    }
  }

  /**
   * Returns a QueueReader object containing all methods found reachable so far, and which will be informed of any new
   * methods that are later found to be reachable.
   */
  public QueueReader<MethodOrMethodContext> listener() {
    return allReachables.clone();
  }

  /**
   * Returns a QueueReader object which will contain ONLY NEW methods which will be found to be reachable, but not those that
   * have already been found to be reachable.
   */
  public QueueReader<MethodOrMethodContext> newListener() {
    return reachables.reader();
  }

  /** Returns true iff method is reachable. */
  public boolean contains(MethodOrMethodContext m) {
    return set.contains(m);
  }

  /** Returns the number of methods that are reachable. */
  public int size() {
    return set.size();
  }
}
