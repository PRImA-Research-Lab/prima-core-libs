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
package org.primaresearch.dla.page.layout.logical;


/**
 * A member of a logical layout group.
 *  
 * @author Christian Clausner
 *
 */
public interface GroupMember {

	/**
	 * Returns the parent group
	 */
	public Group getParent();

	/**
	 * Moves this object to another group
	 * @param newParent Target group
	 */
	public void moveTo(Group newParent);
}
