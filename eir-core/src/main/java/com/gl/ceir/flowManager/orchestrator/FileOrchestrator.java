package com.gl.ceir.flowManager.orchestrator;

import com.gl.ceir.flowManager.contstants.FileDownloadStatus;
import com.gl.ceir.flowManager.contstants.ListAction;
import com.gl.ceir.flowManager.contstants.ListType;
import com.gl.ceir.flowManager.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class FileOrchestrator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private BlackListService blackListService;

    @Autowired
    private BlockedTACService blockedTACService;
    @Autowired
    private AllowedTACService allowedTACService;
    @Autowired
    private TrackedListService trackedListService;
    @Autowired
    private ExceptionListService exceptionListService;

    public FileDownloadStatus downloadFile(ListType listType, ListAction action) {
        FileDownloadStatus fileStatus = null;
        logger.info("Request for listType{} action:{}", listType, action);
        switch (listType) {
            case TRACKED_LIST -> {
                if (action == ListAction.START) {
                    synchronized (trackedListService) {
                        logger.info("Request for listType:TRACKED_LIST action:{} FileStatus:{}", action, trackedListService.getFileStatus());
                        if (trackedListService.getFileStatus() == FileDownloadStatus.COMPLETED
                                || trackedListService.getFileStatus() == FileDownloadStatus.COMPLETED_ERROR) {
                            trackedListService.setFileStatus(FileDownloadStatus.PROGRESS);
                            logger.info("Starting for listType:TRACKED_LIST action:{} FileStatus:{}", action, trackedListService.getFileStatus());
                            CompletableFuture.runAsync(() -> trackedListService.writeFile());
                        }
                    }
                }
                fileStatus = trackedListService.getFileStatus();
                break;
            }
            case BLOCKED_LIST -> {
                if (action == ListAction.START) {
                    synchronized (blackListService) {
                        logger.info("Request for listType:BLOCKED_LIST action:{} FileStatus:{}", action, trackedListService.getFileStatus());
                        if (blackListService.getFileStatus() == FileDownloadStatus.COMPLETED
                                || blackListService.getFileStatus() == FileDownloadStatus.COMPLETED_ERROR) {
                            blackListService.setFileStatus(FileDownloadStatus.PROGRESS);
                            logger.info("Starting for listType:BLOCKED_LIST action:{} FileStatus:{}", action, trackedListService.getFileStatus());
                            CompletableFuture.runAsync(() -> blackListService.writeFile());
                        }
                    }
                }
                fileStatus = blackListService.getFileStatus();
            }
            case EXCEPTION_LIST -> {
                if (action == ListAction.START) {
                    synchronized (exceptionListService) {
                        logger.info("Request for listType:EXCEPTION_LIST action:{} FileStatus:{}", action, trackedListService.getFileStatus());
                        if (exceptionListService.getFileStatus() == FileDownloadStatus.COMPLETED
                                || exceptionListService.getFileStatus() == FileDownloadStatus.COMPLETED_ERROR) {
                            exceptionListService.setFileStatus(FileDownloadStatus.PROGRESS);
                            logger.info("Starting for listType:EXCEPTION_LIST action:{} FileStatus:{}", action, trackedListService.getFileStatus());
                            CompletableFuture.runAsync(() -> exceptionListService.writeFile());
                        }
                    }
                }
                fileStatus = exceptionListService.getFileStatus();
            }
            case BLOCKED_TAC -> {
                if (action == ListAction.START) {
                    synchronized (blockedTACService) {
                        logger.info("Request for listType:BLOCKED_TAC action:{} FileStatus:{}", action, trackedListService.getFileStatus());
                        if (blockedTACService.getFileStatus() == FileDownloadStatus.COMPLETED
                                || blockedTACService.getFileStatus() == FileDownloadStatus.COMPLETED_ERROR) {
                            blockedTACService.setFileStatus(FileDownloadStatus.PROGRESS);
                            logger.info("Starting for listType:BLOCKED_TAC action:{} FileStatus:{}", action, trackedListService.getFileStatus());
                            CompletableFuture.runAsync(() -> blockedTACService.writeFile());
                        }
                    }
                }
                fileStatus = blockedTACService.getFileStatus();
            }
            case ALLOWED_TAC -> {
                if (action == ListAction.START) {
                    synchronized (allowedTACService) {
                        logger.info("Request for listType:ALLOWED_TAC action:{} FileStatus:{}", action, trackedListService.getFileStatus());
                        if (allowedTACService.getFileStatus() == FileDownloadStatus.COMPLETED
                                || allowedTACService.getFileStatus() == FileDownloadStatus.COMPLETED_ERROR) {
                            allowedTACService.setFileStatus(FileDownloadStatus.PROGRESS);
                            logger.info("Starting for listType:ALLOWED_TAC action:{} FileStatus:{}", action, trackedListService.getFileStatus());
                            CompletableFuture.runAsync(() -> allowedTACService.writeFile());
                        }
                    }
                }
                fileStatus = allowedTACService.getFileStatus();
            }
        }
        return fileStatus;
    }

}
