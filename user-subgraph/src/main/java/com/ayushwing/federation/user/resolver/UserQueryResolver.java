package com.ayushwing.federation.user.resolver;

import com.ayushwing.federation.user.model.User;
import com.ayushwing.federation.user.repository.UserRepository;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * DGS query resolvers for the User type.
 *
 * <p>Handles the {@code user}, {@code users}, and {@code me} queries.
 * The {@code me} query is a stub — in a real service it would inspect
 * the JWT from context to resolve the authenticated caller.
 */
@DgsComponent
public class UserQueryResolver {

    private static final Logger log = LoggerFactory.getLogger(UserQueryResolver.class);

    private final UserRepository userRepository;

    public UserQueryResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @DgsQuery
    public User user(@InputArgument String id) {
        log.debug("Resolving user(id={})", id);
        return userRepository.findById(id).orElse(null);
    }

    @DgsQuery
    public List<User> users() {
        log.debug("Resolving users()");
        return userRepository.findAll();
    }

    @DgsQuery
    public User me() {
        // TODO: resolve from JWT / auth context
        // Stubbed to u1 for demo purposes
        return userRepository.findById("u1").orElse(null);
    }
}
