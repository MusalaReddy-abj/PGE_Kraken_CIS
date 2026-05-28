package com.pge.kraken.cis.configs;

import lombok.Data;

import java.io.InputStream;
import java.util.Properties;

@Data
public class FtpConfig {

    private String host;
    private int port;
    private String username;
    private String password;
    private String hesEventsRemoteDir;
    private long pollDelay;
    
    public static FtpConfig fromProperties() {
        Properties props = new Properties();
        try (InputStream in = FtpConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            // swallow - defaults will be used
        }

        FtpConfig cfg = new FtpConfig();
        cfg.host = props.getProperty("ftp.host", "localhost");
        cfg.port = Integer.parseInt(props.getProperty("ftp.port", "21"));
        cfg.username = props.getProperty("ftp.username", "");
        cfg.password = props.getProperty("ftp.password", "");
        cfg.hesEventsRemoteDir = props.getProperty("ftp.hes.events.remotedir", "mdm/Events");
        cfg.pollDelay = Long.parseLong(props.getProperty("ftp.pollDelay", "60000"));
        return cfg;
    }
}
