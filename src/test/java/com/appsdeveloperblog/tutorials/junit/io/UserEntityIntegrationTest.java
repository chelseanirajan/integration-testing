package com.appsdeveloperblog.tutorials.junit.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.util.UUID;

@DataJpaTest
public class UserEntityIntegrationTest {
    @Autowired
    TestEntityManager entityManager;

    UserEntity userEntity;

    @BeforeEach
    void setup(){
         userEntity = new UserEntity();
        userEntity.setUserId(UUID.randomUUID().toString());
        userEntity.setFirstName("Nirajan");
        userEntity.setLastName("Karki");
        userEntity.setEmail("nirajan@gmail.com");
        userEntity.setEncryptedPassword("nirajan@123");
    }

    @Test
    void testUserEntity_whenUserCreated_returnUserDetails(){
        //arrange

        //act
        UserEntity user = entityManager.persistAndFlush(userEntity);

        //assert

        Assertions.assertTrue(user.getId()>0);
        Assertions.assertEquals(userEntity.getUserId(),user.getUserId());
        Assertions.assertEquals(userEntity.getFirstName(),user.getFirstName());
        Assertions.assertEquals(userEntity.getLastName(),user.getLastName());
        Assertions.assertEquals(userEntity.getEmail(),user.getEmail());
        Assertions.assertEquals(userEntity.getEncryptedPassword(),user.getEncryptedPassword());
    }
    @Test
    @DisplayName("Test User name too long")
    void testUserEntity_whenUsernameTooLong_returnPersistenceEx(){
        //arrange
        userEntity.setFirstName("adjfkl fn;u9ahtfdnasinmkndfjnfiasnl ;kndiuainfdkkanfiadnjnfd");

        //act and assert
        Assertions.assertThrows(PersistenceException.class, ()-> {
            entityManager.persistAndFlush(userEntity);
        },"Username character is too long");

    }
    @Test
    @DisplayName("Test unique user ID")
    void testUserEntity_whenUserIDIsSame_returnPersistenceEx(){
        UserEntity newUser= new UserEntity();
        newUser.setUserId("1");
        newUser.setFirstName("test12345");
        newUser.setLastName("test1234");
        newUser.setEmail("test@gmail.com");
        newUser.setEncryptedPassword("test");

        entityManager.persistAndFlush(newUser);

        userEntity.setUserId("1");
        //act
        Assertions.assertThrows(PersistenceException.class, () -> entityManager.persistAndFlush(userEntity));
    }
}
