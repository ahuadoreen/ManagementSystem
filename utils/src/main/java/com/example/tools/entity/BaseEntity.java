package com.example.tools.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.Instant;

@Data
@EqualsAndHashCode
public abstract class BaseEntity implements Serializable {
    @Null(groups = {Insert.class})
    @NotNull(groups = {Update.class}, message="id不能为空")
    private Long id;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String createName;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createTime;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String updateName;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updateTime;
}
