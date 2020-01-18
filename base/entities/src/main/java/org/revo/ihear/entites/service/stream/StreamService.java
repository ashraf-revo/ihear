package org.revo.ihear.entites.service.stream;

import org.revo.ihear.entites.domain.Stream;
import org.revo.ihear.entites.domain.StreamType;

import java.util.List;
import java.util.Optional;

public interface StreamService {
    Stream save(Stream stream);

    Optional<Stream> findOneById(String id);

    long countBySchemaId(String id);

    long setSps(String id, byte[] sps);

    long setPps(String id, byte[] pps);

    List<Stream> findAll(String id);

    long setSdp(String id, String sdp, StreamType streamType);

}
