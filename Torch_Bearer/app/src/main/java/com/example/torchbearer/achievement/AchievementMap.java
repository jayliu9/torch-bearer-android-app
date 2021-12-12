package com.example.torchbearer.achievement;

import com.example.torchbearer.User;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class AchievementMap {

    private Map<String, Achievement> map;
    private String userToken;
    public AchievementMap () {
        createAchievementMap();
    }

    public AchievementMap(Map<String, Achievement> achievedMap) {
        createAchievementMap();
        for (String key : achievedMap.keySet()) {
            this.map.remove(key);
        }
    }

    private void createAchievementMap() {
        this.map = new HashMap<>();
        map.put("First Path", new Achievement("First Path", "First time start tracking path."));
        map.put("10 Feet", new Achievement("10 Feet", "Tracked 10-foot long path."));
        map.put("First Log", new Achievement("First Log", "Reached the first wood log."));
    }

    public void validateAchievements(Map<String, Achievement> achievedMap, User user) {
        for (String key : achievedMap.keySet()) {
            this.map.remove(key);
        }
        if (user.getPaths().size() == 1) {
            isAchievementValid("First Path", achievedMap);
        }
        if (user.getTotalLength() >= 100.0) {
            isAchievementValid("100 Feet", achievedMap);
        }
        if (user.getLogCount() == 1) {
            isAchievementValid("First Log", achievedMap);
        }
    }

    public void isAchievementValid(String title, Map<String, Achievement> achievedMap) {
        if (this.map.containsKey(title)) {
            Achievement achievement = this.map.get(title);
            achievement.setDate(Calendar.getInstance().getTime().toString());
            this.map.remove(achievement.getTitle());
            achievedMap.put(achievement.getTitle(), achievement);
        }
    }
    public Map<String, Achievement> getMap() {
        return map;
    }

    public void setMap(Map<String, Achievement> map) {
        this.map = map;
    }
}
