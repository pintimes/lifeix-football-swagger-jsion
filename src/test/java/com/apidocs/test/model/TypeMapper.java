package com.apidocs.test.model;

/**
 * @author xule
 * @version 2017年4月25日 下午3:45:08
 */
public class TypeMapper {
    private String wraptype;
    private String basetype;
    private String jsontype;
    private String jsonformat;
    private String response;
    private String responseInnerObj;
    
    
    public String getResponse() {
        return response;
    }
    public void setResponse(String response) {
        this.response = response;
    }
    public String getResponseInnerObj() {
        return responseInnerObj;
    }
    public void setResponseInnerObj(String responseInnerObj) {
        this.responseInnerObj = responseInnerObj;
    }
    public String getBasetype() {
        return basetype;
    }
    public void setBasetype(String basetype) {
        this.basetype = basetype;
    }
    public String getWraptype() {
        return wraptype;
    }
    public void setWraptype(String wraptype) {
        this.wraptype = wraptype;
    }
    public String getJsontype() {
        return jsontype;
    }
    public void setJsontype(String jsontype) {
        this.jsontype = jsontype;
    }
    public String getJsonformat() {
        return jsonformat;
    }
    public void setJsonformat(String jsonformat) {
        this.jsonformat = jsonformat;
    }
    public TypeMapper() {
        super();
    }
    public TypeMapper(String wraptype, String basetype, String jsontype, String jsonformat, String response,
            String responseInnerObj) {
        super();
        this.wraptype = wraptype;
        this.basetype = basetype;
        this.jsontype = jsontype;
        this.jsonformat = jsonformat;
        this.response = response;
        this.responseInnerObj = responseInnerObj;
    }

}
