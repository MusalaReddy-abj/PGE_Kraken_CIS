package com.pge.kraken.cis.models.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class D1DeviceEventSeeder {

    private String externalSenderId;
    private String deviceIdentifierNumber;
    private String externalEventName;
    private String eventDateTime;

    @Override
    public String toString() {
        return String.format(
                "<D1-DeviceEventSeeder>" +
                "<externalSenderId>%s</externalSenderId>" +
                "<deviceIdentifierNumber>%s</deviceIdentifierNumber>" +
                "<externalEventName>%s</externalEventName>" +
                "<eventDateTime>%s</eventDateTime>" +
                "</D1-DeviceEventSeeder>",
                externalSenderId, deviceIdentifierNumber, externalEventName, eventDateTime);
    }
}
