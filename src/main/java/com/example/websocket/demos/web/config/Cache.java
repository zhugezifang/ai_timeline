package com.example.websocket.demos.web.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class Cache {

    //public static HashMap<String,String> nameHash=new HashMap<>();
    //public static HashMap<String, LinkedList<Timeline>> timeLines=new HashMap<>();

    public static LinkedHashMap<String, String> nameHash = new LinkedHashMap<String, String>(
            (int)(500/ 0.75f) + 1, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 500;
        }
    };

    public static LinkedHashMap<String, LinkedList<Timeline>> timeLines = new LinkedHashMap<String, LinkedList<Timeline>>(
            (int)(500/ 0.75f) + 1, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 500;
        }
    };
}
