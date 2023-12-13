package com.appsdeveloperblog.tutorials.junit.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.UUID;

@DataJpaTest
public class UsersRepositoryIntegrationTest {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    TestEntityManager testEntityManager;

    private final String userId = UUID.randomUUID().toString();
    UserEntity userEntity;


    @BeforeEach
    void setup(){
        userEntity = new UserEntity();
        userEntity.setUserId(userId);
        userEntity.setFirstName("Nirajan");
        userEntity.setLastName("Karki");
        userEntity.setEmail("karki@gmail.com");
        userEntity.setEncryptedPassword("test1");
        testEntityManager.persistAndFlush(userEntity);
    }

    @Test
    void testFindByEmail_WhenEmailProvided_returnUserData(){
        // arrange


        // act
        UserEntity byEmail = usersRepository.findByEmail(userEntity.getEmail());

        // assert
        Assertions.assertEquals(userEntity.getEmail(), byEmail.getEmail());
    }

    @Test
    void testFindByUserId_whenUserIdProvided_returnUserWithId(){

        // act
        UserEntity byUserId = usersRepository.findByUserId(userId);

        //assert
        Assertions.assertNotNull(byUserId);
        Assertions.assertEquals(userId, byUserId.getUserId());
    }

    @Test
    void findUserWithEmailLikeEmailDomain_whenEmailDomainProvided_returnSameEmail(){
        String emailDomain = "@gmail.com";

        //act
        List<UserEntity> usersByEmailEndingWith = usersRepository.findUsersByEmailEndingWith(emailDomain);

        //asert
        Assertions.assertEquals(1,usersByEmailEndingWith.size());
        Assertions.assertTrue(usersByEmailEndingWith.get(0).getEmail().endsWith(emailDomain));
    }
}
