package plgrim.sample.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import plgrim.sample.member.application.UserFindService;
import plgrim.sample.member.application.UserJoinService;
import plgrim.sample.member.application.UserModifyService;
import plgrim.sample.member.application.UserPasswordService;
import plgrim.sample.member.controller.dto.mapper.UserCommandMapper;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.controller.dto.user.UserFindByIdDTO;
import plgrim.sample.member.controller.dto.user.UserJoinDTO;
import plgrim.sample.member.controller.dto.user.UserModifyDTO;
import plgrim.sample.member.controller.validation.IdValidation;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/API/user")
public class UserController {
    private final UserFindService userFindService;
    private final UserJoinService userJoinService;
    private final UserModifyService userModifyService;
    private final UserPasswordService userPasswordService;

    private final UserCommandMapper userCommandMapper;      // 커맨드 변환 맵퍼

    /**
     * 회원 가입
     */
    @PostMapping("/join")
    public ResponseEntity<String> join(@Valid @RequestBody UserJoinDTO userJoinDTO) {
        String userId = userJoinService.join(userCommandMapper.UserJoinMapper(userJoinDTO));
        return ResponseEntity.ok(userId);
    }

    /**
     * 유저 조회
     * 굳이 dto 사용 안해도됨
     * @PathVariable 사용하기
     * */
    @GetMapping
    public ResponseEntity<UserDTO> findUserById(@Valid UserFindByIdDTO userFindByIdDTO) {
        UserDTO user = userFindService.findUserById(userFindByIdDTO);
        return ResponseEntity.ok(user);
    }

//    /**
//     * 회원 수정
//     * */
//    @PostMapping("modify")
//    public ResponseEntity<String> modify(@Valid @RequestBody UserModifyDTO userModifyDTO) {
//
//    }
}