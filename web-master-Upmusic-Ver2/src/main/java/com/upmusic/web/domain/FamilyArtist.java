package com.upmusic.web.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upmusic.web.config.UPMusicConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Transient;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyArtist {

    //Member 정보
    private String email;

    private String nick;

    private String password;

    private String profileImage;

    private String birthday;

    private String genderNo;

    private String phoneNumber;

    private MultipartFile profileImageMultipart;

    @Transient
    @Getter
    @JsonIgnore
    private String passwordConfirm;

    //Video 정보
    private String videoService;

    private String videoServiceObjectId;

    private int duration;

    private String thumbnail;

    private UPMusicConstants.VideoType videoType;

    private String subject;

    private String description;
}
