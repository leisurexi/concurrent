package com.leisurexi.concurrent.lock;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: leisurexi
 * @date: 2020-02-14 20:26
 * @description:
 * @since JDK 1.8
 */
public class MonitorVehicleTracker {

    private final Map<String, MutablePoint> locations;

    public MonitorVehicleTracker(Map<String, MutablePoint> locations) {
        this.locations = deepCopy(locations);
    }

    public synchronized Map<String, MutablePoint> getLocations() {
        return deepCopy(locations);
    }

    public synchronized MutablePoint getLocation(String id) {
        MutablePoint point = locations.get(id);
        return point == null ? null : new MutablePoint(point);
    }

    public synchronized void setLocation(String id, int x, int y) {
        MutablePoint point = locations.get(id);
        if (point == null) {
            throw new IllegalArgumentException("No Such ID: " + id);
        }
        point.x = x;
        point.y = y;
    }

    private static Map<String, MutablePoint> deepCopy(Map<String, MutablePoint> mutablePointMap) {
        Map<String, MutablePoint> result = new HashMap<>();
        for (Map.Entry<String, MutablePoint> entry : mutablePointMap.entrySet()) {
            result.put(entry.getKey(), new MutablePoint(entry.getValue()));
        }
        return Collections.unmodifiableMap(result);
    }

    static class MutablePoint {

        public int x, y;

        public MutablePoint() {
            this.x = 0;
            this.y = 0;
        }

        public MutablePoint(MutablePoint point) {
            this.x = point.x;
            this.y = point.y;
        }

    }

}
