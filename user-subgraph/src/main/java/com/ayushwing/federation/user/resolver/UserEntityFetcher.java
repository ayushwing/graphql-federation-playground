package com.ayushwing.federation.user.resolver;

import com.ayushwing.federation.user.model.User;
import com.ayushwing.federation.user.repository.UserRepository;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsEntityFetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Federation entity fetcher for the {@code User} type.
 *
 * <p>Apollo Router calls the {@code _entities} query when another subgraph
 * (e.g. Product, Review) references a User by its {@code @key} field. DGS
 * routes those calls here via the {@link DgsEntityFetcher} annotation.
 *
 * <p>The {@code values} map contains the key fields declared in the schema's
 * {@code @key(fields: "id")} directive — in this case just {@code "id"}.
 */
@DgsComponent
public class UserEntityFetcher {

    private static final Logger log = LoggerFactory.getLogger(UserEntityFetcher.class);

    private final UserRepository userRepository;

    public UserEntityFetcher(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Resolves a User entity by its {@code id} key.
     *
     * <p>Called by the Apollo Router when it needs to join User data from
     * this subgraph into a response that originated in another subgraph.
     *
     * @param values key fields map — contains at minimum {@code "id"}
     * @return the User, or {@code null} if not found
     */
    @DgsEntityFetcher(name = "User")
    public User fetchUserById(Map<String, Object> values) {
        String id = (String) values.get("id");
        log.debug("Federation entity fetch: User(id={})", id);
        return userRepository.findById(id).orElse(null);
    }
}
