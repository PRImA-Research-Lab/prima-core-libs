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
import org.primaresearch.shared.variable.StringValue;
import org.primaresearch.shared.variable.StringVariable;
import org.primaresearch.shared.variable.Variable.WrongVariableTypeException;
import org.primaresearch.shared.variable.constraints.ValidStringValues;

public class StringVariableTest {

	@Test
	public void testStringVariableString() {
		StringVariable v = new StringVariable("TestName");
		assertEquals("TestName", v.getName());
		assertNotNull(v.getValue());
	}

	@Test
	public void testStringVariableStringStringValue() {
		StringVariable v = new StringVariable("TestName", new StringValue("1"));
		assertEquals("TestName", v.getName());
		assertEquals(new StringValue("1"), v.getValue());
	}

	@Test
	public void testGetValue() {
		StringVariable v = new StringVariable("TestName", new StringValue("1"));
		assertEquals(new StringValue("1"), v.getValue());
	}

	@Test
	public void testSetValue() {
		StringVariable v = new StringVariable("TestName", new StringValue("1"));
		assertEquals(new StringValue("1"), v.getValue());
		try {
			v.setValue(new StringValue("2"));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
		assertEquals(new StringValue("2"), v.getValue());
	}

	@Test
	public void testParseValue() {
		StringVariable v = new StringVariable("TestName");
		v.parseValue("5");
		assertEquals(new StringValue("5"), v.getValue());
	}

	@Test
	public void testClone() {
		StringVariable v = new StringVariable("TestName", new StringValue("1"));
		v.setCaption("TestCap");
		StringVariable copy = (StringVariable) v.clone();
		assertNotNull(copy);
		assertEquals(v.getName(), copy.getName());
		assertEquals(v.getCaption(), copy.getCaption());
		assertEquals(v.getValue(), copy.getValue());
	}

	@Test
	public void testGetValidValues() {
		StringVariable v = new StringVariable("TestName", new StringValue("1"));

		assertNull(v.getValidValues());
		
		ValidStringValues constraint = new ValidStringValues();
		constraint.addValidValue("1");
		
		v.setConstraint(constraint);
		
		assertNotNull(v.getValidValues());
		assertEquals(1, v.getValidValues().size());
	}

}
