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

import static org.junit.Assert.*;

import org.junit.Test;
import org.primaresearch.shared.variable.BooleanValue;
import org.primaresearch.shared.variable.BooleanVariable;
import org.primaresearch.shared.variable.DoubleValue;
import org.primaresearch.shared.variable.DoubleVariable;
import org.primaresearch.shared.variable.IntegerValue;
import org.primaresearch.shared.variable.IntegerVariable;
import org.primaresearch.shared.variable.StringValue;
import org.primaresearch.shared.variable.StringVariable;
import org.primaresearch.shared.variable.Variable;
import org.primaresearch.shared.variable.VariableMap;

public class VariableMapTest {

	@Test
	public void testVariableMap() {

		Variable v1 = new BooleanVariable("v1", new BooleanValue(true));
		v1.setId(1);
		v1.setSortIndex(3);
		Variable v2 = new IntegerVariable("v2", new IntegerValue(10));
		v2.setId(2);
		v2.setSortIndex(1);
		Variable v3 = new DoubleVariable("v3", new DoubleValue(0.5));
		v3.setId(3);
		v3.setSortIndex(4);
		Variable v4 = new StringVariable("v4", new StringValue("str"));
		v4.setId(4);
		v4.setSortIndex(2);

		//Create
		VariableMap map = new VariableMap();
		map.setName("name");
		map.setType("type");
		
		assertTrue("Empty new map", map.getSize() == 0);
		assertTrue("Correct map name", "name".equals(map.getName()));
		assertTrue("Correct map type", "type".equals(map.getType()));
		
		//Add
		map.add(v1);
		map.add(v2);
		map.add(v3);
		map.add(v4);
		assertTrue("Full map", map.getSize() == 4);
		
		//Get by index
		assertTrue("Get by index 0", map.get(0) == v1);
		assertTrue("Get by index 1", map.get(1) == v2);
		assertTrue("Get by index 2", map.get(2) == v3);
		assertTrue("Get by index 3", map.get(3) == v4);
		
		//Get by id
		assertTrue("Get by id 1", map.getById(1) == v1);
		assertTrue("Get by id 2", map.getById(2) == v2);
		assertTrue("Get by id 3", map.getById(3) == v3);
		assertTrue("Get by id 4", map.getById(4) == v4);

		//Get by name
		assertTrue("Get by name 1", map.get("v1") == v1);
		assertTrue("Get by name 2", map.get("v2") == v2);
		assertTrue("Get by name 3", map.get("v3") == v3);
		assertTrue("Get by name 4", map.get("v4") == v4);
		
		//Clone
		VariableMap clone = map.clone();
		
		assertTrue("Full clone", clone.getSize() == 4);
		
		// Get by index
		assertTrue("Clone - Get by index 0", clone.get(0).getName().equals(v1.getName()));
		assertTrue("Clone - Get by index 1", clone.get(1).getName().equals(v2.getName()));
		assertTrue("Clone - Get by index 2", clone.get(2).getName().equals(v3.getName()));
		assertTrue("Clone - Get by index 3", clone.get(3).getName().equals(v4.getName()));
		
		// Get by id
		assertTrue("Clone - Get by id 1", clone.getById(1).getName().equals(v1.getName()));
		assertTrue("Clone - Get by id 2", clone.getById(2).getName().equals(v2.getName()));
		assertTrue("Clone - Get by id 3", clone.getById(3).getName().equals(v3.getName()));
		assertTrue("Clone - Get by id 4", clone.getById(4).getName().equals(v4.getName()));

		// Get by name
		assertTrue("Clone - Get by name 1", clone.get("v1").getName().equals(v1.getName()));
		assertTrue("Clone - Get by name 2", clone.get("v2").getName().equals(v2.getName()));
		assertTrue("Clone - Get by name 3", clone.get("v3").getName().equals(v3.getName()));
		assertTrue("Clone - Get by name 4", clone.get("v4").getName().equals(v4.getName()));
		
		//Copy values by name
		try {
			v1.setValue(new BooleanValue(false));
			v2.setValue(new IntegerValue(5));
		} catch (Exception exc) {
			fail("Unwanted exception");
		}
		clone.copyValuesByName(map);
		
		assertTrue(clone.get("v1").getValue().equals(v1.getValue()));
		assertTrue(clone.get("v2").getValue().equals(v2.getValue()));
		
		//Sort
		map.sort();
		assertTrue("Sorted - Get by index 0", map.get(0) == v2);
		assertTrue("Sorted - Get by index 1", map.get(1) == v4);
		assertTrue("Sorted - Get by index 2", map.get(2) == v1);
		assertTrue("Sorted - Get by index 3", map.get(3) == v3);
		
		//Set at
		Variable v5 = new BooleanVariable("v5");
		map.setAt(1, v5);
		assertTrue("Added - Get by index 1", map.get(1) == v5);
		
		//Remove by index
		map.remove(1);
		assertTrue("Removed - Get by index 1", map.get(1) == v1);
		
		//Remove by name
		map.remove("v1");
		assertTrue("Removed 2 - Get by index 1", map.get(1) == v3);
		
		//Clear
		map.clear();
		assertTrue("Empty cleared map", map.getSize() == 0);
	}

}
