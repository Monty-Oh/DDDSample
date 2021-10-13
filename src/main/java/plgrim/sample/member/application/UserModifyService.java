package plgrim.sample.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plgrim.sample.common.SHA256;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.controller.dto.user.UserModifyDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;
import plgrim.sample.member.domain.service.UserDomainService;
import plgrim.sample.member.domain.service.UserRepository;
import plgrim.sample.member.infrastructure.repository.UserJPARepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserModifyService {
    private final UserJPARepository userRepository;        // 리포지토리
    private final UserDomainService userDomainService;
    private final SHA256 sha256;                        // 암호화 객체


    /**
     * 회원정보 수정
     */
    public UserDTO modify(UserModifyDTO userModifyDTO) {
        Optional<User> user = userRepository.findById(userModifyDTO.getUsrNo());
        if (user.isEmpty()) throw new UserException(ErrorCode.MEMBER_NOT_FOUND);

        if(userDomainService.checkDuplicateEmail(userModifyDTO.getEmail(), userModifyDTO.getUsrNo()))
            throw new UserException(ErrorCode.DUPLICATE_ID);

//        if (userDomainService.checkDuplicateEmail(userModifyDTO.getEmail()))                      // 이미 있으면? 에러
//            throw new UserException(ErrorCode.DUPLICATE_ID);
//
//        if (userDomainService.checkDuplicatePhoneNumber(userModifyDTO.getPhoneNumber()))    // 만약 중복되는 phoneNumber가 있으면? 에러
//            throw new UserException(ErrorCode.DUPLICATE_PHONE_NUMBER);

        User userModify = User.builder()
                .usrNo(user.get().getUsrNo())
                .email(userModifyDTO.getEmail())
                .password(sha256.encrypt(userModifyDTO.getPassword()))          // 비밀번호 암호화
                .phoneNumber(userModifyDTO.getPhoneNumber())
                .userBasic(UserBasic.builder()
                        .address(userModifyDTO.getAddress())
                        .gender(userModifyDTO.getGender())
                        .birth(userModifyDTO.getBirth())
                        .snsType(userModifyDTO.getSnsType())
                        .build())
                .build();

        User result = userRepository.save(userModify);
        return UserDTO.builder()
                .usrNo(result.getUsrNo())
                .email(result.getEmail())
                .phoneNumber(result.getPhoneNumber())
                .userBasic(result.getUserBasic())
                .build();
    }

    /**
     * 회원 탈퇴
     * */
    public void delete(Long usrNo) {
        Optional<User> user = userRepository.findById(usrNo);   // user 조회 후 없으면? 못찾는 에러
        if (user.isEmpty()) throw new UserException(ErrorCode.MEMBER_NOT_FOUND);

        userRepository.deleteById(usrNo);
    }
}
