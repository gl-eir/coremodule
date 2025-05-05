package com.gl.ceir.flowManager.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class UdpRejectedExecutionHandler implements RejectedExecutionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        logger.info("EirRejectedExecutionHandler by executor " + r.getClass());
    }
}
