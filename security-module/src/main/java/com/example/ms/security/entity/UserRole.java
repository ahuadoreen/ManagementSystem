package com.example.ms.security.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@EqualsAndHashCode(of = {"userId", "roleId"})
@Data
public class UserRole {
    private long userId;
    private long roleId;
    private String createName;
    private Instant createTime;
}
