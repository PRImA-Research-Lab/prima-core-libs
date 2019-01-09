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
package org.primaresearch.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Map that supports indexed access.<br>
 * This implementation internally used a LinkedHashMap and an ArrayList.
 * 
 * @author Christian Clausner
 *
 * @param <K> Key type 
 * @param <V> Value type
 */
public class IndexedMapImpl<K,V> implements IndexedMap<K,V>{
	
	private Map<K,V> map = new LinkedHashMap<K, V>();
	private List<V> list = null;

	@Override
	public void clear() {
		map.clear();
		list = null;
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object val) {
		return map.containsValue(val);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	@Override
	public V get(Object key) {
		return map.get(key);
	}
	
	@Override
	public V getAt(int index) {
		List<V> list = getList();
		return list.get(index);
	}
	
	/**
	 * Returns the internal list of values. If the list doesn't exist, it will be created and filled from the map.
	 */
	private List<V> getList() {
		if (list == null) {
			list = new ArrayList<V>(this.size());
			list.addAll(map.values());
		}
		return list;
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public V put(K key, V val) {
		if (list != null) {
			boolean hadKey = containsKey(key);
			if (!hadKey)
				list.add(val); //When adding to the map, we have to add to the list as well, but only if not re-inserted
		}
		V v = map.put(key, val);
		return v;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> otherMap) {
		map.putAll(otherMap);
		list = null;	//List will be rebuilt on next indexed operation
	}

	@Override
	public V remove(Object key) {
		V v = map.remove(key);
		list = null;	//List will be rebuilt on next indexed operation
		return v;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	/**
	 * Removes the entry at the specified index from the map.
	 * Note that entries with value 'null' will cannot be removed using this method. Use remove(key) instead.
	 */
	@Override
	public V removeAt(int index) {
		List<V> list = getList();
		V v = list.get(index);
		if (v != null) { //We cannot delete values which are null (there could be many entries in the map having value null)
			list.remove(index);
			//We have to find the value in the map now
			K k = null;
			for (Entry<K, V> entry : map.entrySet()) {
		         if (v.equals(entry.getValue())) {
		             k = entry.getKey();
		             break;
		         }
		    }
			if (k != null)
				map.remove(k);
		}
		return v;
	}

}
