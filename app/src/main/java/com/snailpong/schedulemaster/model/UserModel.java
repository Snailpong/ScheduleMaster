package com.snailpong.schedulemaster.model;

import java.util.HashMap;
import java.util.Map;

public class UserModel {
    public String uid;
    public String userName;
    public String email;
    public String profileImageUrl;
    public Map<String, Boolean> friends = new HashMap<>();
}
