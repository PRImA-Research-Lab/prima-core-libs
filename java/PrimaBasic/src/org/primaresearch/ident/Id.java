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

/**
 * Interface for IDs.<br>
 * <br>
 * Note: IDs should always be compared using the equals method as two different objects
 * might represent the same ID. Using the == operator can lead to errors.<br>
 * <br>
 * Implementations have to override the hashCode and equals methods.
 * 
 * @author Christian Clausner
 *
 */
public interface Id {

	public String toString();
	
	/**
	 * Checks if the given ID equals this ID.
	 */
	//public boolean equals(Id other);
}
