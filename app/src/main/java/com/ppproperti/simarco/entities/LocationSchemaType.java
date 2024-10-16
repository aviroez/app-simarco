package com.ppproperti.simarco.entities;

public class LocationSchemaType {
    private int id;
    private String name;
    private String color;
    private String desc;
    private boolean enabled;

    public LocationSchemaType() {
    }

    public LocationSchemaType(int id, String name, String color, String desc, boolean enabled) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.desc = desc;
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
