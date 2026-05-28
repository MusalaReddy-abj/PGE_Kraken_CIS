package com.pge.kraken.cis.routes.FileListner.MDMEvents;

import com.pge.kraken.cis.configs.FtpConfig;
import com.pge.kraken.cis.logging.StructuredLogger;
import com.pge.kraken.cis.processors.EventXmlProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventsFTPListner extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(EventsFTPListner.class);

    @Override
    public void configure() throws Exception {
        FtpConfig cfg = FtpConfig.fromProperties();

        String ftpUri = String.format(
            "ftp://%s@%s:%d/%s?password=%s&binary=true&passiveMode=true&move=MDMEvents&include=.*\\.xml&delay=%d",
            cfg.getUsername(), cfg.getHost(), cfg.getPort(), cfg.getHesEventsRemoteDir(), cfg.getPassword(), cfg.getPollDelay());

        from(ftpUri)
                .routeId("ftp-listener-route")
                .process(exchange -> {
                    StructuredLogger.info(exchange, "FTP_FILE_PICKED",
                            String.format("Picked up FTP file: %s", exchange.getIn().getHeader("CamelFileName")));
                    StructuredLogger.info(exchange, "FTP_FILE_RECEIVED", "FTP file received from remote directory");
                })
                .process(new EventXmlProcessor())
                .to("direct:processFtpFile");
    }
}
