package com.example.ms.basic.entity;

import com.example.ms.annotation.TableName;
import com.example.tools.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(uniqueKeys = {"keyName"})
public class Language extends BaseEntity {
    private String keyName;
    private String enText;
    private String cnText;
}
