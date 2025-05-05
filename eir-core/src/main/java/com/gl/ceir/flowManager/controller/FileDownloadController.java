package com.gl.ceir.flowManager.controller;

import com.gl.ceir.flowManager.configuration.AppConfiguration;
import com.gl.ceir.flowManager.contstants.*;
import com.gl.ceir.flowManager.orchestrator.FileOrchestrator;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.MalformedURLException;

@RestController
@RequestMapping(path = ResourcesUrls.FILE_DOWNLOAD_RESOURCE_PATH)
public class FileDownloadController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    FileOrchestrator fileOrchestrator;

    @Autowired
    AppConfiguration appConfiguration;

    @GetMapping("/start/{listType}/{action}")
    public ResponseEntity downloadFile(@PathVariable("listType") ListType listType, @PathVariable("action") ListAction action) {
        FileDownloadStatus fileDownloadStatus = fileOrchestrator.downloadFile(listType, action);
        if (fileDownloadStatus == FileDownloadStatus.PROGRESS)
            return new ResponseEntity<FileDownloadStatus>(fileDownloadStatus, HttpStatus.OK);
        else {
            File file = new File(appConfiguration.getFilePath() + "/" + listType.getFilename());
            if (!file.exists())
                return new ResponseEntity<String>("File doesn't exist  " + (appConfiguration.getFilePath() + "/" + listType.getFilename()), HttpStatus.NOT_ACCEPTABLE);
            Resource resource = null;
            try {
                resource = new UrlResource(file.toURI());
            } catch (MalformedURLException e) {
                log.error("URI path not found Error:{}", e.getMessage(), e);
                e.printStackTrace();
            }
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);

        }
    }

}
