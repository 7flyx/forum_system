package com.fly.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
@Data
@ApiModel("用户信息")
public class User {
    private Long id;
    @ApiModelProperty("用户名")
    private String username;

    @JsonIgnore // 不参与json序列化
    private String password;

    @ApiModelProperty("昵称")
    private String nickname;
    @ApiModelProperty("电话")
    private String phoneNum;
    @ApiModelProperty("邮箱")
    private String email;

    private Byte gender;
    @JsonIgnore
    private String salt;
    @ApiModelProperty("头像地址")
    @JsonInclude(JsonInclude.Include.ALWAYS) // 强制参与json序列化
    private String avatarUrl;
    @ApiModelProperty("发帖数量")
    private Integer articleCount;

    private Byte isAdmin;
    @ApiModelProperty("个人简介")
    private String remark;

    private Byte state;
    @JsonIgnore
    private Byte deleteState;
    @ApiModelProperty("注册日期")
    private Date createTime;

    private Date updateTime;
}