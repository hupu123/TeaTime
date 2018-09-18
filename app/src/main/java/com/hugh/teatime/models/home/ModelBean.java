package com.hugh.teatime.models.home;

import android.content.Intent;

public class ModelBean {

    private int position;
    private int icon;
    private String name;
    private String description;
    private Intent targetIntent;

    public ModelBean() {
    }

    public ModelBean(int position, int icon, String name, String description, Intent targetIntent) {
        this.position = position;
        this.icon = icon;
        this.name = name;
        this.description = description;
        this.targetIntent = targetIntent;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Intent getTargetIntent() {
        return targetIntent;
    }

    public void setTargetIntent(Intent targetIntent) {
        this.targetIntent = targetIntent;
    }
}
