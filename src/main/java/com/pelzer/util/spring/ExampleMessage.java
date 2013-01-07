package com.pelzer.util.spring;

import com.pelzer.util.json.JSONObject;

public class ExampleMessage extends JSONObject {

  @Override
  protected String getIdentifier() {
    return "example";
  }

}
