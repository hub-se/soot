/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.toolkits.scalar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.Singletons;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.options.Options;
import soot.util.Chain;

/**
 * A BodyTransformer that removes all unused local variables from a given Body. Implemented as a singleton.
 * 
 * @see BodyTransformer
 * @see Body
 */
public class UnusedLocalEliminator extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(UnusedLocalEliminator.class);

  public UnusedLocalEliminator(Singletons.Global g) {
  }

  public static UnusedLocalEliminator v() {
    return G.v().soot_toolkits_scalar_UnusedLocalEliminator();
  }

  @Override
  protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
    if (Options.v().verbose()) {
      logger.debug("[" + body.getMethod().getName() + "] Eliminating unused locals...");
    }

    int i = 0;
    int n = body.getLocals().size();
    int[] oldNumbers = new int[n];
    Chain<Local> locals = body.getLocals();
    for (Local local : locals) {
      oldNumbers[i] = local.getNumber();
      local.setNumber(i);
      i++;
    }

    boolean[] usedLocals = new boolean[n];

    // Traverse statements noting all the uses and defs
    for (Unit s : body.getUnits()) {
      for (ValueBox vb : s.getUseBoxes()) {
        Value v = vb.getValue();
        if (v instanceof Local) {
          Local l = (Local) v;
          assert locals.contains(l);
          usedLocals[l.getNumber()] = true;
        }
      }
      for (ValueBox vb : s.getDefBoxes()) {
        Value v = vb.getValue();
        if (v instanceof Local) {
          Local l = (Local) v;
          assert locals.contains(l);
          usedLocals[l.getNumber()] = true;
        }
      }
    }

    // Remove all locals that are unused.
    List<Local> keep = new ArrayList<Local>(body.getLocalCount());
    for (Local local : locals) {
      int lno = local.getNumber();
      local.setNumber(oldNumbers[lno]);
      if (usedLocals[lno]) {
        keep.add(local);
      }
    }
    body.getLocals().clear();
    body.getLocals().addAll(keep);
  }

}
