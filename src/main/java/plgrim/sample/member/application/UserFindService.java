package plgrim.sample.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import plgrim.sample.common.enums.ErrorCode;
import plgrim.sample.common.exceptions.UserException;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.controller.dto.user.UserFindByIdDTO;
import plgrim.sample.member.domain.model.aggregates.User;
import plgrim.sample.member.domain.service.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserFindService {
    private final UserRepository userRepository;        // 리포지토리

    /**
     * 회원조회 - ID
     * 파라미터로 받은 ID를 사용해 사용자 정보를 조회.
     * UserDTO로 리턴한다.
     * */
    public UserDTO findUserById(UserFindByIdDTO userFindByIdDto) {
        Optional<User> result = userRepository.findById(userFindByIdDto.getId());
        System.out.println("result = " + result);
        if (result.isEmpty()) throw new UserException(ErrorCode.MEMBER_NOT_FOUND);  // user가 없으면 에러
        User user = result.get();
        return UserDTO.builder()
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .userBasic(user.getUserBasic())
                .build();
    }

    /**
     * 회원 목록 조회
     * 전부다 조회해서 List로 넘겨준다.
     * */
    public List<User> findUsers() {
        List<User> users = userRepository.findAll();
        if(users.isEmpty()) throw new UserException(ErrorCode.MEMBER_NOT_FOUND);
        return users;
    }

    /**
     * 회원 목록 조회
     * Page 객체를 리턴
     * */
    public List<User> findUsers(int page, int size) {
        List<User> users = userRepository.findAll(PageRequest.of(page, size)).getContent();
        if(users.isEmpty()) throw new UserException(ErrorCode.MEMBER_NOT_FOUND);
        return users;
    }
}
