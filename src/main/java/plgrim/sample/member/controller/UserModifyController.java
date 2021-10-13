package plgrim.sample.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import plgrim.sample.member.application.UserModifyService;
import plgrim.sample.member.controller.dto.user.UserDTO;
import plgrim.sample.member.controller.dto.user.UserModifyDTO;

import javax.validation.Valid;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/APIs")
public class UserModifyController {
    private final UserModifyService userModifyService;

    /**
     * 유저 수정 usrNo 필수
     * */
    @PostMapping("/user")
    public ResponseEntity<UserDTO> modify(@Valid @RequestBody UserModifyDTO userModifyDTO) {
        UserDTO userDTO = userModifyService.modify(userModifyDTO);
        return ResponseEntity.ok(userDTO);
    }

    /**
     * 유저 삭제 usrNo 필수
     * */
    @DeleteMapping("/user")
    public void delete(@RequestParam String usrNo) {
    }
}
