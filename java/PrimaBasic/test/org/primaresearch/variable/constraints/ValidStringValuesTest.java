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
package org.primaresearch.variable.constraints;

import static org.junit.Assert.*;

import org.primaresearch.shared.variable.StringValue;
import org.primaresearch.shared.variable.constraints.ValidStringValues;

import org.junit.Test;

/**
 * Test for variable constraint that has a list of valid string values.
 * 
 * @author Christian Clausner
 *
 */
public class ValidStringValuesTest {

	@Test
	public void testIsValid() {
		ValidStringValues constraint = new ValidStringValues();
		
		//No valid values
		try {
			constraint.isValid(new StringValue("Invalid"));
			fail("Exception was expected");
		} catch(IllegalArgumentException exc) {
			//expected
		}
		assertTrue(constraint.isValid(null));

		//Add valid values
		constraint.addValidValue("valid1");
		constraint.addValidValue("valid2");
		constraint.addValidValue("valid3");
		assertTrue(constraint.isValid(new StringValue("valid1")));
		assertTrue(constraint.isValid(new StringValue("valid2")));
		assertTrue(constraint.isValid(new StringValue("valid3")));
		try {
			constraint.isValid(new StringValue("Invalid"));
			fail("Exception was expected");
		} catch(IllegalArgumentException exc) {
			//expected
		}
	}

	@Test
	public void testGetValidValue() {
		ValidStringValues constraint = new ValidStringValues();
		constraint.addValidValue("valid1");
		constraint.addValidValue("valid2");
		constraint.addValidValue("valid3");

		//Valid value (value should stay the same)
		assertEquals(new StringValue("valid1"), constraint.getValidValue(new StringValue("valid1")));
		
		//Invalid value (null should be returned as the invalid value cannot be turned into a valid value)
		assertNull(constraint.getValidValue(new StringValue("Invalid")));
	}

	@Test
	public void testClone() {
		//Create
		ValidStringValues constraint = new ValidStringValues();
		constraint.addValidValue("valid1");
		constraint.addValidValue("valid2");
		constraint.addValidValue("valid3");

		//Clone
		ValidStringValues copy = (ValidStringValues)constraint.clone();
		assertNotNull(copy);
		
		//Test clone
		assertTrue(copy.isValid(new StringValue("valid1")));
		assertTrue(copy.isValid(new StringValue("valid2")));
		assertTrue(copy.isValid(new StringValue("valid3")));
		try {
			copy.isValid(new StringValue("Invalid"));
			fail("Exception was expected");
		} catch(IllegalArgumentException exc) {
			//expected
		}
	}

}
