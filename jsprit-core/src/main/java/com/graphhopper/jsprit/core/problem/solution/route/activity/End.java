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
package com.graphhopper.jsprit.core.problem.solution.route.activity;

import com.graphhopper.jsprit.core.problem.AbstractActivity;
import com.graphhopper.jsprit.core.problem.Capacity;
import com.graphhopper.jsprit.core.problem.Location;

public final class End extends AbstractActivity {

    private static final Capacity capacity = Capacity.Builder.newInstance().build();


	private double endTime = -1;


	private double theoretical_earliestOperationStartTime;


	private double theoretical_latestOperationStartTime;


	private double arrTime;


	private Location location;

	public End(Location location, double theoreticalStart, double theoreticalEnd) {
        this.location = location;
        theoretical_earliestOperationStartTime = theoreticalStart;
        theoretical_latestOperationStartTime = theoreticalEnd;
        endTime = theoreticalEnd;
        setIndex(-2);
    }

	public End(String locationId, double theoreticalStart, double theoreticalEnd) {
        if (locationId != null) {
			this.location = Location.Builder.newInstance().setId(locationId).build();
		}
        theoretical_earliestOperationStartTime = theoreticalStart;
        theoretical_latestOperationStartTime = theoreticalEnd;
        endTime = theoreticalEnd;
        setIndex(-2);
    }

	public End(End end) {
        this.location = end.getLocation();
//		this.locationId = end.getLocation().getId();
        theoretical_earliestOperationStartTime = end.getTheoreticalEarliestOperationStartTime();
        theoretical_latestOperationStartTime = end.getTheoreticalLatestOperationStartTime();
        arrTime = end.getArrTime();
        endTime = end.getEndTime();
        setIndex(-2);
    }

	public static End newInstance(String locationId, double earliestArrival, double latestArrival) {
        return new End(locationId, earliestArrival, latestArrival);
    }

	public static End copyOf(End end) {
        return new End(end);
    }

	@Override
	public void setTheoreticalEarliestOperationStartTime(double theoreticalEarliestOperationStartTime) {
        theoretical_earliestOperationStartTime = theoreticalEarliestOperationStartTime;
    }

	@Override
	public void setTheoreticalLatestOperationStartTime(double theoreticalLatestOperationStartTime) {
        theoretical_latestOperationStartTime = theoreticalLatestOperationStartTime;
    }

	@Override
	public double getTheoreticalEarliestOperationStartTime() {
        return theoretical_earliestOperationStartTime;
    }

	@Override
	public double getTheoreticalLatestOperationStartTime() {
        return theoretical_latestOperationStartTime;
    }

	@Override
	public double getEndTime() {
        return endTime;
    }

	@Override
	public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

	public void setLocation(Location location) {
        this.location = location;
    }

	@Override
    public Location getLocation() {
        return location;
    }

	@Override
    public double getOperationTime() {
        return 0.0;
    }

	@Override
    public String toString() {
        return new StringBuilder().append("[type=").append(getName()).append("][location=").append(location).append("][twStart=").append(Activities.round(theoretical_earliestOperationStartTime))
				.append("][twEnd=").append(Activities.round(theoretical_latestOperationStartTime)).append("]").toString();
    }

	@Override
    public String getName() {
        return "end";
    }

	@Override
    public double getArrTime() {
        return this.arrTime;
    }

	@Override
    public void setArrTime(double arrTime) {
        this.arrTime = arrTime;

    }

	@Override
    public TourActivity duplicate() {
        return new End(this);
    }

	@Override
    public Capacity getSize() {
        return capacity;
    }

}
