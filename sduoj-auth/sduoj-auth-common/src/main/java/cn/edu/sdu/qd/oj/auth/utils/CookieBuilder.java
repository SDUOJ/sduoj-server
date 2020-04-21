package cn.edu.sdu.qd.oj.auth.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CookieBuilder {
    private String key;
    private String value;
    private String expires;
    private String domain;
    private String path;

    public CookieBuilder setKey(String key) {
        this.key = key;
        return this;
    }

    public CookieBuilder setValue(String value) {
        this.value = value;
        return this;
    }

    public CookieBuilder setMaxAge(long ms) {
        // cookie的过期日期为 GMT 格式的时间。
        Date date = new Date(new Date().getTime() + ms);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        this.expires = sdf.format(date);
        return this;
    }

    public CookieBuilder setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public CookieBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.key);
        sb.append("=");
        sb.append(this.value);
        sb.append(";");
        if (null != this.expires) {
            sb.append("expires=");
            sb.append(this.expires);
            sb.append(";");
        }
        if (null != this.domain) {
            sb.append("domain=");
            sb.append(this.domain);
            sb.append(";");
        }
        if (null != this.path) {
            sb.append("path=");
            sb.append(this.path);
            sb.append(";");
        }
        return sb.toString();
    }
}