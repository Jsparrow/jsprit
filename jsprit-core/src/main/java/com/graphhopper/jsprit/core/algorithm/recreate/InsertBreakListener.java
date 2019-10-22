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

package com.graphhopper.jsprit.core.algorithm.recreate;

import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by schroeder on 19/05/15.
 */
class InsertBreakListener implements EventListener {

    private static final Logger logger = LoggerFactory.getLogger(InsertBreakListener.class);

    @Override
    public void inform(Event event) {
        if (!(event instanceof InsertBreak)) {
			return;
		}
		InsertBreak insertActivity = (InsertBreak) event;
		boolean condition = !insertActivity.getNewVehicle().isReturnToDepot() && insertActivity.getIndex() >= insertActivity.getVehicleRoute().getActivities().size();
		if (condition) {
		    insertActivity.getVehicleRoute().getEnd().setLocation(insertActivity.getActivity().getLocation());
		}
		VehicleRoute vehicleRoute = ((InsertBreak) event).getVehicleRoute();
		boolean condition1 = !vehicleRoute.isEmpty() && vehicleRoute.getVehicle() != ((InsertBreak) event).getNewVehicle() && vehicleRoute.getVehicle().getBreak() != null;
		if (condition1) {
		    boolean removed = vehicleRoute.getTourActivities().removeJob(vehicleRoute.getVehicle().getBreak());
		    if (removed) {
				logger.trace("remove old break " + vehicleRoute.getVehicle().getBreak());
			}
		}
		insertActivity.getVehicleRoute().getTourActivities().addActivity(insertActivity.getIndex(), ((InsertBreak) event).getActivity());
    }

}
