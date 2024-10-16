package com.ppproperti.simarco.entities;

import java.util.List;

public class LocationSchema {
    private int id;
    private String name;
    private int maxX;
    private int maxY;
    private List<LocationSchemaDetail> listLocationSchemaDetail;

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

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public List<LocationSchemaDetail> getListLocationSchemaDetail() {
        return listLocationSchemaDetail;
    }

    public void setListLocationSchemaDetail(List<LocationSchemaDetail> listLocationSchemaDetail) {
        this.listLocationSchemaDetail = listLocationSchemaDetail;
    }
}
