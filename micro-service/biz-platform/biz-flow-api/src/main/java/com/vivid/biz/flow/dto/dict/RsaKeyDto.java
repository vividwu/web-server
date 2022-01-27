package com.vivid.biz.flow.dto.dict;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RsaKeyDto implements Serializable {
    private String privateKey;
    private String publicKey;
    private String sign;
    private String raw;
}
