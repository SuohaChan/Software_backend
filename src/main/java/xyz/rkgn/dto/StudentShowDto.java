package xyz.rkgn.dto;

import lombok.Data;

@Data
public class StudentShowDto
{
    private Long id;
    private String nickname;
    private String avatar;
    private String type = "Student";
    private Integer level;
    private Integer totalExperience;
}
