package org.primaresearch.variable.constraints;

import static org.junit.Assert.*;

import org.junit.Test;
import org.primaresearch.shared.variable.DoubleValue;
import org.primaresearch.shared.variable.DoubleVariable;
import org.primaresearch.shared.variable.IntegerValue;
import org.primaresearch.shared.variable.IntegerVariable;
import org.primaresearch.shared.variable.constraints.MinMaxConstraint;

public class MinMaxConstraintTest {

	@Test
	public void test() {
		try {
			//Integer
			IntegerVariable v1 = new IntegerVariable("v1");
			v1.setValue(new IntegerValue(100));
			
			v1.setConstraint(new MinMaxConstraint(new IntegerValue(0), new IntegerValue(50)));
			
			//Cap?
			assertTrue(((IntegerValue)v1.getValue()).val == 50);
			
			v1.setValue(new IntegerValue(-10));
			assertTrue(((IntegerValue)v1.getValue()).val == 0);
			
			//Double
			DoubleVariable v2 = new DoubleVariable("v1");
			v2.setValue(new DoubleValue(100.0));
			
			v2.setConstraint(new MinMaxConstraint(new DoubleValue(0.0), new DoubleValue(50.0)));
			
			//Cap?
			assertTrue(((DoubleValue)v2.getValue()).val == 50.0);
			
			v2.setValue(new DoubleValue(-10.0));
			assertTrue(((DoubleValue)v2.getValue()).val == 0.0);
			
		} catch(Exception exc) {
			exc.printStackTrace();
			fail(exc.toString());
		}
	}

}
