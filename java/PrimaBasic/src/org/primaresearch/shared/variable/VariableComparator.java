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
package org.primaresearch.shared.variable;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator to sort variables by sort index
 * 
 * @author Christian Clausner
 *
 */
public class VariableComparator implements Comparator<Variable>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Variable v1, Variable v2) {
		if (v1 != null && v2 != null)
			return (new Integer(v1.getSortIndex())).compareTo(new Integer(v2.getSortIndex()));
		return 0;
	}

}
