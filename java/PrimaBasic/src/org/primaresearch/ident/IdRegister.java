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

/**
 * Interface for central ID register that makes sure IDs are unique.
 * 
 * @author Christian Clausner
 *
 */
public interface IdRegister {

	/**
	 * Creates a new ID with default prefix (does not register the ID, use <code>registerId(...)</code>). 
	 * 
	 * @return New ID object
	 */
	public Id generateId();

	/**
	 * Creates a new ID (does not register the ID, use <code>registerId(...)</code>). 
	 * 
	 * @param prefix Prefix to be used for the ID
	 * @return New ID object
	 */
	public Id generateId(String prefix) throws InvalidIdException;

	/**
	 * Registers the specified ID.
	 * 
	 * @param id ID object
	 * @throws InvalidIdException The specified ID already is in use.
	 */
	public void registerId(Id id) throws InvalidIdException;
		
	/**
	 * Registers the specified ID.
	 * 
	 * @param id ID object
	 * @throws InvalidIdException The specified ID already is in use.
	 */
	public Id registerId(String id) throws InvalidIdException;
	
	/**
	 * Registers the specified ID and unregisters an old ID at the same time.
	 * The old ID is unregistered only if the new ID is not in use already.
	 *  
	 * @param id ID object to register
	 * @param oldIdToUnregister ID object to unregister
	 * @throws InvalidIdException The specified ID already is in use.
	 */
	public void registerId(Id id, Id oldIdToUnregister) throws InvalidIdException;
	
	/**
	 * Registers the specified ID and unregisters an old ID at the same time.
	 * The old ID is unregistered only if the new ID is not in use already.
	 *  
	 * @param id ID object to register
	 * @param oldIdToUnregister ID object to unregister
	 * @throws InvalidIdException The specified ID already is in use.
	 */
	public Id registerId(String id, Id oldIdToUnregister) throws InvalidIdException;
	
	/**
	 * Tries to register the specified ID. If successful, the ID is returned, otherwise
	 * a new ID with default prefix is created and returned.
	 * 
	 * @param id ID object to register
	 * @return The registered ID or a new ID if the specified ID is in use already. 
	 */
	public Id registerOrCreateNewId(String id) throws InvalidIdException;

	/**
	 * Tries to register the specified ID. If successful, the ID is returned, otherwise
	 * a new ID is created and returned.
	 * 
	 * @param id ID object to register
	 * @param prefix Prefix to be used for the new ID
	 * @return The registered ID or a new ID if the specified ID is in use already. 
	 */
	public Id registerOrCreateNewId(String id, String prefix) throws InvalidIdException;

	/**
	 * Tries to register the specified ID. If successful, the ID is returned, otherwise
	 * a new ID with default prefix is created and returned.
	 * 
	 * @param id ID object to register
	 * @return The registered ID or a new ID if the specified ID is in use already. 
	 */
	public Id registerOrCreateNewId(Id id);

	/**
	 * Tries to register the specified ID. If successful, the ID is returned, otherwise
	 * a new ID is created and returned.
	 * 
	 * @param id ID object to register
	 * @param prefix Prefix to be used for the new ID
	 * @return The registered ID or a new ID if the specified ID is in use already. 
	 */
	public Id registerOrCreateNewId(Id id, String prefix) throws InvalidIdException;

	/**
	 * Unregisters the specified ID 
	 * @param id
	 */
	public void unregisterId(Id id);
	
	/**
	 * Returns an ID object for the given ID string
	 * @param key ID string
	 * @return ID object
	 */
	public Id getId(String key) throws InvalidIdException;
	
	/**
	 * Exception for duplicate or malformed IDs
	 * @author Christian Clausner
	 *
	 */
	@SuppressWarnings("serial")
	public static class InvalidIdException extends Exception {
		public InvalidIdException(String message) {
			super(message);
		}
	}
}
