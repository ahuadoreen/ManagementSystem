package com.example.ms.basic.entity;

import com.example.ms.annotation.TableName;
import com.example.tools.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(of = {"dictionaryName", "parentId"}, callSuper = false)
@Data
@TableName(uniqueKeys = {"dictionaryName", "parentId"})
public class Dictionary extends BaseEntity {
    @Getter
    public enum DictionaryType {
        ENUM(0),
        SQL(1),
        API(2);
        private final int value;
        DictionaryType(int value) {
            this.value = value;
        }
    }

    /**
     * 字典名称，用来描述该字典的含义
     */
    private String dictionaryName;
    /**
     * 字典索引关键词，用来唯一标识该字典
     */
    private String dictionaryKey;
    /**
     * 字典值，用来描述该字典的值，如果该字典有子集，则该字段为空
     */
    private String dictionaryValue;
    /**
     * 字典类型，0：枚举，1：SQL，2：API
     */
    private Integer dictionaryType;
    /**
     * 调用的服务名称，当该字典类型为SQL或API时，该字段为必填项
     */
    private String serviceName;
    /**
     * 前端样式，用来补充在页面上显示的样式，通常枚举类的字典可能要用到
     * 配置的时候需要对应前端使用的组件需求，但是要配置成json格式，如{"type": "success"}
     */
    private String frontendStyle;
    /**
     * 备注
     */
    private String remark;
    /**
     * 父级字典id
     */
    private long parentId;
    /**
     * 排序
     */
    private Integer orderNo;
    /**
     * 是否启用
     */
    private Boolean enable;
    /**
     * dictionary_key转化的类型，可设置类型对应DictionaryDataType
     */
    private Integer keyType;
    /**
     * dictionary_value转化的类型，可设置类型对应DictionaryDataType
     */
    private Integer valueType;
}
