package plgrim.sample.member;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.infrastructure.repository.UserRepository;
import plgrim.sample.member.infrastructure.repository.UserRepositorySupport;

import java.util.List;

@SpringBootTest
public class Data {
    @Autowired
    private UserRepositorySupport userRepositorySupport;
    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    UserJoinService userJoinService;
//
//    UserJoinCommand userJoinCommand = UserJoinCommand.builder()
//            .email("monty@plgrim.com")
//            .password("testtest")
//            .mobileNo("01040684490")
//            .userBasic(UserBasic.builder()
//                    .address("동대문구")
//                    .gender(Gender.MALE)
//                    .birth(LocalDate.of(1994, 3, 30))
//                    .snsType(Sns.LOCAL)
//                    .build())
//            .build();
//
//    @Test
//    void setupData() {
//        userJoinService.join(userJoinCommand);
//    }

    @Test
    void testQuery() {
        userRepository.save(User.builder().email("monty@plgrim.com").build());
        List<User> byEmail = userRepositorySupport.findByEmail("monty@plgrim.com");

        System.out.println("byEmail = " + byEmail);
    }
}
