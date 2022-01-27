package com.vivid.biz.flow.entity.code;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wc_file_upload")
public class FileUploadEnt {
    private String fileId;
    private String bizId;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String dirName;
    private String uploader;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status;
}
