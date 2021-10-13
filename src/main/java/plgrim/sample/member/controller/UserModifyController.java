package plgrim.sample.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plgrim.sample.member.application.UserModifyService;
import plgrim.sample.member.controller.dto.user.UserModifyDTO;

import javax.validation.Valid;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/API")
public class UserModifyController {
    private final UserModifyService userModifyService;

    /**
     * 유저 수정 usrNo 필수
     * */
    @PutMapping("/user")
    public void modify(@Valid @RequestBody UserModifyDTO userModifyDTO) {
        userModifyService.modify(userModifyDTO);
    }
}
