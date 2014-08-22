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
package org.primaresearch.shared.variable;

import java.io.Serializable;

import org.primaresearch.shared.variable.constraints.VariableConstraint;

/**
 * Abstract base class for Variable implementations handling name, caption, description, ID, sort index, visibility and constraint.
 *  
 * @author Christian Clausner
 *
 */
public abstract class BaseVariable implements Serializable, Variable {

	private static final long serialVersionUID = 1L;

	protected String name;
	protected String caption = "";
	protected VariableConstraint constraint = null;
	protected int id = 0;
	protected int sortIndex = 0;
	protected boolean visible = true;
	protected String description;
	protected boolean readOnly = false;
	protected int version = 0;

	protected BaseVariable() {
	}

	protected BaseVariable(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the caption for this variable. 
	 * The caption is an alternative to the <code>name</code> attribute, mainly for displaying purposes.
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Sets the caption for this variable. 
	 * The caption is an alternative to the <code>name</code> attribute, mainly for displaying purposes.
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	public void setConstraint(VariableConstraint constraint) {
		this.constraint = constraint;
		//Set the value again to enforce the constraint
		if (constraint != null) {
			try {
				setValue(getValue());
			} catch (Exception e) {
			}
		}
	}
	
	public VariableConstraint getConstraint() {
		return constraint;
	}

	protected void checkValueAgainstConstraint(VariableValue val) throws IllegalArgumentException {
		if (constraint == null || !constraint.throwsIllegalArgumentException())
			return;
		constraint.isValid(val); //Will throw an exception if not valid
	}
	
	protected VariableValue getValueComplyingWithConstraint(VariableValue val) {
		if (constraint == null)
			return val;
		return constraint.getValidValue(val);
	}
	
	protected void copyFrom(BaseVariable other) {
		this.caption = other.caption;
		this.name = other.name;
		this.description = other.description;
		this.id = other.id;
		this.sortIndex = other.sortIndex;
		this.visible = other.visible;
		if (other.constraint != null)
			this.constraint = other.constraint.clone();
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public abstract Variable clone();
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Variable))
			return false;
		Variable other = (Variable)obj;
		if (this.getValue() == null && other.getValue() == null)
			return true;
		if (this.getValue() != null && other.getValue() != null)
			return this.getValue().equals(other.getValue());
		return false;
	}
}
