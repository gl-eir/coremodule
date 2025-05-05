package com.gl.ceir.flowManager.executor;

import com.gl.ceir.flowManager.configuration.ThreadPoolConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class UdpThreadPoolExecutor extends ThreadPoolExecutor {

    @Autowired
    public UdpThreadPoolExecutor(ThreadPoolConfiguration threadPoolConfig, UdpRejectedExecutionHandler rejectedExecutionHandler) {
        super(threadPoolConfig.getCorePoolSize(), threadPoolConfig.getMaximumPoolSize(), threadPoolConfig.getKeepAliveTime(), TimeUnit.SECONDS, new ArrayBlockingQueue<>(threadPoolConfig.getQueueSize()), rejectedExecutionHandler);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        //Perform beforeExecute() logic
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t != null) {
            //Perform exception handler logic
        }
        //Perform afterExecute() logic
    }
}
