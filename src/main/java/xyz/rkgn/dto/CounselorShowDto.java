package xyz.rkgn.dto;

import lombok.Data;

/**
 * @author SuohaChan
 * @data 2025/9/23
 */
@Data
public class CounselorShowDto {
    private Long id;
    private String nickname;
    private String avatar;
    private final String type = "Counselor";
}
