package com.vivid.framework.common.data;

import org.springframework.http.HttpStatus;


/**
 * Created by wuwei2_m on 2018/3/7.
 */
public class ResponseResult<T> {

    private boolean success;
    private String message;
    private T data;
    private String errCode;
    private HttpStatus status;
    private T ext;

    public ResponseResult(){}  //给某些框架序列化用
    /**
     * 最全参数
     * @param success
     * @param message
     * @param data
     * @param errCode
     * @param status
     */
    public ResponseResult(boolean success, String message, T data, String errCode, HttpStatus status, T ext) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errCode = errCode;
        this.status = status;
        this.ext = ext;
    }

    public ResponseResult(ResultBuilder<T> resultBuilder){
        this(resultBuilder.success,resultBuilder.message,resultBuilder.data,resultBuilder.errCode,HttpStatus.OK,resultBuilder.ext);
    }



    public boolean getSuccess() {
        return this.success;
    }
//
//    public void setSuccess(boolean success) {
//        this.success = success;
//    }

    public String getMessage() {
        return message;
    }

//    public void setMessage(String message) {
//        this.message = message;
//    }

    public T getData() {
        return data;
    }

//    public void setData(T data) {
//        this.data = data;
//    }

    public String getErrCode() {
        return errCode;
    }

//    public void setErrCode(String errCode) {
//        this.errCode = errCode;
//    }

    public T getExt() {
        return ext;
    }

    public static<T> ResponseResult<T> successed(T data) {
        ResultBuilder<T> builder = status(true,null,data,null,HttpStatus.OK,null);
        return builder.build();
    }
    public static ResponseResult failed(String message) {
        return failed(null,message);
    }
    public static<T> ResponseResult failed(T data,String message) {
        ResultBuilder<T> builder =  status(false,message,null,null,HttpStatus.OK,null);
        return  builder.build();
    }

    private static<T> ResultBuilder<T> status(boolean success, String message, T data, String errCode, HttpStatus status, T ext){
        return new ResultBuilder(success, message, data, errCode, status, ext);
    }

    public static ResponseResult<String> string(String data) {
        ResultBuilder<String> builder = status(data == null, data, data, null, HttpStatus.OK, null);
        return builder.build();
    }

    public static class ResultBuilder<T>{
        private boolean success;
        private String message;
        private T data;
        private String errCode;
        private HttpStatus status;
        private T ext;

        public ResultBuilder(boolean success){
            this.success = success;
        }

        private ResultBuilder(boolean success, String message, T data, String errCode,HttpStatus status, T ext) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.errCode = errCode;
            this.status = status;
            this.ext = ext;
        }

        /**
         * 只提供快捷操作,不暴露Builder
         * @return
         */
        public ResponseResult<T> build(){
            return new ResponseResult<T>(this);
        }


//        public ResultBuilder setSuccess(boolean success) {
//            this.success = success;
//            return this;
//        }

        public ResultBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        public ResultBuilder setData(T data) {
            this.data = data;
            return this;
        }

        public ResultBuilder setErrCode(String errCode) {
            this.errCode = errCode;
            return this;
        }

        /**
         * 如果有自定义的响应状态码,如4,5开头的可以在builder设置.
         * 此项可以影响客户端接收的状态,期会在全局捕获
         * TODO ResponseBodyAdvice beforBodyWrite 前
         * @param status
         * @return
         */
        public ResultBuilder setStatus(HttpStatus status) {
            this.status = status;
            return this;
        }

        /**
         * 在实际业务中经常有拓展的全局标记,或者业务后期增加数据,可以放此(不建议使用)
         * @param ext
         * @return
         */
        @Deprecated
        public ResultBuilder setExt(T ext) {
            this.ext = ext;
            return this;
        }
    }
    //ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
}
