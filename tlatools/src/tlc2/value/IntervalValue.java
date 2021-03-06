// Copyright (c) 2003 Compaq Corporation.  All rights reserved.
// Portions Copyright (c) 2003 Microsoft Corporation.  All rights reserved.
// Last modified on Wed 12 Jul 2017 at 16:10:00 PST by ian morris nieves
//      modified on Sat 23 February 2008 at 10:12:59 PST by lamport
//      modified on Fri Aug 10 15:07:36 PDT 2001 by yuanyu

package tlc2.value;

import java.io.IOException;

import tlc2.tool.FingerprintException;
import tlc2.util.FP64;
import util.Assert;

public class IntervalValue extends EnumerableValue
implements Enumerable, Reducible {
  public int low, high;   // the integer interval [low, high]

  /* Constructor */
  public IntervalValue(int low, int high) {
    this.low = low;
    this.high = high;
  }

  public final byte getKind() { return INTERVALVALUE; }

  public final int compareTo(Object obj) {
    try {
      if (obj instanceof IntervalValue) {
        IntervalValue intv = (IntervalValue)obj;
        int cmp = this.size() - intv.size();
        if (cmp != 0) return cmp;
        if (this.size() == 0) return 0;
        return this.low - intv.low;
      }
      // Well, we have to convert them to sets and compare.
      return this.toSetEnum().compareTo(obj);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final boolean equals(Object obj) {
    try {
      if (obj instanceof IntervalValue) {
        IntervalValue intv = (IntervalValue)obj;
        if (this.size() == 0) return intv.size() == 0;
        return (this.low == intv.low) && (this.high == intv.high);
      }
      // Well, we have to convert them to sets and compare.
      return this.toSetEnum().equals(obj);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final boolean member(Value elem) {
    try {
      if (elem instanceof IntValue) {
        int x = ((IntValue)elem).val;
        return (x >= low) && (x <= high);
      }
      if (   (this.low <= this.high)
           && (   !(elem instanceof ModelValue)
               || (((ModelValue) elem).type != 0)) ) {
        Assert.fail("Attempted to check if the value:\n" + ppr(elem.toString()) +
        "\nis in the integer interval " + ppr(this.toString()));
      }
      return false;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public Value isSubsetEq(Value other) {
    try {
      if (other instanceof IntervalValue) {
        final IntervalValue iv = (IntervalValue) other;
        if (iv.low <= low && iv.high >= high) {
          return ValTrue;
        }
      }
      return super.isSubsetEq(other);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final boolean isFinite() { return true; }

  public final int size() {
    try {
      if (this.high < this.low) return 0;
      return this.high - this.low + 1;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

	/**
	 * @return Converts this IntervalValue instance into a Value[]. This can be seen
	 *         as the inverse to the performance optimization that the IntervalValue
	 *         actually is.
	 */
	final Value[] asValues() {
		final Value[] values = new Value[size()];
		for (int i = 0; i < size(); i++) {
			values[i] = IntValue.gen(this.low + i);
		}
		return values;
	}
  
  /* Return this - val.  */
  public final Value diff(Value val) {
    try {
      ValueVec diffElems = new ValueVec();
      for (int i = this.low; i <= this.high; i++) {
        Value elem = IntValue.gen(i);
        if (!val.member(elem)) diffElems.addElement(elem);
      }
      return new SetEnumValue(diffElems, true);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  /* Return this \cap val. */
  public final Value cap(Value val) {
    try {
      ValueVec capElems = new ValueVec();
      for (int i = this.low; i <= this.high; i++) {
        Value elem = IntValue.gen(i);
        if (val.member(elem)) capElems.addElement(elem);
      }
      return new SetEnumValue(capElems, true);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  /* Return this \cup val.  */
  public final Value cup(Value set) {
    try {
      if (this.size() == 0) return set;

      if (set instanceof Reducible) {
        ValueVec cupElems = new ValueVec();
        for (int i = this.low; i <= this.high; i++) {
          cupElems.addElement(IntValue.gen(i));
        }
        ValueEnumeration Enum = ((Enumerable)set).elements();
        Value elem;
        while ((elem = Enum.nextElement()) != null) {
          if (!this.member(elem)) cupElems.addElement(elem);
        }
        return new SetEnumValue(cupElems, false);
      }
      return new SetCupValue(this, set);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final Value takeExcept(ValueExcept ex) {
    try {
      if (ex.idx < ex.path.length) {
        Assert.fail("Attempted to apply EXCEPT construct to the interval value " +
        ppr(this.toString()) + ".");
      }
      return ex.value;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final Value takeExcept(ValueExcept[] exs) {
    try {
      if (exs.length != 0) {
        Assert.fail("Attempted to apply EXCEPT construct to the interval value " +
        ppr(this.toString()) + ".");
      }
      return this;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final boolean isNormalized() { return true; }

  public final Value normalize() { /*nop*/return this; }

  public final boolean isDefined() { return true; }

  public final Value deepCopy() { return this; }

  public final boolean assignable(Value val) {
    try {
      return ((val instanceof IntervalValue) &&
        this.high == ((IntervalValue)val).high &&
        this.low == ((IntervalValue)val).low);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

	@Override
	public void write(final ValueOutputStream vos) throws IOException {
		vos.writeByte(INTERVALVALUE);
		vos.writeInt(low);
		vos.writeInt(high);
	}

  /* The fingerprint method */
  public final long fingerPrint(long fp) {
    try {
      fp = FP64.Extend(fp, SETENUMVALUE);
      fp = FP64.Extend(fp, this.size()) ;
      for (int i = this.low; i <= this.high; i++) {
        fp = FP64.Extend(fp, INTVALUE);
        fp = FP64.Extend(fp, i);
      }
      return fp;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final Value permute(MVPerm perm) {
    return this;
  }

  @Override
  public SetEnumValue toSetEnum() {
      Value[] vals = new Value[size()];
      for (int i = 0; i < vals.length; i++) {
        vals[i] = IntValue.gen(i + this.low);
      }
      return new SetEnumValue(vals, true);
  }

  /* The string representation */
  public final StringBuffer toString(StringBuffer sb, int offset) {
    try {
      if (this.low <= this.high) {
        return sb.append(this.low).append("..").append(this.high);
      }
      return sb.append("{").append("}");
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

    @Override
	public EnumerableValue getRandomSubset(final int kOutOfN) {
    	final ValueVec vec = new ValueVec(kOutOfN);
    	
    	final ValueEnumeration ve = elements(kOutOfN);
    	
    	Value v = null;
    	while ((v = ve.nextElement()) != null) {
    		vec.addElement(v);
    	}
    	return new SetEnumValue(vec, false);
	}

	public Value elementAt(final int idx) {
		if (0 <= idx && idx < size()) {
			return IntValue.gen(low + idx);
		}
		Assert.fail(
				"Attempted to retrieve out-of-bounds element from the interval value " + ppr(this.toString()) + ".");
        return null; // make compiler happy
	}
    
  public final ValueEnumeration elements() {
    try {
      return new Enumerator();
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  final class Enumerator implements ValueEnumeration {
    int index = low;

    public final void reset() { this.index = low; }

    public final Value nextElement() {
      if (this.index <= high) {
        return IntValue.gen(this.index++);
      }
      return null;
    }

  }
  
	@Override
	public ValueEnumeration elements(final int kOutOfN) {
		return new EnumerableValue.SubsetEnumerator(kOutOfN) {
			@Override
			public Value nextElement() {
				if (!hasNext()) {
					return null;
				}
				return IntValue.gen(low + nextIndex());
			}
		};
	}
}
