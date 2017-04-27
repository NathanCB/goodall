package com.goodall.serializers;

import com.goodall.entities.Glitch;
import com.goodall.entities.HasId;

import java.util.HashMap;
import java.util.Map;

public class GlitchSerializer extends JsonDataSerializer{

    public String getType() {
        return "events";
    }

    Map<String, Object> getAttributes(HasId entity) {
        Map<String, Object> result = new HashMap<>();
        Glitch glitch = (Glitch) entity;

        result.put("id", glitch.getId());
        result.put("url", glitch.getUrl());

        return result;
    }

    public Map<String, String> getRelationshipUrls() {
        return new HashMap<String, String>();
    }

}
