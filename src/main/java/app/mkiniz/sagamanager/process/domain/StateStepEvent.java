package app.mkiniz.sagamanager.process.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.f4b6a3.tsid.Tsid;

import java.time.ZonedDateTime;

public record StateStepEvent(Tsid stepId, String correlationId, String event, ZonedDateTime processDate,
                             JsonNode eventData) {
}
