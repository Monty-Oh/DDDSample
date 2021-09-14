package plgrim.sample.member.controller.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plgrim.sample.common.exception.UserDuplicateIdException;
import plgrim.sample.common.exception.UserDuplicatePhoneNumberException;
import plgrim.sample.member.application.UserFindService;
import plgrim.sample.member.application.UserJoinService;
import plgrim.sample.member.application.UserModifyService;
import plgrim.sample.member.application.UserPasswordService;
import plgrim.sample.member.controller.dto.mapper.UserCommandMapper;
import plgrim.sample.member.controller.dto.user.UserJoinDTO;

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
        try {
            String userId = userJoinService.join(userCommandMapper.UserJoinMapper(userJoinDTO));
            return ResponseEntity.ok(userId);
        } catch (UserDuplicateIdException e) {
            return ResponseEntity.internalServerError()
                    .body("UserDuplicateIdException");
        } catch (UserDuplicatePhoneNumberException e) {
            return ResponseEntity.internalServerError()
                    .body("UserDuplicatePhoneNumberException");
        }
    }
}