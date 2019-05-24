package com.thoughtworks.trains;

import java.util.*;
import java.util.function.Consumer;

/**
 * 图算法封装类
 */
public class GraphAlgorithms {

    public static final int UNREACHABLE = -1;

    /**
     * 计算一个给定路径的长度，从头开始顺序往后计算
     *
     * @param roadMap
     * @param townName
     * @return 给定路径的长度，如果路径不可达，返回-1
     */
    public static int distanceOfRoute(RoadMap roadMap, List<String> townName) {
        int distance = 0;
        for (int i = 0; i < townName.size() - 1; i++) {
            Town town = roadMap.getTown(townName.get(i));
            int dis = town.getDistanceTo(roadMap.getTown(townName.get(i + 1)));
            if (dis == -1) {
                return UNREACHABLE;
            } else {
                distance += dis;
            }
        }
        return distance;
    }

    public static Set<List<String>> tripsWithMaxStops(RoadMap roadMap, String startTown, String endTown, int maxStops) {
        if (!roadMap.isExist(startTown) || !roadMap.isExist(endTown)) {
            return new HashSet<>();
        }
        Town start = roadMap.getTown(startTown);
        Town end = roadMap.getTown(endTown);
        Set<List<String>> trips = tripsWithMaxStops(start, end, maxStops);
        return trips;
    }

    public static Set<List<String>> tripsWithExactlyStops(RoadMap roadMap, String startTown, String endTown, int exactlyStops) {
        if (!roadMap.isExist(startTown) || !roadMap.isExist(endTown)) {
            return new HashSet<>();
        }
        Town start = roadMap.getTown(startTown);
        Town end = roadMap.getTown(endTown);
        Set<List<String>> trips = tripsWithExactlyStops(start, end, exactlyStops);
        return trips;
    }

    public static int distanceOfShortestRoute(RoadMap roadMap, String startTown, String endTown) {
        if (!roadMap.isExist(startTown) || !roadMap.isExist(endTown)) {
            return UNREACHABLE;
        }
        Town start = roadMap.getTown(startTown);
        Town end = roadMap.getTown(endTown);
        Tuple<Set<Deque<Town>>, Integer> tuple = distanceOfShortestRouteV2(start, end, new HashSet<>());
        return tuple.t2;
    }

    public static Set<List<String>> tripsWithMaxDistance(RoadMap roadMap, String startTown, String endTown, int maxDistance) {
        if (!roadMap.isExist(startTown) || !roadMap.isExist(endTown)) {
            return new HashSet<>();
        }
        Town start = roadMap.getTown(startTown);
        Town end = roadMap.getTown(endTown);
        return tripsWithMaxDistance(start, end, maxDistance);
    }

    /**
     * 计算两个Town之间可能的长度小于maxDistance的路径
     *
     * @param startTown
     * @param endTown
     * @param maxDistance
     * @return 小于maxDistance的路径集合
     */
    private static Set<List<String>> tripsWithMaxDistance(Town startTown, Town endTown, int maxDistance) {
        Set<List<String>> trips = new HashSet<>();
        Set<Town> towns = startTown.getOutTowns().keySet();
        for (Town town : towns) {
            int distance = startTown.getDistanceTo(town);
            if (distance >= maxDistance) continue; //如果distance大于等于maxDistance,那么没有必要从town继续往下走
            if (endTown.equals(town)) {
                if (distance < maxDistance) {
                    //找到一条路径
                    List<String> trip = new ArrayList<>();
                    trip.add(startTown.name);
                    trip.add(endTown.name);
                    trips.add(trip);
                }
            }

            //从town继续往下走
            int subMaxDistance = maxDistance - distance;
            Set<List<String>> subTrips = tripsWithMaxDistance(town, endTown, subMaxDistance);
            for (List<String> subTrip : subTrips) {
                //将找到的子路径加到当前路径
                List<String> trip = new ArrayList<>();
                trip.add(startTown.name);
                trip.addAll(subTrip);
                trips.add(trip);
            }
        }
        return trips;
    }

