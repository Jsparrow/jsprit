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
package com.graphhopper.jsprit.io.problem;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.Skills;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Break;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleIndexComparator;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class VrpXMLWriter {

    private static Logger logger = LoggerFactory.getLogger(VrpXMLWriter.class);

	private Logger log = LoggerFactory.getLogger(VrpXMLWriter.class);

	private VehicleRoutingProblem vrp;

	private Collection<VehicleRoutingProblemSolution> solutions;

	private boolean onlyBestSolution = false;

	public VrpXMLWriter(VehicleRoutingProblem vrp, Collection<VehicleRoutingProblemSolution> solutions, boolean onlyBestSolution) {
        this.vrp = vrp;
        this.solutions = new ArrayList<>(solutions);
        this.onlyBestSolution = onlyBestSolution;
    }

	public VrpXMLWriter(VehicleRoutingProblem vrp, Collection<VehicleRoutingProblemSolution> solutions) {
        this.vrp = vrp;
        this.solutions = solutions;
    }

	public VrpXMLWriter(VehicleRoutingProblem vrp) {
        this.vrp = vrp;
        this.solutions = null;
    }

	public void write(String filename) {
        if (!filename.endsWith(".xml")) {
			filename += ".xml";
		}
        log.info("write vrp: " + filename);
        XMLConf xmlConfig = createXMLConfiguration();

        try {
            xmlConfig.setFileName(filename);
            Writer out = new FileWriter(filename);
            XMLSerializer serializer = new XMLSerializer(out, createOutputFormat());
            serializer.serialize(xmlConfig.getDocument());
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	public OutputStream write() {
        XMLConf xmlConfig = createXMLConfiguration();
        OutputStream out = new ByteArrayOutputStream();

        try {
            XMLSerializer serializer = new XMLSerializer(out, createOutputFormat());
            serializer.serialize(xmlConfig.getDocument());
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return out;
    }

	private XMLConf createXMLConfiguration() {
        XMLConf xmlConfig = new XMLConf();
        xmlConfig.setRootElementName("problem");
        xmlConfig.setAttributeSplittingDisabled(true);
        xmlConfig.setDelimiterParsingDisabled(true);

        writeProblemType(xmlConfig);
        writeVehiclesAndTheirTypes(xmlConfig);

        //might be sorted?
        List<Job> jobs = new ArrayList<>();
        jobs.addAll(vrp.getJobs().values());
        vrp.getInitialVehicleRoutes().forEach(r -> jobs.addAll(r.getTourActivities().getJobs()));

        writeServices(xmlConfig, jobs);
        writeShipments(xmlConfig, jobs);

        writeInitialRoutes(xmlConfig);
        if(onlyBestSolution && solutions != null) {
            VehicleRoutingProblemSolution solution = Solutions.bestOf(solutions);
            solutions.clear();
            solutions.add(solution);
        }

        writeSolutions(xmlConfig);


        try {
            Document document = xmlConfig.createDoc();

            Element element = document.getDocumentElement();
            element.setAttribute("xmlns", "http://www.w3schools.com");
            element.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            element.setAttribute("xsi:schemaLocation", "http://www.w3schools.com vrp_xml_schema.xsd");

        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
        return xmlConfig;
    }

	private OutputFormat createOutputFormat() {
        OutputFormat format = new OutputFormat();
        format.setIndenting(true);
        format.setIndent(5);
        return format;
    }

	private void writeInitialRoutes(XMLConf xmlConfig) {
        if (vrp.getInitialVehicleRoutes().isEmpty()) {
			return;
		}
        String path = "initialRoutes.route";
        int routeCounter = 0;
        for (VehicleRoute route : vrp.getInitialVehicleRoutes()) {
            xmlConfig.setProperty(new StringBuilder().append(path).append("(").append(routeCounter).append(").driverId").toString(), route.getDriver().getId());
            xmlConfig.setProperty(new StringBuilder().append(path).append("(").append(routeCounter).append(").vehicleId").toString(), route.getVehicle().getId());
            xmlConfig.setProperty(new StringBuilder().append(path).append("(").append(routeCounter).append(").start").toString(), route.getStart().getEndTime());
            int actCounter = 0;
            for (TourActivity act : route.getTourActivities().getActivities()) {
                xmlConfig.setProperty(new StringBuilder().append(path).append("(").append(routeCounter).append(").act(").append(actCounter).append(")[@type]")
						.toString(), act.getName());
                if (act instanceof TourActivity.JobActivity) {
                    Job job = ((TourActivity.JobActivity) act).getJob();
                    if (job instanceof Service) {
                        xmlConfig.setProperty(new StringBuilder().append(path).append("(").append(routeCounter).append(").act(").append(actCounter)
								.append(").serviceId").toString(), job.getId());
                    } else if (job instanceof Shipment) {
                        xmlConfig.setProperty(new StringBuilder().append(path).append("(").append(routeCounter).append(").act(").append(actCounter)
								.append(").shipmentId").toString(), job.getId());
                    } else if (job instanceof Break) {
                    	xmlConfig.setProperty(new StringBuilder().append(path).append("(").append(routeCounter).append(").act(").append(actCounter)
								.append(").breakId").toString(), job.getId());
                    } else {
                        throw new IllegalStateException("cannot write solution correctly since job-type is not know. make sure you use either service or shipment, or another writer");
                    }
                }
                xmlConfig.setProperty(new StringBuilder().append(path).append("(").append(routeCounter).append(").act(").append(actCounter).append(").arrTime")
						.toString(), act.getArrTime());
                xmlConfig.setProperty(new StringBuilder().append(path).append("(").append(routeCounter).append(").act(").append(actCounter).append(").endTime")
						.toString(), act.getEndTime());
                actCounter++;
            }
            xmlConfig.setProperty(new StringBuilder().append(path).append("(").append(routeCounter).append(").end").toString(), route.getEnd().getArrTime());
            routeCounter++;
        }

    }

	private void writeSolutions(XMLConf xmlConfig) {
        if (solutions == null) {
			return;
		}
        String solutionPath = "solutions.solution";
        int counter = 0;
        for (VehicleRoutingProblemSolution solution : solutions) {
            xmlConfig.setProperty(new StringBuilder().append(solutionPath).append("(").append(counter).append(").cost").toString(), solution.getCost());
            int routeCounter = 0;
            List<VehicleRoute> list = new ArrayList<>(solution.getRoutes());
            list.sort(new VehicleIndexComparator());
            for (VehicleRoute route : list) {
//				xmlConfig.setProperty(solutionPath + "(" + counter + ").routes.route(" + routeCounter + ").cost", route.getCost());
                xmlConfig.setProperty(new StringBuilder().append(solutionPath).append("(").append(counter).append(").routes.route(").append(routeCounter).append(").driverId")
						.toString(), route.getDriver().getId());
                xmlConfig.setProperty(new StringBuilder().append(solutionPath).append("(").append(counter).append(").routes.route(").append(routeCounter).append(").vehicleId")
						.toString(), route.getVehicle().getId());
                xmlConfig.setProperty(new StringBuilder().append(solutionPath).append("(").append(counter).append(").routes.route(").append(routeCounter).append(").start")
						.toString(), route.getStart().getEndTime());
                int actCounter = 0;
                for (TourActivity act : route.getTourActivities().getActivities()) {
                    xmlConfig.setProperty(new StringBuilder().append(solutionPath).append("(").append(counter).append(").routes.route(").append(routeCounter).append(").act(")
							.append(actCounter).append(")[@type]").toString(), act.getName());
                    if (act instanceof TourActivity.JobActivity) {
                        Job job = ((TourActivity.JobActivity) act).getJob();
                        if (job instanceof Break) {
                            xmlConfig.setProperty(new StringBuilder().append(solutionPath).append("(").append(counter).append(").routes.route(").append(routeCounter)
									.append(").act(").append(actCounter).append(").breakId").toString(), job.getId());
                        } else if (job instanceof Service) {
                            xmlConfig.setProperty(new StringBuilder().append(solutionPath).append("(").append(counter).append(").routes.route(").append(routeCounter)
									.append(").act(").append(actCounter).append(").serviceId").toString(), job.getId());
                        } else if (job instanceof Shipment) {
                            xmlConfig.setProperty(new StringBuilder().append(solutionPath).append("(").append(counter).append(").routes.route(").append(routeCounter)
									.append(").act(").append(actCounter).append(").shipmentId").toString(), job.getId());
                        } else {
                            throw new IllegalStateException("cannot write solution correctly since job-type is not know. make sure you use either service or shipment, or another writer");
                        }
                    }
                    xmlConfig.setProperty(new StringBuilder().append(solutionPath).append("(").append(counter).append(").routes.route(").append(routeCounter).append(").act(")
							.append(actCounter).append(").arrTime").toString(), act.getArrTime());
                    xmlConfig.setProperty(new StringBuilder().append(solutionPath).append("(").append(counter).append(").routes.route(").append(routeCounter).append(").act(")
							.append(actCounter).append(").endTime").toString(), act.getEndTime());
                    actCounter++;
                }
                xmlConfig.setProperty(new StringBuilder().append(solutionPath).append("(").append(counter).append(").routes.route(").append(routeCounter).append(").end")
						.toString(), route.getEnd().getArrTime());
                routeCounter++;
            }
            int unassignedJobCounter = 0;
            for (Job unassignedJob : solution.getUnassignedJobs()) {
                xmlConfig.setProperty(new StringBuilder().append(solutionPath).append("(").append(counter).append(").unassignedJobs.job(").append(unassignedJobCounter).append(")[@id]")
						.toString(), unassignedJob.getId());
                unassignedJobCounter++;
            }
            counter++;
        }
    }

	private void writeServices(XMLConf xmlConfig, List<Job> jobs) {
        String shipmentPathString = "services.service";
        int counter = 0;
        for (Job j : jobs) {
            if (!(j instanceof Service)) {
				continue;
			}
            Service service = (Service) j;
            xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(")[@id]").toString(), service.getId());
            xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(")[@type]").toString(), service.getType());
            if (service.getLocation().getId() != null) {
				xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").location.id").toString(), service.getLocation().getId());
			}
            if (service.getLocation().getCoordinate() != null) {
                xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").location.coord[@x]").toString(), service.getLocation().getCoordinate().getX());
                xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").location.coord[@y]").toString(), service.getLocation().getCoordinate().getY());
            }
            if (service.getLocation().getIndex() != Location.NO_INDEX) {
                xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").location.index").toString(), service.getLocation().getIndex());
            }
            for (int i = 0; i < service.getSize().getNuOfDimensions(); i++) {
                xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").capacity-dimensions.dimension(").append(i).append(")[@index]")
						.toString(), i);
                xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").capacity-dimensions.dimension(").append(i).append(")")
						.toString(), service.getSize().get(i));
            }

            Collection<TimeWindow> tws = service.getTimeWindows();
            int index = 0;
            xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").duration").toString(), service.getServiceDuration());
            for(TimeWindow tw : tws) {
	            xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").timeWindows.timeWindow(").append(index).append(").start")
						.toString(), tw.getStart());
	            xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").timeWindows.timeWindow(").append(index).append(").end")
						.toString(), tw.getEnd());
	            ++index;
            }

            //skills
            String skillString = getSkillString(service);
            xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").requiredSkills").toString(), skillString);

            boolean condition = service.getName() != null && !"no-name".equals(service.getName());
			//name
            if (condition) {
			    xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").name").toString(), service.getName());
			}
            counter++;
        }
    }

	private void writeShipments(XMLConf xmlConfig, List<Job> jobs) {
        String shipmentPathString = "shipments.shipment";
        int counter = 0;
        for (Job j : jobs) {
            if (!(j instanceof Shipment)) {
				continue;
			}
            Shipment shipment = (Shipment) j;
            xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(")[@id]").toString(), shipment.getId());
//			xmlConfig.setProperty(shipmentPathString + "("+counter+")[@type]", service.getType());
            if (shipment.getPickupLocation().getId() != null) {
				xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").pickup.location.id").toString(), shipment.getPickupLocation().getId());
			}
            if (shipment.getPickupLocation().getCoordinate() != null) {
                xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").pickup.location.coord[@x]").toString(), shipment.getPickupLocation().getCoordinate().getX());
                xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").pickup.location.coord[@y]").toString(), shipment.getPickupLocation().getCoordinate().getY());
            }
            if (shipment.getPickupLocation().getIndex() != Location.NO_INDEX) {
                xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").pickup.location.index").toString(), shipment.getPickupLocation().getIndex());
            }

            Collection<TimeWindow> pu_tws = shipment.getPickupTimeWindows();
            int index = 0;
            xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").pickup.duration").toString(), shipment.getPickupServiceTime());
            for(TimeWindow tw : pu_tws) {
	            xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").pickup.timeWindows.timeWindow(").append(index).append(").start")
						.toString(), tw.getStart());
	            xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").pickup.timeWindows.timeWindow(").append(index).append(").end")
						.toString(), tw.getEnd());
	            ++index;
	        }

            if (shipment.getDeliveryLocation().getId() != null) {
				xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").delivery.location.id").toString(), shipment.getDeliveryLocation().getId());
			}
            if (shipment.getDeliveryLocation().getCoordinate() != null) {
                xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").delivery.location.coord[@x]").toString(), shipment.getDeliveryLocation().getCoordinate().getX());
                xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").delivery.location.coord[@y]").toString(), shipment.getDeliveryLocation().getCoordinate().getY());
            }
            if (shipment.getDeliveryLocation().getIndex() != Location.NO_INDEX) {
                xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").delivery.location.index").toString(), shipment.getDeliveryLocation().getIndex());
            }

            Collection<TimeWindow> del_tws = shipment.getDeliveryTimeWindows();
        	xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").delivery.duration").toString(), shipment.getDeliveryServiceTime());
        	index = 0;
            for(TimeWindow tw : del_tws) {
            	xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").delivery.timeWindows.timeWindow(").append(index).append(").start")
						.toString(), tw.getStart());
            	xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").delivery.timeWindows.timeWindow(").append(index).append(").end")
						.toString(), tw.getEnd());
            	++index;
            }

            for (int i = 0; i < shipment.getSize().getNuOfDimensions(); i++) {
                xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").capacity-dimensions.dimension(").append(i).append(")[@index]")
						.toString(), i);
                xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").capacity-dimensions.dimension(").append(i).append(")")
						.toString(), shipment.getSize().get(i));
            }

            //skills
            String skillString = getSkillString(shipment);
            xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").requiredSkills").toString(), skillString);

            boolean condition = shipment.getName() != null && !"no-name".equals(shipment.getName());
			//name
            if (condition) {
			    xmlConfig.setProperty(new StringBuilder().append(shipmentPathString).append("(").append(counter).append(").name").toString(), shipment.getName());
			}
            counter++;
        }
    }

	private void writeProblemType(XMLConfiguration xmlConfig) {
        xmlConfig.setProperty("problemType.fleetSize", vrp.getFleetSize());
    }

	private void writeVehiclesAndTheirTypes(XMLConfiguration xmlConfig) {

        //vehicles
        String vehiclePathString = new StringBuilder().append(Schema.VEHICLES).append(".").append(Schema.VEHICLE).toString();
        int counter = 0;
        for (Vehicle vehicle : vrp.getVehicles()) {
            xmlConfig.setProperty(new StringBuilder().append(vehiclePathString).append("(").append(counter).append(").id").toString(), vehicle.getId());
            xmlConfig.setProperty(new StringBuilder().append(vehiclePathString).append("(").append(counter).append(").typeId").toString(), vehicle.getType().getTypeId());
            xmlConfig.setProperty(new StringBuilder().append(vehiclePathString).append("(").append(counter).append(").startLocation.id").toString(), vehicle.getStartLocation().getId());
            if (vehicle.getStartLocation().getCoordinate() != null) {
                xmlConfig.setProperty(new StringBuilder().append(vehiclePathString).append("(").append(counter).append(").startLocation.coord[@x]").toString(), vehicle.getStartLocation().getCoordinate().getX());
                xmlConfig.setProperty(new StringBuilder().append(vehiclePathString).append("(").append(counter).append(").startLocation.coord[@y]").toString(), vehicle.getStartLocation().getCoordinate().getY());
            }
            if (vehicle.getStartLocation().getIndex() != Location.NO_INDEX) {
                xmlConfig.setProperty(new StringBuilder().append(vehiclePathString).append("(").append(counter).append(").startLocation.index").toString(), vehicle.getStartLocation().getIndex());
            }

            xmlConfig.setProperty(new StringBuilder().append(vehiclePathString).append("(").append(counter).append(").endLocation.id").toString(), vehicle.getEndLocation().getId());
            if (vehicle.getEndLocation().getCoordinate() != null) {
                xmlConfig.setProperty(new StringBuilder().append(vehiclePathString).append("(").append(counter).append(").endLocation.coord[@x]").toString(), vehicle.getEndLocation().getCoordinate().getX());
                xmlConfig.setProperty(new StringBuilder().append(vehiclePathString).append("(").append(counter).append(").endLocation.coord[@y]").toString(), vehicle.getEndLocation().getCoordinate().getY());
            }
            if (vehicle.getEndLocation().getIndex() != Location.NO_INDEX) {
                xmlConfig.setProperty(new StringBuilder().append(vehiclePathString).append("(").append(counter).append(").endLocation.index").toString(), vehicle.getEndLocation().getId());
            }
            xmlConfig.setProperty(new StringBuilder().append(vehiclePathString).append("(").append(counter).append(").timeSchedule.start").toString(), vehicle.getEarliestDeparture());
            xmlConfig.setProperty(new StringBuilder().append(vehiclePathString).append("(").append(counter).append(").timeSchedule.end").toString(), vehicle.getLatestArrival());

            if (vehicle.getBreak() != null) {
                Collection<TimeWindow> tws = vehicle.getBreak().getTimeWindows();
                int index = 0;
	            xmlConfig.setProperty(new StringBuilder().append(vehiclePathString).append("(").append(counter).append(").breaks.duration").toString(), vehicle.getBreak().getServiceDuration());
                for(TimeWindow tw : tws) {
		            xmlConfig.setProperty(new StringBuilder().append(vehiclePathString).append("(").append(counter).append(").breaks.timeWindows.timeWindow(").append(index).append(").start")
							.toString(), tw.getStart());
		            xmlConfig.setProperty(new StringBuilder().append(vehiclePathString).append("(").append(counter).append(").breaks.timeWindows.timeWindow(").append(index).append(").end")
							.toString(), tw.getEnd());
		            ++index;
                }
	        }
            xmlConfig.setProperty(new StringBuilder().append(vehiclePathString).append("(").append(counter).append(").returnToDepot").toString(), vehicle.isReturnToDepot());

            //write skills
            String skillString = getSkillString(vehicle);
            xmlConfig.setProperty(new StringBuilder().append(vehiclePathString).append("(").append(counter).append(").skills").toString(), skillString);

            counter++;
        }

        //types
        String typePathString = Schema.builder().append(Schema.TYPES).dot(Schema.TYPE).build();
        int typeCounter = 0;
        for (VehicleType type : vrp.getTypes()) {
            xmlConfig.setProperty(new StringBuilder().append(typePathString).append("(").append(typeCounter).append(").id").toString(), type.getTypeId());

            for (int i = 0; i < type.getCapacityDimensions().getNuOfDimensions(); i++) {
                xmlConfig.setProperty(new StringBuilder().append(typePathString).append("(").append(typeCounter).append(").capacity-dimensions.dimension(").append(i).append(")[@index]")
						.toString(), i);
                xmlConfig.setProperty(new StringBuilder().append(typePathString).append("(").append(typeCounter).append(").capacity-dimensions.dimension(").append(i).append(")")
						.toString(), type.getCapacityDimensions().get(i));
            }

            xmlConfig.setProperty(new StringBuilder().append(typePathString).append("(").append(typeCounter).append(").costs.fixed").toString(), type.getVehicleCostParams().fix);
            xmlConfig.setProperty(new StringBuilder().append(typePathString).append("(").append(typeCounter).append(").costs.distance").toString(), type.getVehicleCostParams().perDistanceUnit);
            xmlConfig.setProperty(new StringBuilder().append(typePathString).append("(").append(typeCounter).append(").costs.time").toString(), type.getVehicleCostParams().perTransportTimeUnit);
            xmlConfig.setProperty(new StringBuilder().append(typePathString).append("(").append(typeCounter).append(").costs.service").toString(), type.getVehicleCostParams().perServiceTimeUnit);
            xmlConfig.setProperty(new StringBuilder().append(typePathString).append("(").append(typeCounter).append(").costs.wait").toString(), type.getVehicleCostParams().perWaitingTimeUnit);
            typeCounter++;
        }


    }

	private String getSkillString(Vehicle vehicle) {
        return createSkillString(vehicle.getSkills());
    }

	private String getSkillString(Job job) {
        return createSkillString(job.getRequiredSkills());
    }

	private String createSkillString(Skills skills) {
        if (skills.values().size() == 0) {
			return null;
		}
        String skillString = null;
        for (String skill : skills.values()) {
            if (skillString == null) {
				skillString = skill;
			} else {
				skillString += ", " + skill;
			}
        }
        return skillString;
    }

	static class XMLConf extends XMLConfiguration {


        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public Document createDoc() throws ConfigurationException {
            return createDocument();
        }
    }


}

