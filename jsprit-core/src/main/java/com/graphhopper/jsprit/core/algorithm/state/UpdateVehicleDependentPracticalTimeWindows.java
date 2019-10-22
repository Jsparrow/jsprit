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

package com.graphhopper.jsprit.core.algorithm.state;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingActivityCosts;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.solution.route.RouteVisitor;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;

import java.util.Collection;
import java.util.Iterator;
import java.util.Collections;

public class UpdateVehicleDependentPracticalTimeWindows implements RouteVisitor, StateUpdater {

    private VehiclesToUpdate vehiclesToUpdate = (VehicleRoute route) -> Collections.singletonList(route.getVehicle());

	private final StateManager stateManager;

	private final VehicleRoutingTransportCosts transportCosts;

	private final VehicleRoutingActivityCosts activityCosts;

	private VehicleRoute route;

	private double[] latestArrTimesAtPrevAct;

	private Location[] locationOfPrevAct;

	private Collection<Vehicle> vehicles;

	public UpdateVehicleDependentPracticalTimeWindows(StateManager stateManager, VehicleRoutingTransportCosts tpCosts, VehicleRoutingActivityCosts activityCosts) {
        this.stateManager = stateManager;
        this.transportCosts = tpCosts;
        this.activityCosts = activityCosts;
        latestArrTimesAtPrevAct = new double[stateManager.getMaxIndexOfVehicleTypeIdentifiers() + 1];
        locationOfPrevAct = new Location[stateManager.getMaxIndexOfVehicleTypeIdentifiers() + 1];
    }

	@Override
    public void visit(VehicleRoute route) {
        begin(route);
        Iterator<TourActivity> revIterator = route.getTourActivities().reverseActivityIterator();
        while (revIterator.hasNext()) {
            visit(revIterator.next());
        }
        finish();
    }

	public void setVehiclesToUpdate(VehiclesToUpdate vehiclesToUpdate) {
        this.vehiclesToUpdate = vehiclesToUpdate;
    }

	public void begin(VehicleRoute route) {
        this.route = route;
        vehicles = vehiclesToUpdate.get(route);
        vehicles.forEach(vehicle -> {
            latestArrTimesAtPrevAct[vehicle.getVehicleTypeIdentifier().getIndex()] = vehicle.getLatestArrival();
            Location location = vehicle.getEndLocation();
            if(!vehicle.isReturnToDepot()){
                location = route.getEnd().getLocation();
            }
            locationOfPrevAct[vehicle.getVehicleTypeIdentifier().getIndex()] = location;
        });
    }

	public void visit(TourActivity activity) {
        vehicles.forEach(vehicle -> {
            double latestArrTimeAtPrevAct = latestArrTimesAtPrevAct[vehicle.getVehicleTypeIdentifier().getIndex()];
            Location prevLocation = locationOfPrevAct[vehicle.getVehicleTypeIdentifier().getIndex()];
            double potentialLatestArrivalTimeAtCurrAct = latestArrTimeAtPrevAct - transportCosts.getBackwardTransportTime(activity.getLocation(), prevLocation,
                latestArrTimeAtPrevAct, route.getDriver(), vehicle) - activityCosts.getActivityDuration(activity, latestArrTimeAtPrevAct, route.getDriver(), route.getVehicle());
            double latestArrivalTime = Math.min(activity.getTheoreticalLatestOperationStartTime(), potentialLatestArrivalTimeAtCurrAct);
            if (latestArrivalTime < activity.getTheoreticalEarliestOperationStartTime()) {
                stateManager.putTypedInternalRouteState(route, vehicle, InternalStates.SWITCH_NOT_FEASIBLE, true);
            }
            stateManager.putInternalTypedActivityState(activity, vehicle, InternalStates.LATEST_OPERATION_START_TIME, latestArrivalTime);
            latestArrTimesAtPrevAct[vehicle.getVehicleTypeIdentifier().getIndex()] = latestArrivalTime;
            locationOfPrevAct[vehicle.getVehicleTypeIdentifier().getIndex()] = activity.getLocation();
        });
    }

	public void finish() {
    }

	public static interface VehiclesToUpdate {

        Collection<Vehicle> get(VehicleRoute route);

    }

}

