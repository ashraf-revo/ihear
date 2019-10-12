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
    @CreatedDate
    private Date createdDate;
    @LastModifiedDate
    private Date lastModifiedDate;
    private String createBy;
    private String title;
    private String meta;
    // type=5
    private byte[] idr;
    // type=6
    private byte[] sei;
    // type=7
    private byte[] sps;
    // type=8
    private byte[] pps;
    private boolean active;

    public String getId() {
        return id;
    }

    public Stream setId(String id) {
        this.id = id;
        return this;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Stream setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public Stream setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
        return this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public Stream setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Stream setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getMeta() {
        return meta;
    }

    public Stream setMeta(String meta) {
        this.meta = meta;
        return this;
    }

    public byte[] getIdr() {
        return idr;
    }

    public Stream setIdr(byte[] idr) {
        this.idr = idr;
        return this;
    }

    public byte[] getSei() {
        return sei;
    }

    public Stream setSei(byte[] sei) {
        this.sei = sei;
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
