package plgrim.sample.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plgrim.sample.member.application.UserFindService;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.controller.validation.EmailValidation;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/API")
public class UserFindController {
    private final UserFindService userFindService;

    /**
     * 유저 조회
     * 굳이 dto 사용 안해도됨
     * @PathVariable 사용하기 - 수정 완료
     * */
    @GetMapping("/user/{email}")
    public ResponseEntity<UserDTO> findUserByEmail(@EmailValidation @PathVariable String email) {
        UserDTO user = userFindService.findUserByEmail(email);
        return ResponseEntity.ok(user);
    }
}
