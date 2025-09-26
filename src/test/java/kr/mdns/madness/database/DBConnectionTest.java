package kr.mdns.madness.database;

import jakarta.persistence.Query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import jakarta.persistence.EntityManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=postgresql://postgres.lxjbwzycavcgjfahyngk:sm15883082@aws-0-ap-northeast-2.pooler.supabase.com:5432/postgres",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect",
        "spring.test.database.replace=NONE"
})
public class DBConnectionTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testConnection() {
        Query query = entityManager.createNativeQuery("SELECT 1");
        Object result = query.getSingleResult();
        assertEquals(1, ((Number) result).intValue());
    }

}
