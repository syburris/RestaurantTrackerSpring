package com.theironyard;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zach on 6/21/16.
 */
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByName(String username);
}
