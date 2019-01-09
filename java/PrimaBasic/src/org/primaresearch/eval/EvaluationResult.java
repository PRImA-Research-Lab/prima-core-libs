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
package org.primaresearch.eval;

import org.primaresearch.shared.variable.VariableMap;

/**
 * Interface for evaluation results.
 * 
 * Results can have multiple sets of values. One set can be interpreted as a row of a table.
 * 
 * @author Christian Clausner
 *
 */
public interface EvaluationResult {

	/**
	 * Returns the values of the evaluation result measures.
	 */
	public VariableMap getValues();
	
	/**
	 * Returns the values of the evaluation result measures at a given index (if there are multiple result sets).
	 */
	public VariableMap getValues(int index);

	/**
	 * Returns the number of result sets (one set consists of multiple values). 
	 */
	public int getResultSetCount();
	
	/**
	 * Returns a caption (e.g. for display in a user interface)
	 */
	public String getCaption();

}
