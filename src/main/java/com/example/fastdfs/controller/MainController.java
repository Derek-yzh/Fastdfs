package com.example.fastdfs.controller;

import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.sun.istack.internal.NotNull;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author: Derek
 * @DateTime: 2021/2/5 21:48
 * @Description: TODO
 */
@RestController
@RequestMapping("/upload")
public class MainController {

    public final Map<String,String> account = new HashMap<>();

    @Autowired
    protected FastFileStorageClient fc;

    @PostMapping("/file")
    public String fileUpload (@RequestParam @NotNull MultipartFile filename, String password, HttpServletRequest request) {

        // 元数据
        Set<MetaData> metaDataSet = new HashSet<MetaData>();
        metaDataSet.add(new MetaData("Author", "aHua"));
        metaDataSet.add(new MetaData("CreateDate", "2021-01-01"));

        StorePath uploadFile = null;
        try {
            uploadFile = fc.uploadFile(filename.getInputStream(), filename.getSize(), FilenameUtils.getExtension(filename.getOriginalFilename()), metaDataSet);

            System.out.println("Path: " + uploadFile.getPath());
            System.out.println("FullPath: " + uploadFile.getFullPath());
            //Path: M00/00/00/wKhKLGAdWBeAaeW1AAAg8ggUGdA113.txt
            //FullPath: group1/M00/00/00/wKhKLGAdWBeAaeW1AAAg8ggUGdA113.txt
            account.put("password",password);
            account.put("path",uploadFile.getPath());

        } catch (IOException e) {
            e.printStackTrace();
            return "Error";
        }

        return uploadFile.getPath();
    }

    @RequestMapping("/down")
    @ResponseBody
    public ResponseEntity<byte[]> down(HttpServletResponse resp) {

        DownloadByteArray cb = new DownloadByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "aaa.xx");
        byte[] bs = fc.downloadFile("group1", "M00/00/00/wKhKLGAdWBeAaeW1AAAg8ggUGdA113.txt", cb);

        return new ResponseEntity<>(bs,headers, HttpStatus.OK);
    }

    @RequestMapping("/see")
    @ResponseBody
    public ResponseEntity<byte[]> see() {

        DownloadByteArray cb = new DownloadByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        //headers.setContentDispositionFormData("attachment", "aaa.xx");
        byte[] bs = fc.downloadFile("group1", "M00/00/00/wKhKLGAdR4-AdhYhABZj9xP4qIU544_big.png", cb);

        return new ResponseEntity<>(bs,headers, HttpStatus.OK);
    }

}
