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

public final class Start extends AbstractActivity {

    @Deprecated
    public static final String ACTIVITY_NAME = "start";

    private static final Capacity capacity = Capacity.Builder.newInstance().build();

	private String locationId;

	private double theoreticalEarliestOperationStartTime;

	private double theoreticalLatestOperationStartTime;

	private double endTime;

	private double arrTime;

	private Location location;

	public Start(Location location, double theoreticalStart, double theoreticalEnd) {
        this.location = location;
        this.theoreticalEarliestOperationStartTime = theoreticalStart;
        this.theoreticalLatestOperationStartTime = theoreticalEnd;
        this.endTime = theoreticalStart;
        setIndex(-1);
    }

	private Start(String locationId, double theoreticalStart, double theoreticalEnd) {
        if (locationId != null) {
			this.location = Location.Builder.newInstance().setId(locationId).build();
		}
        this.theoreticalEarliestOperationStartTime = theoreticalStart;
        this.theoreticalLatestOperationStartTime = theoreticalEnd;
        this.endTime = theoreticalStart;
        setIndex(-1);
    }

	private Start(Start start) {
        this.location = start.getLocation();
        theoreticalEarliestOperationStartTime = start.getTheoreticalEarliestOperationStartTime();
        theoreticalLatestOperationStartTime = start.getTheoreticalLatestOperationStartTime();
        endTime = start.getEndTime();
        setIndex(-1);
    }

	public static Start newInstance(String locationId, double theoreticalStart, double theoreticalEnd) {
        return new Start(locationId, theoreticalStart, theoreticalEnd);
    }

	public static Start copyOf(Start start) {
        return new Start(start);
    }

	@Override
	public double getTheoreticalEarliestOperationStartTime() {
        return theoreticalEarliestOperationStartTime;
    }

	public void setLocation(Location location) {
        this.location = location;
    }

	@Override
	public double getTheoreticalLatestOperationStartTime() {
        return theoreticalLatestOperationStartTime;
    }

	@Override
	public void setTheoreticalEarliestOperationStartTime(double time) {
        this.theoreticalEarliestOperationStartTime = time;
    }

	@Override
	public void setTheoreticalLatestOperationStartTime(double time) {
        this.theoreticalLatestOperationStartTime = time;
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
        return new StringBuilder().append("[type=").append(getName()).append("][location=").append(location).append("][twStart=").append(Activities.round(theoreticalEarliestOperationStartTime))
				.append("][twEnd=").append(Activities.round(theoreticalLatestOperationStartTime)).append("]").toString();
    }

	@Override
    public String getName() {
        return "start";
    }

	@Override
    public double getArrTime() {
        return arrTime;
    }

	@Override
    public double getEndTime() {
        return endTime;
    }

	@Override
    public void setArrTime(double arrTime) {
        this.arrTime = arrTime;
    }

	@Override
    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

	@Override
    public TourActivity duplicate() {
        return new Start(this);
    }

	@Override
    public Capacity getSize() {
        return capacity;
    }

}
