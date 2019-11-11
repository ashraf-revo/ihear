package org.revo.ihear.livepoll.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "ui", url = "http://localhost:8080")
public interface UiService {
    @GetMapping("/auth/user")
    String user(@RequestHeader("cookie") String cookie);
}
