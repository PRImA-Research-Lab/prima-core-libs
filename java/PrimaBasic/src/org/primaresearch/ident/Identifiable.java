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
package org.primaresearch.ident;

import org.primaresearch.ident.IdRegister.InvalidIdException;


/**
 * Interface for objects having an ID.
 * 
 * @author Christian Clausner
 *
 */
public interface Identifiable {

	/**
	 * Returns the ID of this object
	 * @return ID
	 */
	public Id getId();
	
	/**
	 * Returns the ID register that is used to manage IDs and make sure they are unique.
	 * @return ID register object
	 */
	public IdRegister getIdRegister();
	
	/**
	 * Changes the ID of this Identifiable object.<br>
	 * Implementations need to take care of unregistering the old ID from the IDRegister.
	 * 
	 * @throws InvalidIdException The specified ID already is in use.
	 */
	public void setId(String id) throws InvalidIdException;

	/**
	 * Changes the ID of this Identifiable object.<br>
	 * Implementations need to take care of unregistering the old ID from the IDRegister.
	 * 
	 * @throws InvalidIdException The specified ID already is in use.
	 */
	public void setId(Id id) throws InvalidIdException;
}
