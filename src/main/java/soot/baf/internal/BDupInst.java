/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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

package soot.baf.internal;

import java.util.Iterator;

import soot.AbstractJasminClass;
import soot.Type;
import soot.baf.DupInst;
import soot.util.Switch;

public abstract class BDupInst extends AbstractInst implements DupInst {

  public int getInCount() {
    return getUnderTypes().size() + getOpTypes().size();
  }

  public int getInMachineCount() {
    int count = 0;

    Iterator<Type> underTypesIt = getUnderTypes().iterator();
    while (underTypesIt.hasNext()) {
      count += AbstractJasminClass.sizeOfType(underTypesIt.next());
    }

    Iterator<Type> opTypesIt = getOpTypes().iterator();
    while (opTypesIt.hasNext()) {
      count += AbstractJasminClass.sizeOfType(opTypesIt.next());
    }

    return count;
  }

  public int getOutCount() {
    return getUnderTypes().size() + 2 * getOpTypes().size();
  }

  public int getOutMachineCount() {
    int count = 0;

    Iterator<Type> underTypesIt = getUnderTypes().iterator();
    while (underTypesIt.hasNext()) {
      count += AbstractJasminClass.sizeOfType(underTypesIt.next());
    }

    Iterator<Type> opTypesIt = getOpTypes().iterator();
    while (opTypesIt.hasNext()) {
      count += 2 * AbstractJasminClass.sizeOfType(opTypesIt.next());
    }
    return count;
  }

  public void apply(Switch sw) {
    throw new RuntimeException();
  }

}
