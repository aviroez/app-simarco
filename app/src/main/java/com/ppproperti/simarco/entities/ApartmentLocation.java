package com.ppproperti.simarco.entities;

public class ApartmentLocation {
    private int id;
    private String name;
    private int locationSchemaId;
    private LocationSchema locationSchema;

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

    public int getLocationSchemaId() {
        return locationSchemaId;
    }

    public void setLocationSchemaId(int locationSchemaId) {
        this.locationSchemaId = locationSchemaId;
    }

    public LocationSchema getLocationSchema() {
        return locationSchema;
    }

    public void setLocationSchema(LocationSchema locationSchema) {
        this.locationSchema = locationSchema;
    }
}
