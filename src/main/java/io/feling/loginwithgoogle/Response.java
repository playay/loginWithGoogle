package io.feling.loginwithgoogle;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.Map;

public class Response<T> implements Serializable {

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").serializeNulls().create();

    private Result result = new Result<T>();
    private Map<String, Object> extraRoot = Maps.newHashMap();
    private Map<String, Object> extraData = Maps.newHashMap();

    public Response<T> fail() {
        result.setSuccess(false);
        if ("E000000".equals(result.getCode())) {
            result.setCode("");
        }
        if ("请求成功".equals(result.getMessage())) {
            result.setMessage("");
        }
        return this;
    }

    public Response<T> success() {
        result.setSuccess(true);
        return this;
    }

    public Response<T> errCode(String errCode) {
        result.setCode(errCode);
        return this;
    }

    public Response<T> msg(String msg) {
        result.setMessage(msg);
        return this;
    }

    public Response data(T data) {
        result.setData(data);
        return this;
    }

    public Response putToRoot(String key, Object value) {
        extraRoot.put(key, value);
        return this;
    }

    public Response putToData(String key, Object value) {
        extraData.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        if (!extraData.isEmpty() && result.getData() != null) {
            Map fullData = gson.fromJson(gson.toJson(result.getData()), Map.class);
            fullData.putAll(extraData);
            result.setData(fullData);
        }
        if (extraRoot.isEmpty()) {
            return gson.toJson(result);
        } else {
            Map<String, Object> extraRootResult = gson.fromJson(gson.toJson(result), Map.class);
            extraRootResult.putAll(extraRoot);
            return gson.toJson(extraRootResult);
        }
    }

    private class Result<T> {

        private boolean success = true;

        private String code = "E000000";

        private String message = "请求成功";

        private T data;

        public Result() {
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}
