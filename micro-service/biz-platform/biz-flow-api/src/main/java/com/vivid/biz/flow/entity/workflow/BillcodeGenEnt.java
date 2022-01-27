package com.vivid.biz.flow.entity.workflow;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
@Data
@TableName("wc_billcode_gen")
public class BillcodeGenEnt {
    private String processName;
    private LocalDate genDate;
    private Integer num;
}
