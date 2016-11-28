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
package org.primaresearch.eval;

import java.util.List;

import org.primaresearch.shared.variable.Variable;
import org.primaresearch.shared.variable.VariableMap;

/**
 * Helper class to output or format data in comma-separated values (CSV) format
 * 
 * @author Christian Clausner
 *
 */
public class CsvResultFormatter {

	/**
	 * Prints CSV (comma separated values).
	 * @param evalRes Evaluation result object
	 * @param csvHeaders If true, CSV headers are printed before the values
	 */
	public static void printEvaluationResult(	String gtFileName, String resFileName, List<EvaluationResult> evalResults, 
												boolean csvAddInp, boolean csvHeaders) {
		//Headers
		if (csvHeaders) {
			if (csvAddInp) {
				System.out.print("groundTruth,result,");
			}
			for (int m=0; m<evalResults.size(); m++) {
				VariableMap values = evalResults.get(m).getValues();
				if (m>0)
					System.out.print(",");
				for (int i=0; i<values.getSize(); i++) {
					if (i>0)
						System.out.print(",");
					Variable var = values.get(i);
					System.out.print(var.getName());
				}
			}
			System.out.println();
		}
		
		//Values
		
		// Find result with most value sets
		int maxSetCount = 0;
		for (int m=0; m<evalResults.size(); m++) {
			if (evalResults.get(m).getResultSetCount() > maxSetCount)
				maxSetCount = evalResults.get(m).getResultSetCount();
		}

		// Iterate over all sets
		for (int s=0; s<maxSetCount; s++) {
					
			// Prefix
			if (csvAddInp) {
				System.out.print(gtFileName + "," + resFileName + ",");
			}
			
			for (int m=0; m<evalResults.size(); m++) {
				EvaluationResult evalRes = evalResults.get(m);
				VariableMap values = null;
				if (s < evalRes.getResultSetCount()) //There is a value set at this index in the current result
					values = evalRes.getValues(s);
				if (m>0)
					System.out.print(",");
				for (int i=0; i<evalRes.getValues().getSize(); i++) { //Use the first value set for the loop (as 'values' might be null )
					if (i>0)
						System.out.print(",");
					if (values != null) {
						Variable var = values.get(i);
						System.out.print("\"");
						System.out.print(var.getValue() != null ? var.getValue().toString() : "");
						System.out.print("\"");
					}
				}
			}
			System.out.println();
		}
	}
	
	/**
	 * CSV formatting (comma separated values).
	 * @param evalRes Evaluation result object
	 * @param csvHeaders If true, CSV headers are printed before the values
	 */
	public static String formatEvaluationResult(	String gtFileName, String resFileName, List<EvaluationResult> evalResults, 
												boolean csvAddInp, boolean csvHeaders) {
		StringBuilder csv = new StringBuilder();
		
		//Headers
		if (csvHeaders) {
			if (csvAddInp) {
				csv.append("groundTruth,result,");
			}
			for (int m=0; m<evalResults.size(); m++) {
				VariableMap values = evalResults.get(m).getValues();
				if (m>0)
					csv.append(",");
				for (int i=0; i<values.getSize(); i++) {
					if (i>0)
						csv.append(",");
					Variable var = values.get(i);
					csv.append(var.getName());
				}
			}
			csv.append("\n");
		}
		
		//Values
		
		// Find result with most value sets
		int maxSetCount = 0;
		for (int m=0; m<evalResults.size(); m++) {
			if (evalResults.get(m).getResultSetCount() > maxSetCount)
				maxSetCount = evalResults.get(m).getResultSetCount();
		}

		// Iterate over all sets
		for (int s=0; s<maxSetCount; s++) {
					
			// Prefix
			if (csvAddInp) {
				csv.append(gtFileName + "," + resFileName + ",");
			}
			
			for (int m=0; m<evalResults.size(); m++) {
				EvaluationResult evalRes = evalResults.get(m);
				VariableMap values = null;
				if (s < evalRes.getResultSetCount()) //There is a value set at this index in the current result
					values = evalRes.getValues(s);
				if (m>0)
					csv.append(",");
				for (int i=0; i<evalRes.getValues().getSize(); i++) { //Use the first value set for the loop (as 'values' might be null )
					if (i>0)
						csv.append(",");
					if (values != null) {
						Variable var = values.get(i);
						csv.append(var.getValue() != null ? var.getValue().toString() : "");
					}
				}
			}
			csv.append("\n");
		}
		return csv.toString();
	}
}
