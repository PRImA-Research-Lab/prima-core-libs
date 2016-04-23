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
package org.primaresearch.shared.variable;

import java.io.Serializable;

import org.primaresearch.shared.variable.constraints.VariableConstraint;

/**
 * Interface for variables holding values.
 * 
 * @author Christian Clausner
 *
 */
public interface Variable extends Serializable {

	/**
	 * Returns the internal name of the variable. 
	 */
	public String getName();
	
	public void setName(String name);
	
	/**
	 * Returns the display name of the variable.
	 */
	public String getCaption();
	
	/**
	 * Sets the display name of the variable. 
	 */
	public void setCaption(String caption);
	
	/**
	 * Returns the value of the variable.
	 * @return A wrapper containing the value or null if the variable is 'not set'. 
	 */
	public VariableValue getValue();
	
	/**
	 * Sets the variable value
	 * @param value Wrapper containing the actual value. Use null to mark the variable as 'not set'.
	 * @throws WrongVariableTypeException 
	 * @throws IllegalArgumentException The value doesn't comply with a constraint. 
	 */
	public void setValue(VariableValue value) throws WrongVariableTypeException, IllegalArgumentException;
	
	/**
	 * Sets the variable value by parsing the given string.
	 * @param valueInTextForm String representing the new variable value. 
	 */
	public void parseValue(String valueInTextForm);
	
	/**
	 * Creates a deep copy of this variable.
	 */
	public Variable clone();
	
	/**
	 * Sets a constraint for this variable (e.g. minimum and maximum for numbers).
	 * @param constraint Constraint implementation
	 */
	public void setConstraint(VariableConstraint constraint);
	
	/**
	 * Returns the constraint for this variable (e.g. minimum and maximum for numbers).
	 * @return Constraint object or <code>null</code> if there is no constraint
	 */
	public VariableConstraint getConstraint();
	
	/**
	 * Returns the ID of this variable
	 * @return ID number
	 */
	public int getId();
	
	/**
	 * Sets the ID of this variable
	 * @param id ID number
	 */
	public void setId(int id);
	
	/**
	 * Returns the sort index of this variable. Sort indexes are used in <code>VariableMaps</code> for instance.
	 * @return Current sort index
	 */
	public int getSortIndex();
	
	/**
	 * Sets the sort index of this variable. Sort indexes are used in <code>VariableMaps</code> for instance.
	 * @param index New sort index
	 */
	public void setSortIndex(int index);
	
	/**
	 * Recommendation for visibility of this variable in graphical user interfaces
	 * @return <code>true</code> if this variable should be shown, <code>false</code> otherwise
	 */
	public boolean isVisible();
	
	/**
	 * Sets recommendation for visibility of this variable in graphical user interfaces
	 * @param vis Set to <code>true</code> if this variable should be shown, <code>false</code> otherwise
	 */
	public void setVisible(boolean vis);
	
	/**
	 * Returns the description text for this variable
	 * @return Text
	 */
	public String getDescription();

	/**
	 * Sets the description text for this variable
	 * @param text Description
	 */
	public void setDescription(String text);
	
	/**
	 * Recommendation for permission to edit of this variable in graphical user interfaces
	 * @return <code>true</code> if this variable should be read-only, <code>false</code> otherwise
	 */
	public boolean isReadOnly();
	
	/**
	 * Sets recommendation for permission to edit of this variable in graphical user interfaces
	 * @param readOnly Set to <code>true</code> if this variable should be read-only, <code>false</code> otherwise
	 */
	public void setReadOnly(boolean readOnly);
	
	/**
	 * Returns the version of this variable. Versions can be used in conjunction with saved variables to force updates. 
	 * @return Version number
	 */
	public int getVersion();

	/**
	 * Sets the version of this variable. Versions can be used in conjunction with saved variables to force updates.
	 * @param v Version number
	 */
	public void setVersion(int v);
	
	/**
	 * Exception for calls to <code>setValue()</code> with value object that doesn't match the variable type.
	 * 
	 * @author Christian Clausner
	 *
	 */
	@SuppressWarnings("serial")
	public static class WrongVariableTypeException extends Exception {
		public WrongVariableTypeException(String msg) {
			super(msg);
		}
	}
}
