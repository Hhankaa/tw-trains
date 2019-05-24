package com.thoughtworks.trains;

import java.net.URL;
import java.util.*;

public class Driver {
    public static void main(String[] args) {
        try {
            Properties prop = new Properties();
            URL url = Thread.currentThread().getContextClassLoader().getResource("conf.properties");
            prop.load(url.openStream());
            String graph = prop.getProperty("train.graph");
            RoadMap roadMap = new RoadMap();
            boolean result = roadMap.init(graph);
            if (!result) {
                throw new Exception("roadMap init error.please check your configuration. graph = " + graph);
            }
            int distance = GraphAlgorithms.distanceOfRoute(roadMap, genRoute("A-B-C"));
            System.out.println("Output #1: " + transform(distance));
            distance = GraphAlgorithms.distanceOfRoute(roadMap, genRoute("A-D"));
            System.out.println("Output #2: " + transform(distance));
            distance = GraphAlgorithms.distanceOfRoute(roadMap, genRoute("A-D-C"));
            System.out.println("Output #3: " + transform(distance));
            distance = GraphAlgorithms.distanceOfRoute(roadMap, genRoute("A-E-B-C-D"));
            System.out.println("Output #4: " + transform(distance));
            distance = GraphAlgorithms.distanceOfRoute(roadMap, genRoute("A-E-D"));
            System.out.println("Output #5: " + transform(distance));

            Set<List<String>> trips = GraphAlgorithms.tripsWithMaxStops(roadMap, "C", "C", 3);
            System.out.println("Output #6: " + trips.size());
            trips = GraphAlgorithms.tripsWithExactlyStops(roadMap, "A", "C", 4);
            System.out.println("Output #7: " + trips.size());
            int shortestDistance = GraphAlgorithms.distanceOfShortestRoute(roadMap, "A", "C");
            System.out.println("Output #8: " + transform(shortestDistance));
            shortestDistance = GraphAlgorithms.distanceOfShortestRoute(roadMap, "B", "B");
            System.out.println("Output #9: " + transform(shortestDistance));
            trips = GraphAlgorithms.tripsWithMaxDistance(roadMap, "C", "C", 30);
            System.out.println("Output #10: " + trips.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static List<String> genRoute(String routeStr) {
        String[] arr = routeStr.split("-");
        Arrays.asList(arr);
        return Arrays.asList(arr);
    }

    static String transform(int distance) {
        return distance == GraphAlgorithms.UNREACHABLE ? "NO SUCH ROUTE" : distance + "";
    }
}
