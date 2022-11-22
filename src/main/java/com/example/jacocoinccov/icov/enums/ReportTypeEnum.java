package com.example.jacocoinccov.icov.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum ReportTypeEnum {
    BRANCH(1, "分支对比"),
    VERSION(2, "版本对比"),
    ;

    private final int type;
    private final String desc;
}