package com.thoughtworks.trains;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Town {
    String name;
    Map<Town, Integer> inTowns = new HashMap<>();
    Map<Town, Integer> outTowns = new HashMap<>();

    Town(String name) {
        this.name = name;
    }

    void addInTown(Town town, int distance) {
        inTowns.put(town, distance);
    }

    void addOutTown(Town town, int distance) {
        outTowns.put(town, distance);
    }

    int getDistanceTo(Town town) {
        return outTowns.get(town) == null ? -1 : outTowns.get(town);
    }

    public Map<Town, Integer> getInTowns() {
        return inTowns;
    }

    public Map<Town, Integer> getOutTowns() {
        return outTowns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Town town = (Town) o;
        return Objects.equals(name, town.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
