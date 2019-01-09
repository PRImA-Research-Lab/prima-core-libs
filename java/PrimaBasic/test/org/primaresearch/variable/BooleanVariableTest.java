/*
 * Copyright 2019 PRImA Research Lab, University of Salford, United Kingdom
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
import org.primaresearch.shared.variable.BooleanValue;
import org.primaresearch.shared.variable.BooleanVariable;
import org.primaresearch.shared.variable.Variable.WrongVariableTypeException;

public class BooleanVariableTest {

	@Test
	public void testBooleanVariableString() {
		BooleanVariable v = new BooleanVariable("TestName");
		assertEquals("TestName", v.getName());
		assertNotNull(v.getValue());
	}

	@Test
	public void testBooleanVariableStringBooleanValue() {
		BooleanVariable v = new BooleanVariable("TestName", new BooleanValue(true));
		assertEquals("TestName", v.getName());
		assertEquals(new BooleanValue(true), v.getValue());
	}

	@Test
	public void testGetValue() {
		BooleanVariable v = new BooleanVariable("TestName", new BooleanValue(true));
		assertEquals(new BooleanValue(true), v.getValue());
	}

	@Test
	public void testSetValue() {
		BooleanVariable v = new BooleanVariable("TestName", new BooleanValue(true));
		assertEquals(new BooleanValue(true), v.getValue());
		try {
			v.setValue(new BooleanValue(false));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
		assertEquals(new BooleanValue(false), v.getValue());
	}

	@Test
	public void testParseValue() {
		BooleanVariable v = new BooleanVariable("TestName");
		v.parseValue("true");
		assertEquals(new BooleanValue(true), v.getValue());
		v.parseValue("]");
		assertEquals(new BooleanValue(false), v.getValue());
	}

	@Test
	public void testClone() {
		BooleanVariable v = new BooleanVariable("TestName", new BooleanValue(true));
		v.setCaption("TestCap");
		BooleanVariable copy = (BooleanVariable) v.clone();
		assertNotNull(copy);
		assertEquals(v.getName(), copy.getName());
		assertEquals(v.getCaption(), copy.getCaption());
		assertEquals(v.getValue(), copy.getValue());
	}

}
