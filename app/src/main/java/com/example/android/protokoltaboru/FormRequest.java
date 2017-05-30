package com.example.android.protokoltaboru;

/**
 * Created by Man on 2017-03-31.
 */

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class FormRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "http://piomarpanel.cba.pl/app_scripts/receive.php";
    private Map<String, String> params;

    public FormRequest(String name, String password, String opis, String zdjecie1, String zdjecie2, String nrRej, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();

        params.put("name", name);
        params.put("password", password);
        params.put("nrRej", nrRej);
        params.put("opis", opis);
        params.put("zdjecie1", zdjecie1);
        params.put("zdjecie2", zdjecie2);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
