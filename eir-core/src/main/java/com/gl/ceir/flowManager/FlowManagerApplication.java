package com.gl.ceir.flowManager;


import com.gl.ceir.flowManager.alert.AlertConfig;
import com.gl.ceir.flowManager.alert.AlertConfigDto;
import com.gl.ceir.flowManager.configuration.AppConfiguration;
import com.gl.ceir.flowManager.contstants.AlertIds;
import com.gl.ceir.flowManager.server.IUdpServer;
import com.gl.ceir.flowManager.server.RequestProcessorThread;
import com.gl.ceir.flowManager.server.UdpSender;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Map;


@SpringBootApplication
@EnableAsync
@EnableEncryptableProperties
public class FlowManagerApplication implements CommandLineRunner {

    private static final Logger logger = LogManager.getLogger(FlowManagerApplication.class);

    public static void main(String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(FlowManagerApplication.class, args);
        validationAlertsConfig(context);
    }

    @Autowired
    RequestProcessorThread requestProcessorThread;
    @Autowired
    AppConfiguration configuration;

    @Autowired
    IUdpServer udpServer;

    @Override
    public void run(String... args) throws Exception {
        //7001
        udpServer.startServer(configuration.getUdpServerPort());
        logger.info("imeiNullPattern is null {} imeiNullPattern:{}", (configuration.getImeiNullPattern() == null), configuration.getImeiNullPattern());
        new Thread(udpServer).start();
        requestProcessorThread.start();
    }

    private static void validationAlertsConfig(ApplicationContext context) {
        AlertConfig alertConfig = context.getBean(AlertConfig.class);
        if (StringUtils.isBlank(alertConfig.getPostUrl())) {
            logger.error("Alerts Alert's URL Configuration missing");
            System.exit(1);
        } else if (StringUtils.isBlank(alertConfig.getProcessId())) {
            logger.error("Alerts Alert's Process Id Configuration missing");
            System.exit(1);
        } else {
            Map<AlertIds, AlertConfigDto> requiredAlerts = context.getBean(AlertConfig.class).getAlertsMapping();
            if (requiredAlerts == null) {
                logger.error("Alerts Configuration missing");
                System.exit(1);
            } else {
                for (AlertIds alertId : AlertIds.values()) {
                    AlertConfigDto alertConfigDto = requiredAlerts.get(alertId);
                    if (alertConfigDto == null) {
                        logger.error("AlertsId:{} Configuration missing", alertId);
                        System.exit(1);
                    } else {
                        if (StringUtils.isBlank(alertConfigDto.getAlertId()) || StringUtils.isBlank(alertConfigDto.getMessage())) {
                            logger.error("AlertsId or Message  Configuration missing for alertId:{}", alertId);
                            System.exit(1);
                        }
                    }
                }
            }
        }
    }

}
