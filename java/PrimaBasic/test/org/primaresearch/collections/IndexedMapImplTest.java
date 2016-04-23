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
package org.primaresearch.collections;

import static org.junit.Assert.*;

import java.util.Map.Entry;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;
import org.primaresearch.collections.IndexedMap;
import org.primaresearch.collections.IndexedMapImpl;

/**
 * 
 * @author Christian Clausner
 *
 */
public class IndexedMapImplTest {

	@Test
	public void testClear() {
		IndexedMap<Integer, Double> map = new IndexedMapImpl<Integer, Double>();
		//Clearing when empty
		map.clear();
		assertEquals(0, map.size());
		//Add to map
		map.put(1, 1.0);
		assertEquals(1, map.size());
		//Clear
		map.clear();
		assertEquals(0, map.size());
	}

	@Test
	public void testContainsKey() {
		IndexedMap<Integer, Double> map = new IndexedMapImpl<Integer, Double>();
		//Test on empty map
		assertFalse(map.containsKey(new Integer(1)));
		//Fill map
		map.put(1, 1.0);
		assertTrue(map.containsKey(new Integer(1)));
		map.put(2, 2.0);
		assertTrue(map.containsKey(new Integer(1)));
		assertTrue(map.containsKey(new Integer(2)));
		map.put(3, 3.0);
		assertTrue(map.containsKey(new Integer(1)));
		assertTrue(map.containsKey(new Integer(2)));
		assertTrue(map.containsKey(new Integer(3)));
	}

	@Test
	public void testContainsValue() {
		IndexedMap<Integer, Double> map = new IndexedMapImpl<Integer, Double>();
		//Test on empty map
		assertFalse(map.containsValue(new Double(1.0)));
		//Fill map
		map.put(1, 1.0);
		assertTrue(map.containsValue(new Double(1.0)));
		map.put(2, 2.0);
		assertTrue(map.containsValue(new Double(1.0)));
		assertTrue(map.containsValue(new Double(2.0)));
		map.put(3, 3.0);
		assertTrue(map.containsValue(new Double(1.0)));
		assertTrue(map.containsValue(new Double(2.0)));
		assertTrue(map.containsValue(new Double(3.0)));
	}

	@Test
	public void testEntrySet() {
		IndexedMap<Integer, Double> map = new IndexedMapImpl<Integer, Double>();
		//Test on empty map
		Set<Entry<Integer,Double>> set = map.entrySet();
		assertEquals(0, set.size());
		//Fill map
		map.put(1, 1.0);
		map.put(2, 2.0);
		map.put(3, 3.0);
		set = map.entrySet();
		assertEquals(3, set.size());
		int foundKeys = 0;
		for (Iterator<Entry<Integer,Double>> it = set.iterator(); it.hasNext(); ) {
			Entry<Integer,Double> entry = it.next();
			if (	entry.getKey().equals(new Integer(1))
				|| 	entry.getKey().equals(new Integer(2))
				|| 	entry.getKey().equals(new Integer(3)))
				foundKeys++;
		}
		assertEquals(3, foundKeys);
	}

	@Test
	public void testGetObject() {
		IndexedMap<Integer, Double> map = new IndexedMapImpl<Integer, Double>();
		//Test on empty map
		assertNull(map.get(new Integer(1)));
		//Fill map
		map.put(1, 1.0);
		map.put(2, 2.0);
		map.put(3, 3.0);
		assertEquals(new Double(1.0), map.get(new Integer(1)));
		assertEquals(new Double(2.0), map.get(new Integer(2)));
		assertEquals(new Double(3.0), map.get(new Integer(3)));
		assertNull(map.get(new Integer(4)));
	}

	@Test
	public void testGetInt() {
		IndexedMap<Integer, Double> map = new IndexedMapImpl<Integer, Double>();
		//Test on empty map
		try { 
			map.getAt(1);
			fail("IndexOutOfBoundsException was expected");
		} catch (IndexOutOfBoundsException exc) {
			//Expected exception
		}
		
		//Fill map
		map.put(1, 1.0);
		map.put(2, 2.0);
		map.put(3, 3.0);
		assertEquals(new Double(1.0), map.getAt(0));
		assertEquals(new Double(2.0), map.getAt(1));
		assertEquals(new Double(3.0), map.getAt(2));
	}

