package com.work_service.work.domain.request;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class MemberSaveRequestDto {
    @NotNull(message = "아이디를 넣어주세요")
    private String userId;
    @NotNull(message = "패스워드를 넣어주세요")
    private String password;
    @NotNull(message = "이름을 넣어주세요")
    private String userName;
    @NotNull(message = "나이를 넣어주세요")
    private Integer age;
}
