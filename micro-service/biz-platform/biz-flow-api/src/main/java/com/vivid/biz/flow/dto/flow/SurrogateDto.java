package com.vivid.biz.flow.dto.flow;

import com.vivid.biz.flow.entity.apply.SurrogateEnt;
import lombok.Data;

@Data
public class SurrogateDto extends SurrogateEnt {
    private String operatorView;
    private String surrogateView;
}
