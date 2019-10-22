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
package com.graphhopper.jsprit.examples;

import com.graphhopper.jsprit.analysis.toolbox.AlgorithmSearchProgressChartListener;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.selector.SelectBest;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.analysis.SolutionAnalyser;
import com.graphhopper.jsprit.core.problem.Capacity;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.constraint.ConstraintManager;
import com.graphhopper.jsprit.core.problem.constraint.ServiceDeliveriesFirstConstraint;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.io.problem.VrpXMLReader;
import com.graphhopper.jsprit.util.Examples;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VRPWithBackhaulsExample2 {

    private static final Logger logger = LoggerFactory.getLogger(VRPWithBackhaulsExample2.class);

	public static void main(String[] args) {

		/*
         * some preparation - create output folder
		 */
        Examples.createOutputFolder();

		/*
         * Build the problem.
		 *
		 * But define a problem-builder first.
		 */
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();

		/*
         * A solomonReader reads solomon-instance files, and stores the required information in the builder.
		 */
        new VrpXMLReader(vrpBuilder).read("input/pd_christophides_vrpnc1_vcap50.xml");


		/*
         * Finally, the problem can be built. By default, transportCosts are crowFlyDistances (as usually used for vrp-instances).
		 */
        final VehicleRoutingProblem vrp = vrpBuilder.build();

//		new Plotter(vrp).plot("output/vrpwbh_christophides_vrpnc1.png", "pd_vrpnc1");


		/*
         * Define the required vehicle-routing algorithms to solve the above problem.
		 *
		 * The algorithm can be defined and configured in an xml-file.
		 */
//		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "input/algorithmConfig_solomon.xml");

//        VehicleRoutingAlgorithmBuilder vraBuilder = new VehicleRoutingAlgorithmBuilder(vrp,"input/algorithmConfig_solomon.xml");
//        vraBuilder.addDefaultCostCalculators();
//        vraBuilder.addCoreConstraints();

        StateManager stateManager = new StateManager(vrp);
        ConstraintManager constraintManager = new ConstraintManager(vrp, stateManager);
        constraintManager.addConstraint(new ServiceDeliveriesFirstConstraint(), ConstraintManager.Priority.CRITICAL);

//        vraBuilder.setStateAndConstraintManager(stateManager,constraintManager);

//        VehicleRoutingAlgorithm vra = vraBuilder.build();


        VehicleRoutingAlgorithm vra = Jsprit.Builder.newInstance(vrp)
            .setStateAndConstraintManager(stateManager, constraintManager)
            .setProperty(Jsprit.Parameter.FIXED_COST_PARAM.toString(), "0.")
            .buildAlgorithm();
        vra.setMaxIterations(2000);
        vra.addListener(new AlgorithmSearchProgressChartListener("output/search"));



		/*
         * Solve the problem.
		 *
		 *
		 */
        Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();

		/*
         * Retrieve best solution.
		 */
        VehicleRoutingProblemSolution solution = new SelectBest().selectSolution(solutions);

		/*
         * print solution
		 */
        SolutionPrinter.print(solution);

		/*
		 * Plot solution.
		 */
//		SolutionPlotter.plotSolutionAsPNG(vrp, solution, "output/pd_solomon_r101_solution.png","pd_r101");
        Plotter plotter = new Plotter(vrp, solution);
//		plotter.setLabel(Plotter.Label.SIZE);
        plotter.plot("output/vrpwbh_christophides_vrpnc1_solution.png", "vrpwbh_vrpnc1");

        SolutionAnalyser analyser = new SolutionAnalyser(vrp, solution, vrp.getTransportCosts());

        solution.getRoutes().forEach(route -> {
            logger.info("------");
            logger.info("vehicleId: " + route.getVehicle().getId());
            logger.info(new StringBuilder().append("vehicleCapacity: ").append(route.getVehicle().getType().getCapacityDimensions()).append(" maxLoad: ").append(analyser.getMaxLoad(route)).toString());
            logger.info("totalDistance: " + analyser.getDistance(route));
            logger.info("waitingTime: " + analyser.getWaitingTime(route));
            logger.info("load@beginning: " + analyser.getLoadAtBeginning(route));
            logger.info("load@end: " + analyser.getLoadAtEnd(route));
            logger.info("operationTime: " + analyser.getOperationTime(route));
            logger.info("serviceTime: " + analyser.getServiceTime(route));
            logger.info("transportTime: " + analyser.getTransportTime(route));
            logger.info("transportCosts: " + analyser.getVariableTransportCosts(route));
            logger.info("fixedCosts: " + analyser.getFixedCosts(route));
            logger.info("capViolationOnRoute: " + analyser.getCapacityViolation(route));
            logger.info("capViolation@beginning: " + analyser.getCapacityViolationAtBeginning(route));
            logger.info("capViolation@end: " + analyser.getCapacityViolationAtEnd(route));
            logger.info("timeWindowViolationOnRoute: " + analyser.getTimeWindowViolation(route));
            logger.info("skillConstraintViolatedOnRoute: " + analyser.hasSkillConstraintViolation(route));

            logger.info(new StringBuilder().append("dist@").append(route.getStart().getLocation().getId()).append(": ").append(analyser.getDistanceAtActivity(route.getStart(), route)).toString());
            logger.info(new StringBuilder().append("timeWindowViolation@").append(route.getStart().getLocation().getId()).append(": ").append(analyser.getTimeWindowViolationAtActivity(route.getStart(), route)).toString());
            route.getActivities().forEach(act -> {
                logger.info("--");
                logger.info(new StringBuilder().append("actType: ").append(act.getName()).append(" demand: ").append(act.getSize()).toString());
                logger.info(new StringBuilder().append("dist@").append(act.getLocation().getId()).append(": ").append(analyser.getDistanceAtActivity(act, route)).toString());
                logger.info(new StringBuilder().append("load(before)@").append(act.getLocation().getId()).append(": ").append(analyser.getLoadJustBeforeActivity(act, route)).toString());
                logger.info(new StringBuilder().append("load(after)@").append(act.getLocation().getId()).append(": ").append(analyser.getLoadRightAfterActivity(act, route)).toString());
                logger.info(new StringBuilder().append("transportCosts@").append(act.getLocation().getId()).append(": ").append(analyser.getVariableTransportCostsAtActivity(act, route)).toString());
                logger.info(new StringBuilder().append("capViolation(after)@").append(act.getLocation().getId()).append(": ").append(analyser.getCapacityViolationAfterActivity(act, route)).toString());
                logger.info(new StringBuilder().append("timeWindowViolation@").append(act.getLocation().getId()).append(": ").append(analyser.getTimeWindowViolationAtActivity(act, route)).toString());
                logger.info(new StringBuilder().append("skillConstraintViolated@").append(act.getLocation().getId()).append(": ").append(analyser.hasSkillConstraintViolationAtActivity(act, route)).toString());
            });
            logger.info("--");
            logger.info(new StringBuilder().append("dist@").append(route.getEnd().getLocation().getId()).append(": ").append(analyser.getDistanceAtActivity(route.getEnd(), route)).toString());
            logger.info(new StringBuilder().append("timeWindowViolation@").append(route.getEnd().getLocation().getId()).append(": ").append(analyser.getTimeWindowViolationAtActivity(route.getEnd(), route)).toString());
        });

        logger.info("-----");
        logger.info("aggreate solution stats");
        logger.info("total freight moved: " + Capacity.addup(analyser.getLoadAtBeginning(), analyser.getLoadPickedUp()));
        logger.info("total no. picks at beginning: " + analyser.getNumberOfPickupsAtBeginning());
        logger.info("total no. picks on routes: " + analyser.getNumberOfPickups());
        logger.info("total picked load at beginnnig: " + analyser.getLoadAtBeginning());
        logger.info("total picked load on routes: " + analyser.getLoadPickedUp());
        logger.info("total no. deliveries at end: " + analyser.getNumberOfDeliveriesAtEnd());
        logger.info("total no. deliveries on routes: " + analyser.getNumberOfDeliveries());
        logger.info("total delivered load at end: " + analyser.getLoadAtEnd());
        logger.info("total delivered load on routes: " + analyser.getLoadDelivered());
        logger.info("total tp_distance: " + analyser.getDistance());
        logger.info("total tp_time: " + analyser.getTransportTime());
        logger.info("total waiting_time: " + analyser.getWaitingTime());
        logger.info("total service_time: " + analyser.getServiceTime());
        logger.info("total operation_time: " + analyser.getOperationTime());
        logger.info("total twViolation: " + analyser.getTimeWindowViolation());
        logger.info("total capViolation: " + analyser.getCapacityViolation());
        logger.info("total fixedCosts: " + analyser.getFixedCosts());
        logger.info("total variableCosts: " + analyser.getVariableTransportCosts());
        logger.info("total costs: " + analyser.getTotalCosts());

    }

}
