package org.revo.base.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class Stream {
    @Id
    private String id;
    private String sessionId;
    @CreatedDate
    private Date createdDate;
    @LastModifiedDate
    private Date lastModifiedDate;
    private String createBy;
    private byte[] sps;
    private byte[] pps;
    private boolean active;

    public String getId() {
        return id;
    }

    public Stream setId(String id) {
        this.id = id;
        return this;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Stream setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Stream setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public Stream setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    public byte[] getSps() {
        return sps;
    }

    public Stream setSps(byte[] sps) {
        this.sps = sps;
        return this;
    }

    public byte[] getPps() {
        return pps;
    }

    public Stream setPps(byte[] pps) {
        this.pps = pps;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public Stream setActive(boolean active) {
        this.active = active;
        return this;
    }
}
