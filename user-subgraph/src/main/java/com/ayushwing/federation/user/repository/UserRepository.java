package com.ayushwing.federation.user.repository;

import com.ayushwing.federation.user.model.User;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory user store. Seeded with a handful of demo users at startup.
 *
 * <p>In a real service this would delegate to JPA / R2DBC, but keeping it
 * in-memory keeps the demo self-contained and dependency-free.
 */
@Repository
public class UserRepository {

    private final Map<String, User> store = new ConcurrentHashMap<>();

    public UserRepository() {
        seed("u1", "alice", "alice@example.com", "Alice Chen",
                "Senior engineer. Coffee enthusiast. Open-source contributor.",
                "https://avatars.githubusercontent.com/u/1", "2024-01-10T08:00:00Z");
        seed("u2", "bob", "bob@example.com", "Bob Nguyen",
                "Backend dev. Kafka wrangler. Terrible at frontend.",
                "https://avatars.githubusercontent.com/u/2", "2024-02-14T10:30:00Z");
        seed("u3", "carol", "carol@example.com", "Carol Okafor",
                "Platform engineer. Kubernetes native. GraphQL advocate.",
                "https://avatars.githubusercontent.com/u/3", "2024-03-01T09:15:00Z");
        seed("u4", "dave", "dave@example.com", "Dave Singh",
                "Full-stack. Loves clean APIs and spicy food.",
                null, "2024-04-20T14:00:00Z");
    }

    private void seed(String id, String username, String email, String name,
                      String bio, String avatarUrl, String createdAt) {
        store.put(id, new User(id, username, email, name, bio, avatarUrl, createdAt));
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId("u" + (store.size() + 1));
        }
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(Instant.now().toString());
        }
        store.put(user.getId(), user);
        return user;
    }
}
