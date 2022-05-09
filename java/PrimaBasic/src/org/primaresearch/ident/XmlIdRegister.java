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
package org.primaresearch.ident;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * IdRegister implementation for XML conform IDs.
 * 
 * @author Christian Clausner
 *
 */
public class XmlIdRegister implements IdRegister {

	private Set<Id> usedIds = new HashSet<Id>(); 
	private String defaultPrefix;	//XML IDs need a non-numeric prefix (see XML schema documentation for detailed rules)#
	/** Map with [prefix, counter] */
	private Map<String, Integer> counters = new HashMap<String, Integer>(); 

	/**
	 * Default constructor using a predefined ID prefix.
	 */
	public XmlIdRegister() {
		this("i");
	}
	
	/**
	 * Constructor with prefix specification.
	 * @param defaultIdPrefix XML ID prefix (must not start with a digit; see XML schema documentation for detailed rules). 
	 */
	public XmlIdRegister(String defaultIdPrefix) {
		this.defaultPrefix = defaultIdPrefix;
	}

	/**
	 * Returns the counter that corresponds to the given ID prefix
	 * @param prefix ID prefix
	 * @param increment If set to <code>true</code>, the counter will also be incremented (+1)
	 * @return The current counter value (before increment)
	 */
	private int getCounter(String prefix, boolean increment) {
		Integer counter = counters.get(prefix);
		if (counter == null) {
			counter = Integer.valueOf(1);
			counters.put(prefix, counter);
		}
		int ret = counter.intValue();
		if (increment)
			counters.put(prefix, Integer.valueOf(ret+1));
		return ret;
	}

	@Override
	public Id generateId() {
		try {
			return generateId(defaultPrefix);
		} catch (InvalidIdException e) {
		}
		return null;
	}
	
	@Override
	public Id generateId(String prefix) throws InvalidIdException {
		XmlId newId;
		do {
			newId = new XmlId(prefix + getCounter(prefix, true));
		} while (hasId(newId));
		return newId;
	}

	
	@Override
	public void registerId(Id id) throws InvalidIdException {
		registerId(id, null);
	}
	
	@Override
	public void registerId(Id id, Id oldIdToUnregister) throws InvalidIdException {
		
		if (id.equals(oldIdToUnregister)) //New and old are the same -> we don't have to do anything
			return;

		if (hasId(id))
			throw new InvalidIdException("ID already in use: "+id);
		if (oldIdToUnregister != null)
			unregisterId(oldIdToUnregister);
		usedIds.add(id);
	}
	
	@Override
	public Id registerId(String id) throws InvalidIdException {
		return registerId(id, null);
	}

	@Override
	public Id registerId(String id, Id oldIdToUnregister) throws InvalidIdException {
		Id xmlId = new XmlId(id);

		if (xmlId.equals(oldIdToUnregister)) //New and old are the same -> we don't have to do anything
			return xmlId;
		
		if (hasId(xmlId))
			throw new InvalidIdException("ID already in use: "+id);
		if (oldIdToUnregister != null)
			unregisterId(oldIdToUnregister);
		usedIds.add(xmlId);
		return xmlId;
	}

	@Override
	public void unregisterId(Id id) {
		if (hasId(id))
			usedIds.remove(id);
	}
	
	private boolean hasId(Id id) {
		if (id == null)
			return false;
		return usedIds.contains(id);
	}

	@Override
	public Id registerOrCreateNewId(String id) throws InvalidIdException {
		return registerOrCreateNewId(id, defaultPrefix);
	}
	
	@Override
	public Id registerOrCreateNewId(String id, String prefix) throws InvalidIdException {
		Id xmlId = new XmlId(id);
		if (hasId(xmlId)) //Already in use -> create a new ID
			return generateId(prefix);
		else { //Not in use -> register
			try {
				registerId(xmlId);
			} catch (InvalidIdException e) {
				e.printStackTrace();	//Should not happen as we already called hasId()
			}
		}
		return xmlId;
	}

	@Override
	public Id registerOrCreateNewId(Id id) {
		try {
			return registerOrCreateNewId(id, defaultPrefix);
		} catch (InvalidIdException e) {
		}
		return null;
	}
	
	@Override
	public Id registerOrCreateNewId(Id id, String prefix) throws InvalidIdException {
		if (hasId(id)) //Already in use -> create a new ID
			return generateId(prefix);
		else { //Not in use -> register
			try {
				registerId(id);
			} catch (InvalidIdException e) {
				e.printStackTrace();	//Should not happen as we already called hasId()
			}
		}
		return id;
	}

	@Override
	public Id getId(String key) throws InvalidIdException {
		return new XmlId(key);
	}

	
	/**
	 * Implementation of Id intended for XML id convention. This class does some basic checks for ID validity.  
	 * 
	 * @author Christian Clausner
	 *
	 */
	public static class XmlId implements Id {
	
		private String id;

		/**
		 * Constructor
		 * 
		 * @param id ID content
		 * @throws InvalidIdException Invalid ID
		 */
		private XmlId(String id) throws InvalidIdException {
			if (id == null || id.isEmpty() ||
					Character.isDigit(id.charAt(0)))
				throw new InvalidIdException("Invalid ID format"); 
			this.id = id;
		}

		@Override
		public boolean equals(Object other) {
			if (other == null)
				return false;
			if (other instanceof XmlId)
				return ((XmlId)other).id.equals(id);
			return other.toString().equals(id);
		}
		
		@Override
		public String toString() {
			return id;
		}
		
		@Override
		public int hashCode() {
			return id.hashCode();
		}
	}



	
}
