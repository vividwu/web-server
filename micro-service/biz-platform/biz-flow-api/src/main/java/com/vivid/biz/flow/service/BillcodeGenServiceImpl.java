package com.vivid.biz.flow.service;

import com.vivid.biz.flow.entity.workflow.BillcodeGenEnt;
import com.vivid.biz.flow.repository.workflow.BillcodeGenMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class BillcodeGenServiceImpl {
    @Autowired
    BillcodeGenMapper billcodeGenMapper;
    public String GetBillCode(String processName) {
        LocalDate date = LocalDate.now();
        String sDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Integer num = billcodeGenMapper.getBillCode(sDate, processName);
        BillcodeGenEnt ent = new BillcodeGenEnt();
        if(num == null){
            ent.setNum(1);
        }else{
            ent.setNum(num+1);
        }
        ent.setGenDate(date);
        ent.setProcessName(processName);
        billcodeGenMapper.insert(ent);
        return String.format("%s-%s-%05d", processName,date.format(DateTimeFormatter.ofPattern("yyyyMMdd")),ent.getNum());
    }
}
