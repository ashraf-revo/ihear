package org.revo.ihear.pi.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Device {
    private String id;
    private String name;
    private DeviceType deviceType;
    private String createdBy;
    @CreatedDate
    private String createdDate;
    private String schemaId;
    private String clientId;
    @Transient
    private String clientSecret;

    public String getId() {
        return id;
    }

    public Device setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Device setName(String name) {
        this.name = name;
        return this;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public Device setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
        return this;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Device setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public Device setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String getClientId() {
        return clientId;
    }

    public Device setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public Device setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }
}
