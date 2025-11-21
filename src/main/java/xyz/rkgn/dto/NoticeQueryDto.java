package xyz.rkgn.dto;

import lombok.Data;

@Data
public class NoticeQueryDto {
    private Long id;
    private String title;
    private Integer isPublished;
    private String createTime;
}
