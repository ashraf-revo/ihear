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
    private AudioContent audioContent;
    private VideoContent videoContent;
    private String sdp;
    private StreamType streamType;

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

    public AudioContent getAudioContent() {
        return audioContent;
    }

    public void setAudioContent(AudioContent audioContent) {
        this.audioContent = audioContent;
    }

    public VideoContent getVideoContent() {
        return videoContent;
    }

    public void setVideoContent(VideoContent videoContent) {
        this.videoContent = videoContent;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public StreamType getStreamType() {
        return streamType;
    }

    public void setStreamType(StreamType streamType) {
        this.streamType = streamType;
    }
}
