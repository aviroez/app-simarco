package com.ppproperti.simarco.entities;

public class LocationSchemaDetail {
    private int id;
    private String name;
    private LocationSchema locationSchema;
    private LocationSchemaType locationSchemaType;
    private int x;
    private int y;

    public LocationSchemaDetail() {
    }

    public LocationSchemaDetail(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public LocationSchemaDetail(int id, int x, int y, String name, LocationSchema locationSchema, LocationSchemaType locationSchemaType) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.name = name;
        this.locationSchema = locationSchema;
        this.locationSchemaType = locationSchemaType;
    }

    public LocationSchemaDetail(int id, int x, int y, LocationSchema locationSchema, LocationSchemaType locationSchemaType) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.locationSchema = locationSchema;
        this.locationSchemaType = locationSchemaType;
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

    public LocationSchema getLocationSchema() {
        return locationSchema;
    }

    public void setLocationSchema(LocationSchema locationSchema) {
        this.locationSchema = locationSchema;
    }

    public LocationSchemaType getLocationSchemaType() {
        return locationSchemaType;
    }

    public void setLocationSchemaType(LocationSchemaType locationSchemaType) {
        this.locationSchemaType = locationSchemaType;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
