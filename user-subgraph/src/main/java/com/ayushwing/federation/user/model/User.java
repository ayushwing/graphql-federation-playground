package com.ayushwing.federation.user.model;

/**
 * User domain object. Owned by this subgraph — the canonical source of truth
 * for all user profile data across the federation.
 */
public class User {

    private String id;
    private String username;
    private String email;
    private String name;
    private String bio;
    private String avatarUrl;
    private String createdAt;

    public User() {}

    public User(String id, String username, String email, String name,
                String bio, String avatarUrl, String createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
