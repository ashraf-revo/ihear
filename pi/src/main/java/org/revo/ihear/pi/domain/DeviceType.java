package org.revo.ihear.pi.domain;

public enum DeviceType {
    Raspberry_4(4),
    Raspberry_3(3),
    Raspberry_0(0);

    DeviceType(Integer version) {
        this.version = version;
    }

    private Integer version;

    public Integer getVersion() {
        return version;
    }
}
