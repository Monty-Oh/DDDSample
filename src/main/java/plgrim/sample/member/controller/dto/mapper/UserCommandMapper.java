package plgrim.sample.member.controller.dto.mapper;

import org.springframework.stereotype.Component;
import plgrim.sample.member.controller.dto.user.UserJoinDTO;
import plgrim.sample.member.controller.dto.user.UserModifyDTO;
import plgrim.sample.member.domain.model.commands.UserJoinCommand;
import plgrim.sample.member.domain.model.commands.UserModifyCommand;
import plgrim.sample.member.domain.model.entities.SnsInfo;
import plgrim.sample.member.domain.model.valueobjects.UserBasic;

@Component
public class UserCommandMapper {

    public UserJoinCommand toCommand(UserJoinDTO userJoinDTO) {
        return UserJoinCommand.builder()
                .userId(userJoinDTO.getUserId())
                .email(userJoinDTO.getEmail())
                .password(userJoinDTO.getPassword())
                .nickName(userJoinDTO.getNickName())
                .mobileNo(userJoinDTO.getMobileNo())
                .snsType(userJoinDTO.getSnsType())
                .snsInfo(SnsInfo.builder()
                        .refreshToken(userJoinDTO.getRefreshToken())
                        .build())
                .userBasic(UserBasic.builder()
                        .address(userJoinDTO.getAddress())
                        .gender(userJoinDTO.getGender())
                        .birth(userJoinDTO.getBirth())
                        .build())
                .build();
    }

    public UserModifyCommand toCommand(String userId, UserModifyDTO userModifyDTO) {
        return UserModifyCommand.builder()
                .userId(userId)
                .email(userModifyDTO.getEmail())
                .password(userModifyDTO.getPassword())
                .nickName(userModifyDTO.getNickName())
                .snsInfo(SnsInfo.builder()
                        .refreshToken(userModifyDTO.getRefreshToken())
                        .build())
                .userBasic(UserBasic.builder()
                        .address(userModifyDTO.getAddress())
                        .gender(userModifyDTO.getGender())
                        .birth(userModifyDTO.getBirth())
                        .build())
                .build();
    }
}
