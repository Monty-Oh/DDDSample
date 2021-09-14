package plgrim.sample.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import plgrim.sample.common.exception.UserNotFoundException;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.controller.dto.user.UserFindByIdDTO;
import plgrim.sample.member.domain.model.entity.User;
import plgrim.sample.member.infrastructure.repository.UserRepository;

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
        Optional<User> user = userRepository.findById(userFindByIdDto.getId());

        if (user.isEmpty()) throw new UserNotFoundException();  // user가 없으면 에러
        return new UserDTO(user.get());
    }

    /**
     * 회원 목록 조회
     * 전부다 조회해서 List로 넘겨준다.
     * */
    public List<User> findUsers() {
        List<User> users = userRepository.findAll();
        if(users.size() == 0) throw new UserNotFoundException();

        return users;
    }
}
