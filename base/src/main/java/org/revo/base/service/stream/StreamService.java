package org.revo.base.service.stream;

import org.revo.base.domain.Stream;

import java.util.List;
import java.util.Optional;

public interface StreamService {
    Stream save(Stream stream);

    Optional<Stream> findOneById(String id);

    long setSpsPps(String id, String sessionId, byte[] sps, byte[] pps);

    List<Stream> findAll();

    long setActive(String id, boolean b);
}
