package com.elice.iliceworksbe.common.constant;


import com.fasterxml.jackson.annotation.JsonProperty;

public enum ContentType {
    @JsonProperty("application/x-www-form-urlencoded")
    APPLICATION_X_WWW_FORM_URLENCODED,

    @JsonProperty("application/json")
    APPLICATION_JSON
}

