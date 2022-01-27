package com.vivid.biz.flow.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.vivid.biz.flow.entity.code.FileUploadEnt;
import com.vivid.biz.flow.repository.code.FileUploadMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {
    @Value("${upload.path}")
    private String uploadPath;
    @Autowired
    FileUploadMapper fileUploadMapper;

    @PostMapping("/open/form/upload")
    public Map<String, Object> formUpload(@RequestParam(value = "file", required = false) MultipartFile dropFile, HttpServletRequest request) {
        String bizId = request.getParameter("bill_code");
        Map<String, Object> result = new HashMap<>();
        //前端上传的文件
        MultipartFile myFile = dropFile;
        //文件名
        String fileName = myFile.getOriginalFilename();
        //后缀
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        //文件上传路径
        String ymd = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String filePath = uploadPath + File.separator + ymd;//request.getSession().getServletContext().getRealPath("/upload/");
        //判断路径是否存在
        File file = new File(filePath);
        //不存在 则创建
        if (!file.exists()) {
            file.mkdir();
        }
        //将文件更名后写入指定路径下
        String fileId = ymd + UUID.randomUUID() + "" + (System.currentTimeMillis() % 10000);
        file = new File(filePath, fileId + fileSuffix);
        try {
            myFile.transferTo(file);
            FileUploadEnt ent = new FileUploadEnt();
            ent.setFileId(fileId);
            ent.setBizId(bizId);
            ent.setFileName(fileName);
            ent.setFileSize(myFile.getSize());
            ent.setFileType(fileSuffix);
            ent.setDirName(ymd);
            ent.setCreateTime(LocalDateTime.now());
            ent.setStatus(1);
            fileUploadMapper.insert(ent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Dropzone上传
        //String serverPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        result.put("fileName", file.getName());
        result.put("fileId", fileId);
        return result;
    }

    @RequestMapping(value = "/open/form/file",produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getFile(String img, HttpServletResponse response) throws IOException {  //210815
        String dir = img.substring(0, 6);
        String filePath = uploadPath + File.separator + dir;
        File file = new File(filePath + File.separator + img);
//        String prefix = img.split("\\.")[1];
//        response.setContentType("image/"+prefix);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes, 0, inputStream.available());
        return bytes;
    }

    @GetMapping("/open/form/file_list")
    public List<FileUploadEnt> getFileByBizId(String bizId) {
        List<FileUploadEnt> fileUploadEnts = fileUploadMapper.selectList(new QueryWrapper<FileUploadEnt>().lambda()
                .eq(FileUploadEnt::getBizId, bizId).eq(FileUploadEnt::getStatus, 1));
        return fileUploadEnts;
    }

    @PostMapping("/open/form/remove")
    public String removeFile(String fileId) {  //todo 权限验证
        UpdateWrapper<FileUploadEnt> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("file_id",fileId)
                .set("status", 0).set("update_time",LocalDateTime.now());
        fileUploadMapper.update(null,updateWrapper);
        return null;
    }
}
