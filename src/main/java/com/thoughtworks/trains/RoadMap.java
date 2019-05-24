package com.thoughtworks.trains;

import java.util.*;

public class RoadMap {

    private Map<String, Town> towns = new HashMap<>();

    protected boolean isValid(String graph) {
        try {
            if (null == graph || graph.trim().isEmpty()) {
                throw new Exception("graph should not be empty.");
            }
            String[] edges = graph.split(",");
            Set<String> processedEdges = new HashSet<>();
            for (String edge : edges) {
                edge = edge.trim();
                if (edge.length() != 3) {
                    throw new Exception(edge + "'s length not equal 3.");
                }
                String startTownName = edge.substring(0, 1);
                String endTownName = edge.substring(1, 2);
                int distance = Integer.parseInt(edge.substring(2, 3));
                if (startTownName.equals(endTownName)) {
                    throw new Exception(edge + " start town equal end town.");
                }
                edge = startTownName + "" + endTownName;
                if (!processedEdges.add(edge)) {
                    throw new Exception(edge + " add more times.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean init(String graph) {
        if (isValid(graph)) {
            String[] edges = graph.split(",");
            for (String edge : edges) {
                addEdge(edge.trim());
            }
            return true;
        }
        return false;
    }

    protected void addEdge(String edge) {
        String startTownName = edge.substring(0, 1);
        String endTownName = edge.substring(1, 2);
        int distance = Integer.parseInt(edge.substring(2, 3));
        Town startTown = towns.get(startTownName);
        if (null == startTown) {
            startTown = new Town(startTownName);
            towns.put(startTownName, startTown);
        }

        Town endTown = towns.get(endTownName);
        if (null == endTown) {
            endTown = new Town(endTownName);
            towns.put(endTownName, endTown);
        }
        startTown.addOutTown(endTown, distance);
        endTown.addInTown(startTown, distance);
    }

    public Town getTown(String townName) {
        return towns.get(townName);
    }

    public boolean isExist(String townName) {
        return towns.get(townName) == null ? false : true;
    }

}
