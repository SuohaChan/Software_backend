package xyz.rkgn.dto;

import lombok.Data;

/**
 * @author SuohaChan
 * @data 2025/9/14
 */


@Data
public class AppealUpdateDto {
    private Long id;

    private Long handlerId;

    private String replyContent;

    private String status;  // accept/processing/complete/rejected
}

