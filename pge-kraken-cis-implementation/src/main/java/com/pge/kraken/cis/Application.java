package com.pge.kraken.cis;

import com.pge.kraken.cis.health.HealthCheckRoute;
import com.pge.kraken.cis.routes.AuditRoute;
import com.pge.kraken.cis.routes.DLQRoute;
import com.pge.kraken.cis.routes.HESOnDemandReadRoute;
import com.pge.kraken.cis.routes.KafkaConsumerRoute;
import com.pge.kraken.cis.routes.RetryRoute;
import com.pge.kraken.cis.routes.UndertowRoute;
import org.apache.camel.main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        LOG.info("Starting Apache Camel application");

        Main main = new Main();
        LOG.info("Registering Camel routes");
        main.configure().addRoutesBuilder(new UndertowRoute());
        main.configure().addRoutesBuilder(new KafkaConsumerRoute());
        main.configure().addRoutesBuilder(new HESOnDemandReadRoute());
        main.configure().addRoutesBuilder(new AuditRoute());
        main.configure().addRoutesBuilder(new DLQRoute());
        main.configure().addRoutesBuilder(new RetryRoute());
        main.configure().addRoutesBuilder(new HealthCheckRoute());

        LOG.info("Starting Camel runtime");
        main.run(args);
    }
}