	@Test
	public void testIsEmpty() {
		IndexedMap<Integer, Double> map = new IndexedMapImpl<Integer, Double>();
		//Test on empty map
		assertTrue(map.isEmpty());
		
		//Fill map
		map.put(1, 1.0);
		assertFalse(map.isEmpty());
		map.put(2, 2.0);
		assertFalse(map.isEmpty());
		map.put(3, 3.0);
		assertFalse(map.isEmpty());
	}

	@Test
	public void testKeySet() {
		IndexedMap<Integer, Double> map = new IndexedMapImpl<Integer, Double>();
		//Test on empty map
		assertEquals(0, map.keySet().size());
		//Fill map
		map.put(1, 1.0);
		map.put(2, 2.0);
		map.put(3, 3.0);
		assertEquals(3, map.keySet().size());
		int sum = 0;
		for (Iterator<Integer> it = map.keySet().iterator(); it.hasNext(); ) {
			sum += it.next();
		}
		assertEquals(6, sum);
	}

	@Test
	public void testPut() {
		IndexedMap<Integer, Double> map = new IndexedMapImpl<Integer, Double>();
		//Fill map
		map.put(1, 1.0);
		assertEquals(1, map.size());
		assertEquals(new Double(1.0), map.get(new Integer(1)));
		map.put(2, 2.0);
		assertEquals(2, map.size());
		assertEquals(new Double(2.0), map.get(new Integer(2)));
		map.put(3, 3.0);
		assertEquals(3, map.size());
		assertEquals(new Double(3.0), map.get(new Integer(3)));
		//Put existing key
		map.put(3, 3.0);
		assertEquals(3, map.size());
		assertEquals(new Double(3.0), map.get(new Integer(3)));
	}

	@Test
	public void testPutAll() {
		IndexedMap<Integer, Double> map1 = new IndexedMapImpl<Integer, Double>();
		IndexedMap<Integer, Double> map2 = new IndexedMapImpl<Integer, Double>();
		
		//Fill map1
		map1.put(1, 1.0);
		map1.put(2, 2.0);
		map1.put(3, 3.0);
		
		//Add to map2
		map2.putAll(map1);
		assertEquals(3, map2.size());
		assertEquals(new Double(1.0), map2.get(new Integer(1)));
	}

	@Test
	public void testRemoveObject() {
		IndexedMap<Integer, Double> map = new IndexedMapImpl<Integer, Double>();
		//Test on empty map
		assertNull(map.remove(new Integer(1)));
		//Fill map
		map.put(1, 1.0);
		map.put(2, 2.0);
		map.put(3, 3.0);
		assertEquals(new Double(2.0), map.remove(new Integer(2)));
		assertEquals(new Double(1.0), map.remove(new Integer(1)));
		assertEquals(new Double(3.0), map.remove(new Integer(3)));
		assertEquals(0, map.size());
	}

	@Test
	public void testSize() {
		IndexedMap<Integer, Double> map = new IndexedMapImpl<Integer, Double>();
		//Test on empty map
		assertEquals(0, map.size());
		//Fill map
		map.put(1, 1.0);
		assertEquals(1, map.size());
		map.put(2, 2.0);
		assertEquals(2, map.size());
		map.clear();
		assertEquals(0, map.size());
	}

	@Test
	public void testValues() {
		IndexedMap<Integer, Double> map = new IndexedMapImpl<Integer, Double>();
		//Test on empty map
		assertEquals(0, map.values().size());
		//Fill map
		map.put(1, 1.0);
		map.put(2, 2.0);
		map.put(3, 3.0);
		assertEquals(3, map.values().size());
		double sum = 0;
		for (Iterator<Double> it = map.values().iterator(); it.hasNext(); ) {
			sum += it.next();
		}
		assertEquals(6.0, sum, 0.001);
	}

	@Test
	public void testRemoveInt() {
		IndexedMap<Integer, Double> map = new IndexedMapImpl<Integer, Double>();
		//Test on empty map
		try { 
			map.removeAt(1);
			fail("IndexOutOfBoundsException was expected");
		} catch (IndexOutOfBoundsException exc) {
			//Expected exception
		}
		
		//Fill map
		map.put(1, 1.0);
		map.put(2, 2.0);
		map.put(3, 3.0);
		
		try { 
			map.removeAt(3);
			fail("IndexOutOfBoundsException was expected");
		} catch (IndexOutOfBoundsException exc) {
			//Expected exception
		}
		map.removeAt(1);
		assertEquals(2, map.size());
		map.removeAt(1);
		assertEquals(1, map.size());
		map.removeAt(0);
		assertEquals(0, map.size());
	}

}
