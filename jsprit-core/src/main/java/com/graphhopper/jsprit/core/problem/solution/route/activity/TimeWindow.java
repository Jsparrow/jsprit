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

/**
 * TimeWindow consists of a startTime and endTime.
 *
 * @author stefan schroeder
 */

public class TimeWindow {

    private final double start;
	private final double end;

	/**
     * Constructs the timeWindow
     *
     * @param start
     * @param end
     * @throw IllegalArgumentException either if start or end < 0.0 or end < start
     */
    public TimeWindow(double start, double end) {
        if (start < 0.0 || end < 0.0) {
			throw new IllegalArgumentException(new StringBuilder().append("neither time window start nor end must be < 0.0: ").append("[start=").append(start).append("][end=").append(end).append("]").toString());
		}
        if (end < start) {
			throw new IllegalArgumentException(new StringBuilder().append("time window end cannot be smaller than its start: ").append("[start=").append(start).append("][end=").append(end).append("]").toString());
		}
        this.start = start;
        this.end = end;
    }

	/**
     * Returns new instance of TimeWindow.
     *
     * @param start
     * @param end
     * @return TimeWindow
     * @throw IllegalArgumentException either if start or end < 0.0 or end < start
     */
    public static TimeWindow newInstance(double start, double end) {
        return new TimeWindow(start, end);
    }

	/**
     * Returns startTime of TimeWindow.
     *
     * @return startTime
     */
    public double getStart() {
        return start;
    }

	/**
     * Returns endTime of TimeWindow.
     *
     * @return endTime
     */
    public double getEnd() {
        return end;
    }

	public boolean larger(TimeWindow timeWindow) {
        return (this.getEnd() - this.getStart()) > (timeWindow.getEnd() - timeWindow.getStart());
    }

	@Override
    public String toString() {
        return new StringBuilder().append("[start=").append(start).append("][end=").append(end).append("]").toString();
    }

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(end);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(start);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

	/**
     * Two timeWindows are equal if they have the same start AND endTime.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
			return true;
		}
        if (obj == null) {
			return false;
		}
        if (getClass() != obj.getClass()) {
			return false;
		}
        TimeWindow other = (TimeWindow) obj;
        if (Double.doubleToLongBits(end) != Double.doubleToLongBits(other.end)) {
			return false;
		}
        if (Double.doubleToLongBits(start) != Double
            .doubleToLongBits(other.start)) {
			return false;
		}
        return true;
    }


}
