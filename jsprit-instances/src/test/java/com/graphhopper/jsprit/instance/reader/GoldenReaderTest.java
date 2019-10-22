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

package com.graphhopper.jsprit.instance.reader;

import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.instance.reader.VrphGoldenReader.VrphType;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

public class GoldenReaderTest {

    @Test
    public void whenReadingInstance_itShouldReadCorrectNuOfVehicles() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        assertEquals(17, vrp.getVehicles().size());
    }

    private String getPath(String string) {
        URL resource = this.getClass().getClassLoader().getResource(string);
        if (resource == null) {
			throw new IllegalStateException(new StringBuilder().append("resource ").append(string).append(" does not exist").toString());
		}
        return resource.getPath();
    }

    @Test
    public void whenReadingInstance_itShouldReadCorrectNuOfType1Vehicles() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        int nuOfType1Vehicles = 0;
        for (Vehicle v : vrp.getVehicles()) {
            if ("type_1".equals(v.getType().getTypeId())) {
                nuOfType1Vehicles++;
            }
        }
        assertEquals(4, nuOfType1Vehicles);
    }

    @Test
    public void whenReadingInstance_theSumOfType1VehicleShouldHvTheCorrectCapacity() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        int sumOfType1Cap = 0;
        for (Vehicle v : vrp.getVehicles()) {
            if ("type_1".equals(v.getType().getTypeId())) {
                sumOfType1Cap += v.getType().getCapacityDimensions().get(0);
            }
        }
        assertEquals(80, sumOfType1Cap);
    }

    @Test
    public void whenReadingInstance_itShouldReadCorrectNuOfType2Vehicles() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        int nuOfType1Vehicles = 0;
        for (Vehicle v : vrp.getVehicles()) {
            if ("type_2".equals(v.getType().getTypeId())) {
                nuOfType1Vehicles++;
            }
        }
        assertEquals(2, nuOfType1Vehicles);
    }

    @Test
    public void whenReadingInstance_theSumOfType2VehicleShouldHvTheCorrectCapacity() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        int sumOfType1Cap = 0;
        for (Vehicle v : vrp.getVehicles()) {
            if ("type_2".equals(v.getType().getTypeId())) {
                sumOfType1Cap += v.getType().getCapacityDimensions().get(0);
            }
        }
        assertEquals(60, sumOfType1Cap);
    }

    @Test
    public void whenReadingInstance_itShouldReadCorrectNuOfType3Vehicles() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        int nuOfType1Vehicles = 0;
        for (Vehicle v : vrp.getVehicles()) {
            if ("type_3".equals(v.getType().getTypeId())) {
                nuOfType1Vehicles++;
            }
        }
        assertEquals(4, nuOfType1Vehicles);
    }

    @Test
    public void whenReadingInstance_theSumOfType3VehicleShouldHvTheCorrectCapacity() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        int sumOfType1Cap = 0;
        for (Vehicle v : vrp.getVehicles()) {
            if ("type_3".equals(v.getType().getTypeId())) {
                sumOfType1Cap += v.getType().getCapacityDimensions().get(0);
            }
        }
        assertEquals(160, sumOfType1Cap);
    }

    @Test
    public void whenReadingInstance_itShouldReadCorrectNuOfType4Vehicles() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        int nuOfType1Vehicles = 0;
        for (Vehicle v : vrp.getVehicles()) {
            if ("type_4".equals(v.getType().getTypeId())) {
                nuOfType1Vehicles++;
            }
        }
        assertEquals(4, nuOfType1Vehicles);
    }

    @Test
    public void whenReadingInstance_theSumOfType4VehicleShouldHvTheCorrectCapacity() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        int sumOfType1Cap = 0;
        for (Vehicle v : vrp.getVehicles()) {
            if ("type_4".equals(v.getType().getTypeId())) {
                sumOfType1Cap += v.getType().getCapacityDimensions().get(0);
            }
        }
        assertEquals(280, sumOfType1Cap);
    }

    @Test
    public void whenReadingInstance_itShouldReadCorrectNuOfType5Vehicles() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        int nuOfType1Vehicles = 0;
        for (Vehicle v : vrp.getVehicles()) {
            if ("type_5".equals(v.getType().getTypeId())) {
                nuOfType1Vehicles++;
            }
        }
        assertEquals(2, nuOfType1Vehicles);
    }

    @Test
    public void whenReadingInstance_theSumOfType5VehicleShouldHvTheCorrectCapacity() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        int sumOfType1Cap = 0;
        for (Vehicle v : vrp.getVehicles()) {
            if ("type_5".equals(v.getType().getTypeId())) {
                sumOfType1Cap += v.getType().getCapacityDimensions().get(0);
            }
        }
        assertEquals(240, sumOfType1Cap);
    }

    @Test
    public void whenReadingInstance_itShouldReadCorrectNuOfType6Vehicles() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        int nuOfType1Vehicles = 0;
        for (Vehicle v : vrp.getVehicles()) {
            if ("type_6".equals(v.getType().getTypeId())) {
                nuOfType1Vehicles++;
            }
        }
        assertEquals(1, nuOfType1Vehicles);
    }

    @Test
    public void whenReadingInstance_theSumOfType6VehicleShouldHvTheCorrectCapacity() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        int sumOfType1Cap = 0;
        for (Vehicle v : vrp.getVehicles()) {
            if ("type_6".equals(v.getType().getTypeId())) {
                sumOfType1Cap += v.getType().getCapacityDimensions().get(0);
            }
        }
        assertEquals(200, sumOfType1Cap);
    }

    @Test
    public void whenReadingInstance_vehicleShouldHvTheCorrectCoord() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        vrp.getVehicles().forEach(v -> {
            if (v.getStartLocation().getCoordinate().getX() != 40.0) {
                assertFalse(true);
            }
            if (v.getStartLocation().getCoordinate().getY() != 40.0) {
                assertFalse(true);
            }
        });
        assertTrue(true);
    }

    @Test
    public void whenReadingInstance_service1MustHaveCorrectDemand() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        Job job = getJob("1", vrp);
        assertEquals(18, job.getSize().get(0));
    }

    @Test
    public void whenReadingInstance_service1MustHaveCorrectCoordinate() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        Coordinate coord = getCoord("1", vrp);
        assertEquals(22.0, coord.getX(), 0.01);
        assertEquals(22.0, coord.getY(), 0.01);
    }

    @Test
    public void whenReadingInstance_service15MustHaveCorrectCoordinate() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        Coordinate coord = getCoord("15", vrp);
        assertEquals(62.0, coord.getX(), 0.01);
        assertEquals(24.0, coord.getY(), 0.01);
    }


    @Test
    public void whenReadingInstance_service50MustHaveCorrectCoordinate() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        Coordinate coord = getCoord("50", vrp);
        assertEquals(15.0, coord.getX(), 0.01);
        assertEquals(56.0, coord.getY(), 0.01);
    }

    private Coordinate getCoord(String string, VehicleRoutingProblem vrp) {
        Job j = getJob(string, vrp);
        return ((Service) j).getLocation().getCoordinate();
    }

    @Test
    public void whenReadingInstance_service4MustHaveCorrectDemand() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        Job job = getJob("4", vrp);
        assertEquals(30, job.getSize().get(0));
    }

    @Test
    public void whenReadingInstance_service50MustHaveCorrectDemand() {
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        new VrphGoldenReader(vrpBuilder, VrphType.HVRPD)
            .read(getPath("cn_13mix.txt"));
        VehicleRoutingProblem vrp = vrpBuilder.build();
        Job job = getJob("50", vrp);
        assertEquals(22, job.getSize().get(0));
    }

    private Job getJob(String string, VehicleRoutingProblem vrp) {
        return vrp.getJobs().values().stream().filter(j -> j.getId().equals(string)).findFirst().orElse(null);
    }


}
