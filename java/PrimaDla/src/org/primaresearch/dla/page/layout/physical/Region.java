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
package org.primaresearch.dla.page.layout.physical;

import org.primaresearch.dla.page.layout.physical.role.RegionRole;
import org.primaresearch.dla.page.layout.physical.shared.RoleType;
import org.primaresearch.shared.variable.VariableMap;

/**
 * Layout region
 * 
 * @author Christian Clausner
 *
 */
public interface Region extends ContentObject, RegionContainer {

	/** Returns the parent region if this is a nested region or <code>null</code> if it has no parent */
	public RegionContainer getParentRegion();

	/**
	 * User-defined attributes (text, int, decimal or boolean)
	 * @param createIfNotExists Set to true if to create an empty variable map if none exists yet.
	 * @return Variable map or <code>null</code>
	 */
	public VariableMap getUserDefinedAttributes(boolean createIfNotExists);
	
	/**
	 *  User-defined attributes (text, int, decimal or boolean)
	 * @param attrs Variable map
	 */
	public void setUserDefinedAttributes(VariableMap attrs);
	
	/**
	 * Checks if this region has a specific role in context of a parent region (e.g. table cell)
	 * @param type The type of role (e.g. table cell)
	 */
	public boolean hasRole(RoleType type);
	
	/**
	 * Returns the specified role object of this region (in context of a parent region (e.g. table cell))
	 * @param type The type of role (e.g. table cell)
	 * @return Role object or <code>null</code>
	 */
	public RegionRole getRole(RoleType type);
	
	/**
	 * Adds the specified role type for this region (in context of a parent region (e.g. table cell)).
	 * Replaces existing role object of same type, if one exists.
	 * @param type The type of role (e.g. table cell)
	 * @return Role object
	 */
	public RegionRole addRole(RoleType type);
	
	/**
	 * Removes the specified role type from this region (in context of a parent region (e.g. table cell)).
	 * @param type The type of role (e.g. table cell)
	 */
	public void removeRole(RoleType type);
}
