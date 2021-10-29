//package plgrim.sample.member;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import plgrim.sample.common.enums.Gender;
//import plgrim.sample.common.enums.Sns;
//import plgrim.sample.member.application.UserJoinService;
//import plgrim.sample.member.domain.model.commands.UserJoinCommand;
//import plgrim.sample.member.domain.model.valueobjects.UserBasic;
//
//import java.time.LocalDate;
//
//@SpringBootTest
//public class Data {
//    @Autowired
//    UserJoinService userJoinService;
//
//    UserJoinCommand userJoinCommand = UserJoinCommand.builder()
//            .email("monty@plgrim.com")
//            .password("testtest")
//            .phoneNumber("01040684490")
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
//}
