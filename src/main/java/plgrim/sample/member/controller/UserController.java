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

import static plgrim.sample.common.UrlValue.USRNO_PATH;
import static plgrim.sample.common.UrlValue.ROOT_USER_PATH;

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
     * 유저 조회 - usrNo
     */
    @GetMapping(USRNO_PATH)
    public ResponseEntity<UserDTO> findUserByUsrNo(@PathVariable("usrNo") Long usrNo) {
        UserDTO user = userFindService.findUserByUsrNo(usrNo);
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
     * 유저 수정 usrNo 필수
     */
    @PutMapping(USRNO_PATH)
    public ResponseEntity<UserDTO> modify(@PathVariable("usrNo") Long usrNo, @Valid @RequestBody UserModifyDTO userModifyDTO) {
        UserDTO userDTO = userModifyService.modify(userCommandMapper.toCommand(usrNo, userModifyDTO));
        return ResponseEntity.ok(userDTO);
    }

    /**
     * 유저 삭제 usrNo 필수
     */
    @DeleteMapping(USRNO_PATH)
    public ResponseEntity<String> delete(@PathVariable("usrNo") Long usrNo) {
        userModifyService.delete(usrNo);
        return ResponseEntity.ok(SuccessCode.DELETE_SUCCESS.getDetail());
    }
}
