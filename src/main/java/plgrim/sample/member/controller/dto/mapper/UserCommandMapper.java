package plgrim.sample.member.controller.dto.mapper;

import org.springframework.stereotype.Component;
import plgrim.sample.member.controller.dto.user.UserJoinDTO;
import plgrim.sample.member.controller.dto.user.UserModifyDTO;
import plgrim.sample.member.domain.model.commands.UserJoinCommand;
import plgrim.sample.member.domain.model.commands.UserModifyCommand;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;

@Component
public class UserCommandMapper {

    public UserJoinCommand toCommand(UserJoinDTO userJoinDTO) {
        return UserJoinCommand.builder()
                .email(userJoinDTO.getEmail())
                .password(userJoinDTO.getPassword())
                .phoneNumber(userJoinDTO.getPhoneNumber())
                .userBasic(UserBasic.builder()
                        .address(userJoinDTO.getAddress())
                        .gender(userJoinDTO.getGender())
                        .birth(userJoinDTO.getBirth())
                        .snsType(userJoinDTO.getSnsType())
                        .build())
                .build();
    }

    public UserModifyCommand toCommand(Long usrNo, UserModifyDTO userModifyDTO) {
        return UserModifyCommand.builder()
                .usrNo(usrNo)
                .email(userModifyDTO.getEmail())
                .password(userModifyDTO.getPassword())
                .phoneNumber(userModifyDTO.getPhoneNumber())
                .userBasic(UserBasic.builder()
                        .address(userModifyDTO.getAddress())
                        .gender(userModifyDTO.getGender())
                        .birth(userModifyDTO.getBirth())
                        .snsType(userModifyDTO.getSnsType())
                        .build())
                .build();
    }
}
