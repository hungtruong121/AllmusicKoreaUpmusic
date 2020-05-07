package com.upmusic.web.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upmusic.web.helper.AzureHelper;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class MainBanner {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "메인 배너 ID")
    private Long mainBannerId;

    @Column(nullable = false)
    @ApiModelProperty(notes = "배너타입 (이벤트 / 아티스트)")
    private int bannerType;

    @Column(length=50)
    @ApiModelProperty(notes = "파일명")
    private String filename;

    @Column(nullable = false, length = 200)
    @ApiModelProperty(notes = "링크")
    private String link;

    @Column(nullable = false)
    @ApiModelProperty(notes = "순서")
    private Integer orderNo;

    @Column(nullable = false, length = 1)
    @ApiModelProperty(notes = "노출 여부")
    private boolean shown;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @ApiModelProperty(notes = "생성 일시")
    @JsonIgnore
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @ApiModelProperty(notes = "수정 일시")
    @JsonIgnore
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @ApiModelProperty(notes = "회원 ID")
    private Member member;

    @Transient
    @JsonIgnore
    private MultipartFile thumbnailImg;

    public String getAdminEditURL() {
        return "/admin/main/banner/detail?mainBannerId=" + mainBannerId;
    }

    public String getAdminDeleteUrl() {
        return "/admin/main/banner/delete?mainBannerId=" + mainBannerId;
    }

    public String getImgUrl() {
        return String.format("%s/main_banner/%d/%s", AzureHelper.getStorageResourceUrl(), mainBannerId, filename);
    }
}

