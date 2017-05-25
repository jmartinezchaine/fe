package uy.com.fusion.library.rest;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import uy.com.fusion.library.rest.utils.Assert;
import uy.com.fusion.library.rest.utils.LinkedCaseInsensitiveMap;

public class HttpHeaders {

    private static TimeZone GMT = TimeZone.getTimeZone("GMT");
    private static final String[] DATE_FORMATS = new String[] {
        "EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM dd HH:mm:ss yyyy"};

    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_FORMATS[0]).withLocale(Locale.ENGLISH)
        .withZone(DateTimeZone.forTimeZone(GMT));

    private static final Pattern MAX_AGE_PATTERN = Pattern.compile(".*max-age\\s*=\\s*(\\d+).*");
    private static final Pattern CONTENT_TYPE_PATTERN = Pattern
        .compile("(application|audio|example|image|message|model|multipart|text|video)/[a-zA-Z][a-zA-Z0-9\\.\\+\\-]*(;\\s*[a-zA-Z][a-zA-Z0-9\\.\\+\\-]*=[a-zA-Z0-9][a-zA-Z0-9\\-\\.\\+]*)*");

    private final Map<String, String> headers;

    private final Map<String, String> lowerCaseHeaders;

    public HttpHeaders() {
        this.headers = new LinkedCaseInsensitiveMap<String>(8, Locale.ENGLISH);
        this.lowerCaseHeaders = new HashMap<String, String>();
    }

    public String getAccept() {
        return this.get(ACCEPT);
    }

    public void setAccept(String acceptableMediaTypes) {
        this.set(ACCEPT, acceptableMediaTypes);
    }

    public void setAcceptCharset(String value) {

        List<String> list = split(value, ",\\s*");

        List<String> acceptableCharsets = new ArrayList<String>(list.size());
        for (String s : list) {
            Charset charset = Charset.forName(s);
            if (charset != null) {
                acceptableCharsets.add(charset.name().toLowerCase(Locale.ENGLISH));
            }
        }

        this.set(ACCEPT_CHARSET, join(acceptableCharsets, ", "));
    }

    public void setAcceptCharset(List<Charset> acceptableCharsets) {
        StringBuilder builder = new StringBuilder();
        for (Iterator<Charset> iterator = acceptableCharsets.iterator(); iterator.hasNext();) {
            Charset charset = iterator.next();
            builder.append(charset.name().toLowerCase(Locale.ENGLISH));
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        this.set(ACCEPT_CHARSET, builder.toString());
    }

    public String getAcceptCharset() {
        return this.get(ACCEPT_CHARSET);
    }

    public List<Charset> getAcceptCharsetList() {
        List<Charset> result = new ArrayList<Charset>();
        String value = this.get(ACCEPT_CHARSET);
        if (value != null) {
            String[] tokens = value.split(",\\s*");
            for (String token : tokens) {
                int paramIdx = token.indexOf(';');
                String charsetName;
                if (paramIdx == -1) {
                    charsetName = token;
                } else {
                    charsetName = token.substring(0, paramIdx);
                }
                if (!charsetName.equals("*")) {
                    result.add(Charset.forName(charsetName));
                }
            }
        }
        return result;
    }

    public void setAcceptEncoding(String encoding) {
        this.set(ACCEPT_ENCODING, encoding);
    }

    public String getAcceptEncoding() {
        return this.get(ACCEPT_ENCODING);
    }

    public List<String> getAcceptEncodingList() {
        return split(this.get(ACCEPT_ENCODING), ",\\s*");
    }

    /**
     * Set the set of allowed {@link HttpMethod HTTP methods}, as specified by the {@code Allow} header.
     * @param allowedMethods the allowed methods
     */
    public void setAllow(Collection<HttpMethod> allowedMethods) {
        Set<String> allowedMethodsAsString = new HashSet<String>(allowedMethods.size());
        for (HttpMethod m : allowedMethods) {
            allowedMethodsAsString.add(m.name());
        }
        this.set(ALLOW, join(allowedMethodsAsString, ", "));
    }

    /**
     * Return the set of allowed {@link HttpMethod HTTP methods}, as specified by the {@code Allow} header.
     * <p>Returns an empty set when the allowed methods are unspecified.
     * @return the allowed methods
     */
    public Set<HttpMethod> getAllow() {
        String value = this.get(ALLOW);
        if (value != null) {
            List<HttpMethod> allowedMethod = new ArrayList<HttpMethod>(5);
            String[] tokens = value.split(",\\s*");
            for (String token : tokens) {
                allowedMethod.add(HttpMethod.valueOf(token));
            }
            return EnumSet.copyOf(allowedMethod);
        } else {
            return EnumSet.noneOf(HttpMethod.class);
        }
    }

    public void setCacheControl(String cacheControl) {
        this.set(CACHE_CONTROL, cacheControl);
    }

    public String getCacheControl() {
        return this.get(CACHE_CONTROL);
    }

    public void setTransferEncoding(String contentEncoding) {
        this.set(TRANSFER_ENCODING, contentEncoding);
    }

    public String getTransferEncoding() {
        return this.get(TRANSFER_ENCODING);
    }

    public void setContentEncoding(String contentEncoding) {
        this.set(CONTENT_ENCODING, contentEncoding);
    }

    public String getContentEncoding() {
        return this.get(CONTENT_ENCODING);
    }

    public void setContentLength(long contentLength) {
        this.set(CONTENT_LENGTH, Long.toString(contentLength));
    }

    public long getContentLength() {
        String value = this.get(CONTENT_LENGTH);
        return (value != null ? Long.parseLong(value) : -1);
    }

    public void setContentType(String mediaType) {
        Assert.isTrue(!mediaType.contains("*"), "'Content-Type' cannot contain wildcard type '*'");
        Assert.isTrue(CONTENT_TYPE_PATTERN.matcher(mediaType).matches(), "Invalid 'Content-Type'");
        this.set(CONTENT_TYPE, mediaType);
    }

    public String getContentType() {
        return this.get(CONTENT_TYPE);
    }

    public String getContentTypeMediaType() {
        String contentType = this.get(CONTENT_TYPE);
        if (contentType != null) {
            String[] split = contentType.split(";");
            return split[0].trim().length() == 0 ? "" : split[0].trim();
        }
        return "";
    }

    public Charset getContentTypeCharset() {
        String contentType = this.get(CONTENT_TYPE);
        if (contentType != null) {
            int index = contentType.indexOf("=");
            if (index > 0) {
                String charset = contentType.substring(index + 1);
                return Charset.forName(charset);
            }
        }
        return null;
    }

    public void setDate(long date) {
        this.setDate(DATE, date);
    }

    public long getDate() {
        return this.getDate(DATE);
    }

    public void setETag(String eTag) {
        if (eTag != null) {
            Assert.isTrue(eTag.startsWith("\"") || eTag.startsWith("W/"), "Invalid eTag, does not start with W/ or \"");
            Assert.isTrue(eTag.endsWith("\""), "Invalid eTag, does not end with \"");
        }
        this.set(ETAG, eTag);
    }

    public String getETag() {
        return this.get(ETAG);
    }

    public void setExpires(long expires) {
        this.setDate(EXPIRES, expires);
    }

    public long getExpires() {
        return this.getDate(EXPIRES);
    }

    public void setIfModifiedSince(String date) {
        this.set(IF_MODIFIED_SINCE, date);
    }

    public String getIfModifiedSince() {
        return this.get(IF_MODIFIED_SINCE);
    }

    public void setIfNoneMatch(String ifNoneMatch) {
        this.set(IF_NONE_MATCH, ifNoneMatch);
    }

    public String getIfNoneMatch() {
        return this.get(IF_NONE_MATCH);
    }

    public void setLastModified(String lastModified) {
        this.set(LAST_MODIFIED, lastModified);
    }

    public String getLastModified() {
        return this.get(LAST_MODIFIED);
    }

    public void setMaxAge(int maxAgeInSeconds) {
        String cacheControl = this.get(CACHE_CONTROL);
        if (cacheControl == null) {
            cacheControl = "max-age=" + maxAgeInSeconds;
        } else {
            if (cacheControl.contains("max-age")) {
            	cacheControl = cacheControl.replaceFirst("max-age\\s*=\\s*(\\d+)", "max-age=" + maxAgeInSeconds);
            } else {
                cacheControl = cacheControl + "; max-age=" + maxAgeInSeconds;
            }
        }
        this.set(CACHE_CONTROL, cacheControl);
    }

    public int getMaxAge() {
        String cacheControl = this.get(CACHE_CONTROL);

        if (cacheControl != null) {
            Matcher matcher = MAX_AGE_PATTERN.matcher(cacheControl);

            if (matcher.matches()) {
                String maxAge = matcher.group(1);
                return Integer.parseInt(maxAge);
            }
        }
        return -1;
    }

    // Utility methods

    public long getDate(String headerName) {
        String headerValue = this.get(headerName);
        if (headerValue == null) {
            return -1;
        }
        for (String dateFormat : DATE_FORMATS) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
            simpleDateFormat.setTimeZone(GMT);
            try {
                return simpleDateFormat.parse(headerValue).getTime();
            } catch (ParseException e) {
                // ignore
            }
        }
        throw new IllegalArgumentException("Cannot parse date value \"" + headerValue + "\" for \"" + headerName
            + "\" header");
    }

    public void setDate(String headerName, long date) {
        this.set(headerName, formatter.print(date));
    }

    // Single string methods

    /**
     * Return the first header value for the given header name, if any.
     * @param headerName the header name
     * @return the first header value; or {@code null}
     */
    public String get(String headerName) {
        return this.headers.get(headerName);
    }

    public String getByLowerCaseKey(String lowerCaseKey) {
        return this.lowerCaseHeaders.get(lowerCaseKey);
    }

    public void set(String headerName, String headerValue) {
        this.headers.put(headerName, headerValue);
        this.lowerCaseHeaders.put(headerName.toLowerCase(), headerValue);
    }

    public void setAll(Map<String, String> values) {
        for (Entry<String, String> entry : values.entrySet()) {
            this.set(entry.getKey(), entry.getValue());
        }
    }

    public Map<String, String> toLowerCaseMap() {
        return Collections.unmodifiableMap(this.lowerCaseHeaders);
    }

    public Map<String, String> toMap() {
        return Collections.unmodifiableMap(this.headers);
    }

    @Override
    public HttpHeaders clone() {
        HttpHeaders cloned = new HttpHeaders();
        cloned.headers.putAll(this.headers);
        cloned.lowerCaseHeaders.putAll(this.lowerCaseHeaders);
        return cloned;
    }

    @Override
    public String toString() {
        return this.headers.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpHeaders)) {
            return false;
        }

        HttpHeaders that = (HttpHeaders) o;

        if (!this.headers.equals(that.headers)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return this.headers.hashCode();
    }

    private static String join(Collection<String> headerValues, String separator) {
        Assert.notNull(headerValues, "'headers' can't be null");
        Iterator<String> it = headerValues.iterator();
        if (it.hasNext()) {
            StringBuilder sb = new StringBuilder();
            sb.append(it.next());
            while (it.hasNext()) {
                sb.append(separator).append(it.next());
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    private static List<String> split(String headerValues, String separator) {

        if (headerValues != null) {
            String[] split = headerValues.split(separator);
            List<String> list = new ArrayList<String>();
            for (String s : split) {
                if (!s.trim().isEmpty()) {
                    list.add(s.trim());
                }
            }
            return list;
        }
        return null;
    }

    // HTTP Request and Response header fields

    /** The HTTP Cache-Control header field name. */
    public static final String CACHE_CONTROL = "Cache-Control";
    /** The HTTP Content-Length header field name. */
    public static final String CONTENT_LENGTH = "Content-Length";
    /** The HTTP Content-Type header field name. */
    public static final String CONTENT_TYPE = "Content-Type";
    /** The HTTP Date header field name. */
    public static final String DATE = "Date";
    /** The HTTP Via header field name. */
    public static final String VIA = "Via";
    /** The HTTP Warning header field name. */
    public static final String WARNING = "Warning";

    // HTTP Request header fields

    /** The HTTP Accept header field name. */
    public static final String ACCEPT = "Accept";
    /** The HTTP Accept-Charset header field name. */
    public static final String ACCEPT_CHARSET = "Accept-Charset";
    /** The HTTP Accept-Encoding header field name. */
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    /** The HTTP Accept-Language header field name. */
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    /** The HTTP Access-Control-Request-Headers header field name. */
    public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
    /** The HTTP Access-Control-Request-Method header field name. */
    public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
    /** The HTTP Authorization header field name. */
    public static final String AUTHORIZATION = "Authorization";
    /** The HTTP Connection header field name. */
    public static final String CONNECTION = "Connection";
    /** The HTTP Cookie header field name. */
    public static final String COOKIE = "Cookie";
    /** The HTTP Expect header field name. */
    public static final String EXPECT = "Expect";
    /** The HTTP From header field name. */
    public static final String FROM = "From";
    /** The HTTP Host header field name. */
    public static final String HOST = "Host";
    /** The HTTP If-Match header field name. */
    public static final String IF_MATCH = "If-Match";
    /** The HTTP If-Modified-Since header field name. */
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
    /** The HTTP If-None-Match header field name. */
    public static final String IF_NONE_MATCH = "If-None-Match";
    /** The HTTP If-Range header field name. */
    public static final String IF_RANGE = "If-Range";
    /** The HTTP If-Unmodified-Since header field name. */
    public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
    /** The HTTP Last-Event-ID header field name. */
    public static final String LAST_EVENT_ID = "Last-Event-ID";
    /** The HTTP Max-Forwards header field name. */
    public static final String MAX_FORWARDS = "Max-Forwards";
    /** The HTTP Origin header field name. */
    public static final String ORIGIN = "Origin";
    /** The HTTP Proxy-Authorization header field name. */
    public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
    /** The HTTP Range header field name. */
    public static final String RANGE = "Range";
    /** The HTTP Referer header field name. */
    public static final String REFERER = "Referer";
    /** The HTTP TE header field name. */
    public static final String TE = "TE";
    /** The HTTP Upgrade header field name. */
    public static final String UPGRADE = "Upgrade";
    /** The HTTP User-Agent header field name. */
    public static final String USER_AGENT = "User-Agent";

    // HTTP Response header fields

    /** The HTTP Accept-Ranges header field name. */
    public static final String ACCEPT_RANGES = "Accept-Ranges";
    /** The HTTP Access-Control-Allow-Headers header field name. */
    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    /** The HTTP Access-Control-Allow-Methods header field name. */
    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    /** The HTTP Access-Control-Allow-Origin header field name. */
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    /** The HTTP Access-Control-Allow-Credentials header field name. */
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    /** The HTTP Access-Control-Expose-Headers header field name. */
    public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    /** The HTTP Access-Control-Max-Age header field name. */
    public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    /** The HTTP Age header field name. */
    public static final String AGE = "Age";
    /** The HTTP Allow header field name. */
    public static final String ALLOW = "Allow";
    /** The HTTP Content-Disposition header field name. */
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    /** The HTTP Content-Encoding header field name. */
    public static final String CONTENT_ENCODING = "Content-Encoding";
    /** The HTTP Content-Language header field name. */
    public static final String CONTENT_LANGUAGE = "Content-Language";
    /** The HTTP Content-Location header field name. */
    public static final String CONTENT_LOCATION = "Content-Location";
    /** The HTTP Content-MD5 header field name. */
    public static final String CONTENT_MD5 = "Content-MD5";
    /** The HTTP Content-Range header field name. */
    public static final String CONTENT_RANGE = "Content-Range";
    /** The HTTP ETag header field name. */
    public static final String ETAG = "ETag";
    /** The HTTP Expires header field name. */
    public static final String EXPIRES = "Expires";
    /** The HTTP Last-Modified header field name. */
    public static final String LAST_MODIFIED = "Last-Modified";
    /** The HTTP Link header field name. */
    public static final String LINK = "Link";
    /** The HTTP Location header field name. */
    public static final String LOCATION = "Location";
    /** The HTTP P3P header field name. Limited browser support. */
    public static final String P3P = "P3P";
    /** The HTTP Proxy-Authenticate header field name. */
    public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";
    /** The HTTP Refresh header field name. Non-standard header supported by most browsers. */
    public static final String REFRESH = "Refresh";
    /** The HTTP Retry-After header field name. */
    public static final String RETRY_AFTER = "Retry-After";
    /** The HTTP Server header field name. */
    public static final String SERVER = "Server";
    /** The HTTP Set-Cookie header field name. */
    public static final String SET_COOKIE = "Set-Cookie";
    /** The HTTP Set-Cookie2 header field name. */
    public static final String SET_COOKIE2 = "Set-Cookie2";
    /** The HTTP Trailer header field name. */
    public static final String TRAILER = "Trailer";
    /** The HTTP Transfer-Encoding header field name. */
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";
    /** The HTTP Vary header field name. */
    public static final String VARY = "Vary";
    /** The HTTP WWW-Authenticate header field name. */
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";



}
