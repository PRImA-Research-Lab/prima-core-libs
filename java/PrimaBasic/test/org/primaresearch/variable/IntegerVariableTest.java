/*
 * Copyright 2014 PRImA Research Lab, University of Salford, United Kingdom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primaresearch.variable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.primaresearch.shared.variable.IntegerValue;
import org.primaresearch.shared.variable.IntegerVariable;
import org.primaresearch.shared.variable.Variable.WrongVariableTypeException;

public class IntegerVariableTest {

	@Test
	public void testIntegerVariableString() {
		IntegerVariable v = new IntegerVariable("TestName");
		assertEquals("TestName", v.getName());
		assertNotNull(v.getValue());
	}

	@Test
	public void testIntegerVariableStringIntegerValue() {
		IntegerVariable v = new IntegerVariable("TestName", new IntegerValue(1));
		assertEquals("TestName", v.getName());
		assertEquals(new IntegerValue(1), v.getValue());
	}

	@Test
	public void testGetValue() {
		IntegerVariable v = new IntegerVariable("TestName", new IntegerValue(1));
		assertEquals(new IntegerValue(1), v.getValue());
	}

	@Test
	public void testSetValue() {
		IntegerVariable v = new IntegerVariable("TestName", new IntegerValue(1));
		assertEquals(new IntegerValue(1), v.getValue());
		try {
			v.setValue(new IntegerValue(2));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
		assertEquals(new IntegerValue(2), v.getValue());
	}

	@Test
	public void testParseValue() {
		IntegerVariable v = new IntegerVariable("TestName");
		v.parseValue("5");
		assertEquals(new IntegerValue(5), v.getValue());
		v.parseValue("]");
		assertEquals(new IntegerValue(0), v.getValue());
	}

	@Test
	public void testClone() {
		IntegerVariable v = new IntegerVariable("TestName", new IntegerValue(1));
		v.setCaption("TestCap");
		IntegerVariable copy = (IntegerVariable) v.clone();
		assertNotNull(copy);
		assertEquals(v.getName(), copy.getName());
		assertEquals(v.getCaption(), copy.getCaption());
		assertEquals(v.getValue(), copy.getValue());
	}

}
