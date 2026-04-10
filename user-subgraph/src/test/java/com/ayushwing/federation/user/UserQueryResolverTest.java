package com.ayushwing.federation.user;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.test.EnableDgsTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableDgsTest
class UserQueryResolverTest {

    @Autowired
    DgsQueryExecutor dgsQueryExecutor;

    @Test
    void userQueryReturnsSeedUser() {
        String name = dgsQueryExecutor.executeAndExtractJsonPath(
                "{ user(id: \"u1\") { name } }",
                "data.user.name"
        );
        assertThat(name).isEqualTo("Alice Chen");
    }

    @Test
    void usersQueryReturnsAll() {
        List<String> ids = dgsQueryExecutor.executeAndExtractJsonPath(
                "{ users { id } }",
                "data.users[*].id"
        );
        assertThat(ids).hasSize(4).contains("u1", "u2", "u3", "u4");
    }

    @Test
    void userQueryReturnsNullForUnknownId() {
        Object result = dgsQueryExecutor.executeAndExtractJsonPath(
                "{ user(id: \"unknown\") { id } }",
                "data.user"
        );
        assertThat(result).isNull();
    }

    @Test
    void meQueryReturnsStubbedUser() {
        String username = dgsQueryExecutor.executeAndExtractJsonPath(
                "{ me { username } }",
                "data.me.username"
        );
        assertThat(username).isEqualTo("alice");
    }

    @Test
    void userSchemaExposesFederationService() {
        // DGS adds _service { sdl } for federation compliance
        String sdl = dgsQueryExecutor.executeAndExtractJsonPath(
                "{ _service { sdl } }",
                "data._service.sdl"
        );
        assertThat(sdl).contains("User").contains("@key");
    }
}
