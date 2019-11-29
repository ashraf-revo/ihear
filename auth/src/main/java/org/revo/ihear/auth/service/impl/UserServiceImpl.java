package org.revo.ihear.auth.service.impl;

import org.revo.base.domain.User;

import org.revo.ihear.auth.repository.UserRepository;
import org.revo.ihear.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public List<User> findByCreatedBy(String id) {
        return userRepository.findByCreatedBy(id);
    }

    @Override
    public void changeEnabled(String father, String child) {
        long modifiedCount = mongoOperations.updateFirst(new Query(Criteria.where("createdBy").is(father).and("id").is(child).and("enabled").is(false)), new Update().set("enabled", true), User.class).getModifiedCount();
        if (modifiedCount == 0)
            mongoOperations.updateFirst(new Query(Criteria.where("createdBy").is(father).and("id").is(child).and("enabled").is(true)), new Update().set("enabled", false), User.class);
    }

    @Override
    public void delete(String father, String child) {
        userRepository.deleteByCreatedByAndId(father, child);
    }
}
