package plgrim.sample.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import plgrim.sample.common.enums.SuccessCode;
import plgrim.sample.member.application.UserFindService;
import plgrim.sample.member.application.UserJoinService;
import plgrim.sample.member.application.UserModifyService;
import plgrim.sample.member.controller.dto.mapper.UserCommandMapper;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.controller.dto.user.UserJoinDTO;
import plgrim.sample.member.controller.dto.user.UserModifyDTO;
import plgrim.sample.member.domain.model.aggregates.User;

import javax.validation.Valid;
import java.util.List;

import static plgrim.sample.common.UrlValue.ROOT_USER_PATH;
import static plgrim.sample.common.UrlValue.USER_ID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(ROOT_USER_PATH)
public class UserController {
    private final UserFindService userFindService;
    private final UserJoinService userJoinService;
    private final UserCommandMapper userCommandMapper;      // 커맨드 변환 맵퍼
    private final UserModifyService userModifyService;

    /**
     * 유저 조회 - userId
     */
    @GetMapping(USER_ID)
    public ResponseEntity<UserDTO> findUserByUsrNo(@PathVariable("userId") String userId,
                                                   @RequestParam("snsType") String snsType) {
        UserDTO user = userFindService.findUserByUserIdAndSnsType(userId, snsType);
        return ResponseEntity.ok(user);
    }

    /**
     * 유저 목록 조회 page, size
     **/
    @GetMapping
    public ResponseEntity<List<User>> findUserList(@RequestParam("page") int page,
                                                   @RequestParam("size") int size) {
        List<User> users = userFindService.findUsers(page, size);
        return ResponseEntity.ok(users);
    }

    /**
     * 회원 가입
     */
    @PostMapping
    public ResponseEntity<UserDTO> join(@Valid @RequestBody UserJoinDTO userJoinDTO) {
        UserDTO userDTO = userJoinService.join(userCommandMapper.toCommand(userJoinDTO));
        return ResponseEntity.ok(userDTO);
    }

    /**
     * 유저 수정 userId 필수
     */
    @PutMapping(USER_ID)
    public ResponseEntity<UserDTO> modify(@PathVariable("userId") String userId,
                                          @Valid @RequestBody UserModifyDTO userModifyDTO) {
        UserDTO userDTO = userModifyService.modify(userCommandMapper.toCommand(userId, userModifyDTO));
        return ResponseEntity.ok(userDTO);
    }

    /**
     * 유저 삭제 USER_ID 필수
     */
    @DeleteMapping(USER_ID)
    public ResponseEntity<String> delete(@PathVariable("userId") String userId,
                                         @RequestParam("snsType") String snsType) {
        userModifyService.delete(userId, snsType);
        return ResponseEntity.ok(SuccessCode.DELETE_SUCCESS.getDetail());
    }
}
