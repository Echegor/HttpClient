package http;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;

import java.io.IOException;
import java.util.List;

public class Get extends HttpMethod {

    private Get(GetBuilder builder){
        this.url = builder.url;
        this.headers = builder.headers;
        this.queryParams = builder.queryParams;
        this.encodeQueryParams = builder.encodeQueryParams;
        this.httpClient = builder.httpClient;
        this.httpClientContext = builder.httpClientContext;

        processArguments();
    }

    private void processArguments() {
        endodedQueryParams = toURLEncodedString(queryParams,false,encodeQueryParams);
        endodedDataParams = toURLEncodedString(dataParams,true,encodeQueryParams);
    }

    public String doGet() throws IOException {
        HttpResponse response = doHttpGet();
        return printResponse(response);
    }

    public String doCompleteGet() throws IOException {
        HttpResponse response = doHttpGet();
        return printCompleteResponse(response);
    }

    private HttpResponse doHttpGet() throws IOException{
        HttpGet httpGet = new HttpGet(url + endodedQueryParams);
        headers.forEach(httpGet::addHeader);

        return httpClient.execute(httpGet, httpClientContext);
    }

    public boolean addCookie(Cookie cookie) {
        if(httpClientContext == null){
            return false;
        }

        httpClientContext.getCookieStore().addCookie(cookie);
        return true;
    }

    public static class GetBuilder{
        private String url;
        private List<Header> headers;
        private List<NameValuePair> queryParams;
        private boolean encodeQueryParams;
        private HttpClient httpClient;
        private HttpClientContext httpClientContext;

        public GetBuilder(){
            encodeQueryParams = true;
        }

        public GetBuilder url(String url){
            this.url = url;
            return this;
        }

        public GetBuilder headers(List<Header> headers){
            this.headers = headers;
            return this;
        }

        public GetBuilder queryParams(List<NameValuePair> queryParams){
            this.queryParams = queryParams;
            return this;
        }

        public GetBuilder encodeQueryParams(boolean encodeQueryParams){
            this.encodeQueryParams = encodeQueryParams;
            return this;
        }

        public GetBuilder httpClient(HttpClient httpClient){
            this.httpClient = httpClient;
            return this;
        }

        public GetBuilder httpClientContext(HttpClientContext httpClientContext){
            this.httpClientContext = httpClientContext;
            return this;
        }

        public Get build() {
            if(url == null){
                throw new IllegalArgumentException("Url must not be null");
            }

            if(httpClient == null){
                httpClient = createDefaultClient();
            }

            return new Get(this);
        }

    }



}
