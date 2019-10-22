/*
 * Licensed to GraphHopper GmbH under one or more contributor
 * license agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * GraphHopper GmbH licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.graphhopper.jsprit.core.algorithm.selector;

import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;

import java.util.Collection;


public class SelectBest implements SolutionSelector {

    private static SelectBest selector = null;

    public static SelectBest getInstance() {
        if (selector != null) {
			return selector;
		}
		selector = new SelectBest();
		return selector;
    }

    @Override
    public VehicleRoutingProblemSolution selectSolution(Collection<VehicleRoutingProblemSolution> solutions) {
        double minCost = Double.MAX_VALUE;
        VehicleRoutingProblemSolution bestSolution = null;
        for (VehicleRoutingProblemSolution sol : solutions) {
            if (bestSolution == null) {
                bestSolution = sol;
                minCost = sol.getCost();
            } else if (sol.getCost() < minCost) {
                bestSolution = sol;
                minCost = sol.getCost();
            }
        }
        return bestSolution;
    }

    @Override
    public String toString() {
        return "[name=selectBest]";
    }

}
