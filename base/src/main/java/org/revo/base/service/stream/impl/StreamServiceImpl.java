package org.revo.base.service.stream.impl;

import org.revo.base.domain.Stream;
import org.revo.base.domain.StreamType;
import org.revo.base.repository.stream.StreamRepository;
import org.revo.base.service.stream.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class StreamServiceImpl implements StreamService {
    @Autowired
    private StreamRepository streamRepository;
    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public Stream save(Stream stream) {
        return streamRepository.save(stream);
    }

    @Override
    public Optional<Stream> findOneById(String id) {
        return streamRepository.findById(id);
    }

    @Override
    public long setSpsPps(String id, byte[] sps, byte[] pps) {
        return mongoOperations.updateFirst(new Query().addCriteria(where("id").is(id)), new Update().set("videoContent.sps", sps).set("videoContent.pps", pps), Stream.class).getModifiedCount();
    }

    @Override
    public List<Stream> findAll(String id) {
        return streamRepository.findAllByCreateBy(id);
    }

    @Override
    public long setSdp(String id, String sdp, StreamType streamType) {
        return mongoOperations.updateFirst(new Query().addCriteria(where("id").is(id)), new Update().set("sdp", sdp).set("streamType", streamType), Stream.class).getModifiedCount();
    }

    @Override
    public long setIdr(String id, byte[] idr) {
        return mongoOperations.updateFirst(new Query().addCriteria(where("id").is(id)), new Update().set("videoContent.idr", idr), Stream.class).getModifiedCount();
    }

    @Override
    public long setSei(String id, byte[] sei) {
        return mongoOperations.updateFirst(new Query().addCriteria(where("id").is(id)), new Update().set("videoContent.sei", sei), Stream.class).getModifiedCount();
    }

    @Override
    public long setSps(String id, byte[] sps) {
        return mongoOperations.updateFirst(new Query().addCriteria(where("id").is(id)), new Update().set("videoContent.sps", sps), Stream.class).getModifiedCount();
    }

    @Override
    public long setPps(String id, byte[] pps) {
        return mongoOperations.updateFirst(new Query().addCriteria(where("id").is(id)), new Update().set("videoContent.pps", pps), Stream.class).getModifiedCount();
    }
}
