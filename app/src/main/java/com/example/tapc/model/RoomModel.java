package com.example.tapc.model;

import java.util.HashMap;
import java.util.Map;

public class RoomModel {
    public String userUID;
    public String roomKEY;
    public String roomTitle;
    //public Map<String, Object> users = new HashMap<>();
    public Map<String, Comment> comments = new HashMap<>();
    public static class Comment {
        public String name;
        public String colon = " : ";
        public String message;
    }
}
