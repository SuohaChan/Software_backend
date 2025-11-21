package xyz.rkgn.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author SuohaChan
 * @data 2025/9/14
 */

@Data
public class AppealSubmitDto {
    private Long userId;

    private String appealType;  // account/content/reward/other

    private String appealTitle;

    private String appealDescription;

    private String contactInfo;
}
