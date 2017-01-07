package com.zncm.library.utils;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MyStringRequest extends StringRequest {
        private Map<String, String> headers = new HashMap<String, String>();
        private Map<String, String> params = new HashMap<String, String>();

        public MyStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return headers;
        }


        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return params;
        }

        public void setHeader(String title, String content) {
            headers.put(title, content);
        }
    }