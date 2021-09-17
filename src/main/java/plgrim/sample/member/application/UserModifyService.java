package plgrim.sample.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import plgrim.sample.common.SHA256;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.controller.dto.user.UserModifyDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;
import plgrim.sample.member.domain.service.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserModifyService {
    private final UserRepository userRepository;        // 리포지토리
    private final SHA256 sha256;                        // 암호화 객체


    /**
     * 회원정보 수정
     */
    public String modify(UserModifyDTO userModifyDTO) {
        Optional<User> user = userRepository.findById(userModifyDTO.getId());   // user 조회 후 없으면? 못찾는 에러
        if (user.isEmpty()) throw new UserException(ErrorCode.MEMBER_NOT_FOUND);

        User userModify = User.builder()
                .id(userModifyDTO.getId())
                .password(sha256.encrypt(userModifyDTO.getPassword()))          // 비밀번호 암호화
                .phoneNumber(userModifyDTO.getPhoneNumber())
                .userBasic(UserBasic.builder()
                        .address(userModifyDTO.getAddress())
                        .gender(userModifyDTO.getGender())
                        .birth(userModifyDTO.getBirth())
                        .snsType(userModifyDTO.getSnsType())
                        .build())
                .build();                                                       // 조회 결과가 있다면 수정한다.

        userRepository.save(userModify);
        return userModify.getId();
    }
}
