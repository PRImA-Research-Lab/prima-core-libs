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
package org.primaresearch.dla.page.layout.physical.role;

import org.primaresearch.dla.page.layout.physical.AttributeFactory;
import org.primaresearch.dla.page.layout.physical.shared.RoleType;
import org.primaresearch.shared.variable.VariableMap;

/**
 * A role representing a table cell. To be used for regions that are nested in a table region.
 * 
 * @author Christian Clausner
 *
 */
public class TableCellRole implements RegionRole {

	private VariableMap attributes;

	/**
	 * Constructor
	 * @param attrFactory
	 */
	public TableCellRole(AttributeFactory attrFactory) {
		attributes = attrFactory.createAttributes(RoleType.TableCellRole);
	}
	
	@Override
	public VariableMap getAttributes() {
		return attributes;
	}

	@Override
	public RoleType getType() {
		return RoleType.TableCellRole;
	}

}
