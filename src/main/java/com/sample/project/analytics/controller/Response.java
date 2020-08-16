package com.sample.project.analytics.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * @author arvin on 8/16/20
 **/
@Value
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Response {

    private String code;

    private Object result;

}
