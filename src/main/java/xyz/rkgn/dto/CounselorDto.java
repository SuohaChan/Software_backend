package xyz.rkgn.dto;

import lombok.Data;

@Data
public class CounselorDto {
    private Long id;
    private String name;
    private String type = "Counselor";
}
