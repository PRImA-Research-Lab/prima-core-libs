/*
 * Copyright 2015 PRImA Research Lab, University of Salford, United Kingdom
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

import static org.junit.Assert.*;

import org.junit.Test;
import org.primaresearch.shared.variable.DoubleValue;
import org.primaresearch.shared.variable.DoubleVariable;
import org.primaresearch.shared.variable.Variable.WrongVariableTypeException;

public class DoubleVariableTest {

	@Test
	public void testDoubleVariableString() {
		DoubleVariable v = new DoubleVariable("TestName");
		assertEquals("TestName", v.getName());
		assertNotNull(v.getValue());
	}

	@Test
	public void testDoubleVariableStringDoubleValue() {
		DoubleVariable v = new DoubleVariable("TestName", new DoubleValue(1));
		assertEquals("TestName", v.getName());
		assertEquals(new DoubleValue(1), v.getValue());
	}

	@Test
	public void testGetValue() {
		DoubleVariable v = new DoubleVariable("TestName", new DoubleValue(1));
		assertEquals(new DoubleValue(1), v.getValue());
	}

	@Test
	public void testSetValue() {
		DoubleVariable v = new DoubleVariable("TestName", new DoubleValue(1));
		assertEquals(new DoubleValue(1), v.getValue());
		try {
			v.setValue(new DoubleValue(2));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
		assertEquals(new DoubleValue(2), v.getValue());
	}

	@Test
	public void testParseValue() {
		DoubleVariable v = new DoubleVariable("TestName");
		v.parseValue("5");
		assertEquals(new DoubleValue(5), v.getValue());
		v.parseValue("]");
		assertEquals(new DoubleValue(0), v.getValue());
	}

	@Test
	public void testClone() {
		DoubleVariable v = new DoubleVariable("TestName", new DoubleValue(1));
		v.setCaption("TestCap");
		DoubleVariable copy = (DoubleVariable) v.clone();
		assertNotNull(copy);
		assertEquals(v.getName(), copy.getName());
		assertEquals(v.getCaption(), copy.getCaption());
		assertEquals(v.getValue(), copy.getValue());
	}

}
