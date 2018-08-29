package http;

import com.google.gson.JsonObject;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.List;

public class Post extends HttpMethod {

    private Post(PostBuilder builder){
        this.url = builder.url;
        this.headers = builder.headers;
        this.queryParams = builder.queryParams;
        this.dataParams = builder.dataParams;
        this.encodeQueryParams = builder.encodeQueryParams;
        this.encodeDataParams = builder.encodeDataParams;
        this.httpClient = builder.httpClient;
        this.httpClientContext = builder.httpClientContext;
        this.contentType = builder.contentType;
        this.jsonObject = builder.jsonObject;

        processArguments();
    }

    private void processArguments() {
        endodedQueryParams = toURLEncodedString(queryParams,false,encodeQueryParams);

        if(contentType == ContentType.APPLICATION_FORM_URLENCODED){
            endodedDataParams = toURLEncodedString(dataParams,true,encodeQueryParams);
        }
        else if(contentType == ContentType.APPLICATION_JSON){
            endodedDataParams = jsonObject.toString();
        }
        else{
            throw new IllegalArgumentException("Invalid content type " + contentType + " is not supported");
        }


    }

    public String doPost() throws IOException {
        HttpResponse response = doHttpPost();
        return printResponse(response);
    }

    public String doCompletePost() throws IOException {
        HttpResponse response = doHttpPost();
        return printCompleteResponse(response);
    }

    private HttpResponse doHttpPost() throws IOException  {
        HttpPost httppost = new HttpPost(url + endodedQueryParams);
        httppost.setEntity(new StringEntity(endodedDataParams, contentType));

        headers.forEach(httppost::addHeader);
        return httpClient.execute(httppost, httpClientContext);
    }

    public boolean addCookie(Cookie cookie) {
        if(httpClientContext == null){
            return false;
        }

        httpClientContext.getCookieStore().addCookie(cookie);
        return true;
    }

    public static class PostBuilder{
        private String url;
        private List<Header> headers;
        private List<NameValuePair> queryParams;
        private List<NameValuePair> dataParams;
        private boolean encodeQueryParams;
        private boolean encodeDataParams;
        private HttpClient httpClient;
        private HttpClientContext httpClientContext;
        private ContentType contentType;
        private JsonObject jsonObject;

        public PostBuilder(){
            encodeDataParams = true;
            encodeQueryParams = true;
        }

        public PostBuilder url(String url){
            this.url = url;
            return this;
        }

        public PostBuilder headers(List<Header> headers){
            this.headers = headers;
            return this;
        }

        public PostBuilder queryParams(List<NameValuePair> queryParams){
            this.queryParams = queryParams;
            return this;
        }

        public PostBuilder dataParams(List<NameValuePair> dataParams){
            this.dataParams = dataParams;
            return this;
        }


        public PostBuilder encodeQueryParams(boolean encodeQueryParams){
            this.encodeQueryParams = encodeQueryParams;
            return this;
        }

        public PostBuilder encodeDataParams(boolean encodeDataParams){
            this.encodeDataParams = encodeDataParams;
            return this;
        }

        public PostBuilder httpClient(HttpClient httpClient){
            this.httpClient = httpClient;
            return this;
        }

        public PostBuilder jsonObject(JsonObject jsonObject){
            this.jsonObject = jsonObject;
            return this;
        }

        public PostBuilder httpClientContext(HttpClientContext httpClientContext){
            this.httpClientContext = httpClientContext;
            return this;
        }

        public PostBuilder contentType(ContentType contentType){
            this.contentType = contentType;
            return this;
        }

        public Post build() {
            if(url == null){
                throw new IllegalArgumentException("Url must not be null");
            }

            if(dataParams != null && contentType == null)
                throw new IllegalArgumentException("Data parameter is specified but its content type is not");

            if(httpClient == null){
                httpClient = createDefaultClient();
            }

            if(contentType == ContentType.APPLICATION_FORM_URLENCODED && dataParams == null){
                throw new IllegalArgumentException("Data parameter is specified but its content type is not set");
            }

            if(contentType == ContentType.APPLICATION_JSON && jsonObject == null){
                throw new IllegalArgumentException("Data parameter is specified but its content type is not");
            }

            return new Post(this);
        }

    }



}
