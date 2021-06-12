package com.example.final_exercise.model;

import com.github.mikephil.charting.data.Entry;

import java.io.Serializable;
import java.util.Comparator;

public class Mission implements Serializable, Comparable<Mission> {
    private String title, date, description, key, label;
    private int level;
    private boolean isDone;
    private long timeInMills;

    public Mission() {
    }

    public long getTimeInMills() {
        return timeInMills;
    }

    public void setTimeInMills(long timeInMills) {
        this.timeInMills = timeInMills;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public int compareTo(Mission o) {
        if (o.getLevel() == this.getLevel()) {
            return 0;
        }
        return (o.getLevel() > this.getLevel()) ? 1 : -1;
    }
}