    /**
     * 计算两个Town之间的最短路径,返回的路径为最后找到的最短路径,建议使用distanceOfShortestRouteV2,
     * 该方法会返回所有的最短路径
     *
     * @param startTown
     * @param endTown
     * @param hasVisited 用于避免循环遍历
     * @return 最短路径以及长度
     */
    @Deprecated
    private static Tuple<Deque<Town>, Integer> distanceOfShortestRoute(Town startTown, Town endTown, Set<Town> hasVisited) {
        Set<Town> towns = startTown.getOutTowns().keySet();
        int minDistance = UNREACHABLE;
        Deque<Town> stack = new ArrayDeque<>();
        if (hasVisited.contains(startTown)) {
            return new Tuple<>(stack, minDistance);
        }
        for (Town town : towns) {
            int distance = startTown.getDistanceTo(town);
            if (endTown.equals(town)) {
                if (minDistance == UNREACHABLE || minDistance > distance) {
                    minDistance = distance;
                    stack.clear();
                    stack.add(startTown);
                    stack.add(endTown);

                }
            } else {
                if (minDistance != UNREACHABLE && distance >= minDistance) {
                    continue;
                }
                hasVisited.add(startTown);
                Tuple<Deque<Town>, Integer> subTuple = distanceOfShortestRoute(town, endTown, hasVisited);
                int subDistance = subTuple.t2;
                hasVisited.remove(startTown);
                if (subDistance != -1) {
                    if (minDistance == UNREACHABLE || minDistance > distance + subDistance) {
                        minDistance = distance + subDistance;
                        subTuple.t1.addFirst(startTown);
                        stack = subTuple.t1;
                    }
                }
            }
        }
        return new Tuple<>(stack, minDistance);
    }

    /**
     * 返回两个Town之间所有的最短路径以及路径长度
     *
     * @param startTown
     * @param endTown
     * @param hasVisited
     * @return
     */
    private static Tuple<Set<Deque<Town>>, Integer> distanceOfShortestRouteV2(Town startTown, Town endTown, Set<Town> hasVisited) {
        Set<Town> towns = startTown.getOutTowns().keySet();
        int minDistance = UNREACHABLE;
        Set<Deque<Town>> trips = new HashSet<>();//最短路径可能不止一条
        if (hasVisited.contains(startTown)) { //如果遍历过该节点，继续往下遍历的话肯定不是可能的最短路径，所以丢弃这条路径
            return new Tuple<>(trips, minDistance);
        }
        for (Town town : towns) {
            int distance = startTown.getDistanceTo(town);
            if (endTown.equals(town)) {//找到了一条路径
                if (minDistance == UNREACHABLE || minDistance > distance) {
                    //如果是第一次找到或者找到的是更短的路径，那么更新当前最短的长度，清空最短路径集合，添加一条新的最短路径
                    minDistance = distance;
                    trips.clear();
                    Deque<Town> trip = new ArrayDeque<>();
                    trip.add(startTown);
                    trip.add(endTown);
                    trips.add(trip);
                    continue;
                }
                if (minDistance != UNREACHABLE && minDistance == distance) {
                    //如果找到的路径和已经找到的最短路径一样长，那么添加一条最短路径
                    Deque<Town> trip = new ArrayDeque<>();
                    trip.add(startTown);
                    trip.add(endTown);
                    trips.add(trip);
                }
            } else {//需要递归变量子节点

                if (minDistance != -1 && distance >= minDistance) {//说明子路径一定比当前找到的最短路径长，直接跳过
                    continue;
                }
                hasVisited.add(startTown);
                Tuple<Set<Deque<Town>>, Integer> subTuple = distanceOfShortestRouteV2(town, endTown, hasVisited);
                int subDistance = subTuple.t2;
                hasVisited.remove(startTown);
                if (subDistance != UNREACHABLE) {//说明子路径可达
                    if (minDistance == UNREACHABLE || minDistance > distance + subDistance) { //找到了一条更短的子路径，那么更新当前的路径
                        minDistance = distance + subDistance;
                        subTuple.t1.forEach(new Consumer<Deque<Town>>() {
                            @Override
                            public void accept(Deque<Town> towns) {
                                towns.addFirst(startTown);
                            }
                        });
                        trips = subTuple.t1;
                        continue;
                    }
                    if (minDistance != UNREACHABLE && minDistance == distance + subDistance) {
                        //找到了一条新的最短路径
                        for (Deque<Town> subTrip : subTuple.t1) {
                            subTrip.addFirst(startTown);
                            trips.add(subTrip);
                        }
                    }
                }
            }
        }
        return new Tuple<>(trips, minDistance);
    }

