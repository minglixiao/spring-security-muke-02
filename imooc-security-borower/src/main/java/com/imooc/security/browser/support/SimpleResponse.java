/**
 *
 */
package com.imooc.security.browser.support;

/**
 * 只是一个响应的实体类
 */
public class SimpleResponse {

    public SimpleResponse(Object content){
        this.content = content;
    }

    private Object content;

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

}
