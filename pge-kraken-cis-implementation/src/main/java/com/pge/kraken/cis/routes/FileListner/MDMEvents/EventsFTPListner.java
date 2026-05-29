package com.pge.kraken.cis.routes.FileListner.MDMEvents;

import com.pge.kraken.cis.configs.FtpConfig;
import com.pge.kraken.cis.logging.StructuredLogger;
import com.pge.kraken.cis.processors.EventXmlProcessor;
import com.pge.kraken.cis.processors.FtpExceptionProcessor;
import org.apache.camel.builder.RouteBuilder;

public class EventsFTPListner extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        configureErrorHandling();

        FtpConfig cfg = FtpConfig.fromProperties();

        String ftpUri = String.format(
            "ftp://%s@%s:%d/%s?password=%s&binary=true&passiveMode=true&move=ArcheiveFolder&include=.*\\.xml&delay=%d",
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

    private void configureErrorHandling() {
        onException(Exception.class)
                .handled(true)
                .routeId("ftp-listener-exception-handler")
                .process(new FtpExceptionProcessor());
    }
}
