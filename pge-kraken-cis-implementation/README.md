# pge-kraken-cis-implementation

Apache Camel based integration service for CIS on-demand read workflows.

## Features
- Kafka inbound route with validation, enrichment, retry, and DLQ handling
- HES service integration placeholder
- Audit and health monitoring routes
- Spring Boot + Camel configuration scaffolding

## Build

```bash
mvn clean package
```

## Run

```bash
mvn spring-boot:run
```
