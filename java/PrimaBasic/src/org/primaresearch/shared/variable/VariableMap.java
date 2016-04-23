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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.primaresearch.shared.variable.Variable.WrongVariableTypeException;

/**
 * Indexed map of Variables.
 * 
 * @author Christian Clausner
 *
 */
public class VariableMap implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Variable> variables = new ArrayList<Variable>();
	private Map<String, Variable> nameMap = new HashMap<String, Variable>();
	private String type = null;
	private String name = null;
	
	private VariableComparator sortIndexComparator = null;
	
	/**
	 * Creates a deep copy of this map.
	 */
	public VariableMap clone() {
		VariableMap copy = new VariableMap();
		
		copy.setType(type);
		copy.setName(name);
		
		for (Iterator<Variable> it = variables.iterator(); it.hasNext(); ) {
			copy.add(it.next().clone());
		}
		
		return copy;
	}
	
	/**
	 * Copies the values of the variables of the given map to the 
	 * variables with the same name of this map.
	 */
	public void copyValuesByName(VariableMap source) {
		if (source == null)
			return;
		for (int i=0; i<source.getSize(); i++) {
			Variable sourceVar = source.get(i);
			if (sourceVar.getName() != null) { //has name?
				Variable target = this.get(sourceVar.getName());
				if (target != null)	{
					//Constraint
					target.setConstraint(sourceVar.getConstraint());
					//Step
					if (sourceVar instanceof IntegerVariable) {
						((IntegerVariable)target).setStep(((IntegerVariable)sourceVar).getStep());
					}
					else if (sourceVar instanceof DoubleVariable) {
						((DoubleVariable)target).setStep(((DoubleVariable)sourceVar).getStep());
					}
					//Value
					try {
						target.setValue(sourceVar.getValue());
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (WrongVariableTypeException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * Return the first variable of this map with the given name.
	 * @return A variable or null.
	 */
	/*public Variable getVariableWithName(String name) {
		Variable ret = null;
		for (int i=0; i<getSize(); i++) {
			Variable curr = get(i);
			if (name.equals(curr.getName()))
				return curr;
		}
		return ret;
	}*/
	
	/**
	 * Returns the variable at the given index.
	 * @throws IndexOutOfBoundsException  
	 */
	public Variable get(int index) {
		return variables.get(index);
	}
	
	/**
	 * Returns the variable with the given name. 
	 * @return The variable or null if there is no variable with the specified name.
	 */
	public Variable get(String name) {
		return nameMap.get(name);
	}
	
	/**
	 * Return the first variable of this map with the given id number.
	 * @return A variable or <code>null</code>.
	 */
	public Variable getById(int id) {
		for (int i=0; i<getSize(); i++) {
			Variable curr = get(i);
			if (id == curr.getId())
				return curr;
		}
		return null;
	}
	
	/**
	 * Returns the number of variables in this map. 
	 */
	public int getSize() {
		return variables.size();
	}
	
	/**
	 * Adds a variable to the map.
	 */
	public void add(Variable v) {
		variables.add(v);
		nameMap.put(v.getName(), v);
	}
	
	/**
	 * Replaces the variable at the given index with the given variable.
	 */
	public void setAt(int index, Variable v) {
		//Remove old variable at the given position from the name map
		Variable old = variables.get(index);
		nameMap.remove(old.getName());

		//Add new variable
		variables.set(index, v);
		nameMap.put(v.getName(), v);
	}

	/**
	 * Removes the variable at the given index from the map. 
	 */
	public void remove(int index) {
		Variable v = variables.get(index);
		variables.remove(index);
		nameMap.remove(v.getName());
	}
	
	/**
	 * Removes the variable with the given name from the map. 
	 */
	public void remove(String name) {
		Variable v = nameMap.get(name);
		if (v != null) {
			nameMap.remove(v.getName());
			//Find the variable in the list
			for (int i=0; i<variables.size(); i++) {
				if (variables.get(i) == v) {
					variables.remove(i);
					break;
				}
			}
		}
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Removes all variables from this map.
	 */
	public void clear() {
		variables.clear();
		nameMap.clear();
	}

	/**
	 * Sorts the variables by sort index
	 */
	public void sort() {
		if (sortIndexComparator == null)
			sortIndexComparator = new VariableComparator();
		Collections.sort(variables, sortIndexComparator);
	}

	
}