    /**
     * 计算从startTown到endTown且最多停靠maxStops个站的路径
     *
     * @param startTown
     * @param endTown
     * @param maxStops
     * @return 所有可能的路径
     */
    private static Set<List<String>> tripsWithMaxStops(Town startTown, Town endTown, int maxStops) {
        Set<List<String>> trips = new HashSet<>();
        Set<Town> towns = startTown.getOutTowns().keySet();
        if (maxStops < 1) {
            return trips;
        }
        for (Town town : towns) {
            if (town.equals(endTown)) {
                //如果startTown可以直接到达endTown,那么我们找到了一条路径
                List<String> trip = new ArrayList<>();
                trip.add(startTown.name);
                trip.add(endTown.name);
                trips.add(trip);
            }
        }

        if (maxStops > 1) {
            //如果maxStops>1,说明我们可以从子节点继续往下查找,子路径允许的maxStops变为了了当前maxStops-1
            int newMaxStop = --maxStops;
            for (Town town : towns) {
                Set<List<String>> subTrips = tripsWithMaxStops(town, endTown, newMaxStop);
                //将子路径添加到当前路径
                for (List<String> subTrip : subTrips) {
                    List<String> trip = new ArrayList<>();
                    trip.add(startTown.name);
                    trip.addAll(subTrip);
                    trips.add(trip);
                }
            }
        }
        return trips;
    }

    /**
     * 计算从startTown到endTown且靠站次数恰好为exactlyStops的路径
     *
     * @param startTown
     * @param endTown
     * @param exactlyStops
     * @return 所有可能的路径
     */
    private static Set<List<String>> tripsWithExactlyStops(Town startTown, Town endTown, int exactlyStops) {
        Set<List<String>> trips = new HashSet<>();
        Set<Town> towns = startTown.getOutTowns().keySet();
        if (exactlyStops > 1) {
            //如果还要停靠的站数大于1，那么必须从子节点继续往下找，子路径只允许停靠--exactlyStops个站
            int newExactlyStop = --exactlyStops;
            for (Town town : towns) {
                Set<List<String>> subTrips = tripsWithExactlyStops(town, endTown, newExactlyStop);
                for (List<String> subTrip : subTrips) {
                    //将子路径添加到当前路径
                    List<String> trip = new ArrayList<>();
                    trip.add(startTown.name);
                    trip.addAll(subTrip);
                    trips.add(trip);
                }
            }
        } else if (exactlyStops == 1) {
            //如果还要停靠1站，那么只能是子节点等于endTown
            for (Town town : towns) {
                if (town.equals(endTown)) {
                    //找到一条路径
                    List<String> trip = new ArrayList<>();
                    trip.add(startTown.name);
                    trip.add(endTown.name);
                    trips.add(trip);
                }
            }
        }
        return trips;
    }

    private static class Tuple<T1, T2> {
        T1 t1;
        T2 t2;

        Tuple(T1 t1, T2 t2) {
            this.t1 = t1;
            this.t2 = t2;
        }
    }
}
