package com.slib.http;

import android.util.Log;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class HttpRequestUtil {
    private static OkHttpClient mOkHttpClient;
    private static HttpConfig mHttpConfig;
    private static final long DEFAULT_FILE_UPLOAD_READ_TIME_OUT = 30000;
    private static final long DEFAULT_FILE_UPLOAD_WRITE_TIME_OUT = 30000;

    public static void init(HttpConfig httpConfig) {
        mHttpConfig = httpConfig;
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(mHttpConfig.mConnectTimeOut, TimeUnit.MILLISECONDS).readTimeout(mHttpConfig.mReadTimeOut, TimeUnit.MILLISECONDS).writeTimeout(mHttpConfig.mWriteTimeOut, TimeUnit.MILLISECONDS);
//        if (!TextUtils.isEmpty(httpConfig.mTrustHostName)) {
        //限定证书
//            SSLSocketFactory sslSocketFactory = getSSLSocketFactory(context, httpConfig.mSslCerAssetName);
//            if (sslSocketFactory != null)
//                builder.sslSocketFactory(sslSocketFactory);
//            builder.sslSocketFactory(createSSLSocketFactory()).hostnameVerifier(new TrustAllHostnameVerifier(httpConfig.mTrustHostName));
//          信任所有证书
//            try {
//                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//                trustStore.load(null, null);
//                SSLSocketFactoryImp ssl = new SSLSocketFactoryImp(KeyStore.getInstance(KeyStore.getDefaultType()));
//                builder.sslSocketFactory(ssl.getSSLContext().getSocketFactory(), ssl.getTrustManager())
//                        .hostnameVerifier(new TrustAllHostnameVerifier(httpConfig.mTrustHostName));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

//        }
        mOkHttpClient = builder.build();
    }

    public static HttpConfig getHttpConfig() {
        return mHttpConfig;
    }

    private static HashMap<String, String> getDefaultHeader() {
        if (mHttpConfig != null && mHttpConfig.mRequestAssistHandler != null)
            return mHttpConfig.mRequestAssistHandler.getDefaultHeader();
        return null;
    }

    public static IRequestAssistHandler getRequestResultWrapper() {
        if (mHttpConfig != null)
            return mHttpConfig.mRequestAssistHandler;
        return null;
    }

    public static <T> void doHttpGet(String url, Class<T> classZZ, final INetworkCallBack<T> mCallBack) {
        doHttpGet(url, null, classZZ, mCallBack);
    }


    public static <T> void doHttpGet(String url, HashMap<String, String> header, Class<T> classZZ, final INetworkCallBack<T> mCallBack) {
        mOkHttpClient.newCall(getDefaultBuild(url, header).get().build()).enqueue(new UIResultCallBack<>(url, classZZ, mCallBack, mHttpConfig));
    }

    public static <T> Call doHttpPost(String url, HashMap<String, String> param, HashMap<String, String> header, Class<T> classZZ, final INetworkCallBack<T> mCallBack) {
        return doHttpPost(url, param, header, classZZ, 0, 0, mCallBack);
    }

    public static <T> Call doHttpPost(String url, HashMap<String, String> param, HashMap<String, String> header, Class<T> classZZ, long readTimeOut, long writeTimeOut, final INetworkCallBack<T> mCallBack) {
        Call call = getSingleTimeOutClient(readTimeOut, writeTimeOut).newCall(getDefaultPostBuild(url, header, param).build());
        call.enqueue(new UIResultCallBack<>(url, classZZ, mCallBack, mHttpConfig));
        return call;
    }

    public static <T> void doHttpPostJson(String url, String jsonParam, HashMap<String, String> header, Class<T> classZZ, final INetworkCallBack<T> mCallBack) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParam);
        mOkHttpClient.newCall(getDefaultBuild(url, header).post(body).build()).enqueue(new UIResultCallBack<>(url, classZZ, mCallBack, mHttpConfig));
    }


    public static <T> Call doHttpPost(String url, HashMap<String, String> param, Class<T> classZZ, final INetworkCallBack<T> mCallBack) {
        return doHttpPost(url, param, null, classZZ, mCallBack);
    }

    public static <T> Call doHttpPost(String url, HashMap<String, String> param, Class<T> classZZ, final INetworkCallBack<T> mCallBack, HashMap<String, String> header) {
        return doHttpPost(url, param, header, classZZ, mCallBack);
    }

    public static <T> void doHttpWithFiles(String url, long readTimeOut, long writeTimeOut, List<File> fileList, List<String> names, HashMap<String, String> param, HashMap<String, String> headers, Class<T> classZZ, final INetworkCallBack<T> mCallBack) {
        Request.Builder builder = getDefaultBuild(url, headers);
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (param != null && !param.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = param.entrySet().iterator();
            Map.Entry<String, String> entry;
            while (iterator.hasNext()) {
                entry = iterator.next();
                if (entry.getKey() != null && entry.getValue() != null)
                    multipartBodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        if (fileList != null && !fileList.isEmpty()) {
            RequestBody fileBody;
            File file;
            for (int i = 0, size = fileList.size(); i < size; i++) {
                file = fileList.get(i);
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(file.getName())), file);
                multipartBodyBuilder.addFormDataPart(((names != null && !names.isEmpty()) ? names.get(i) : "file"), file.getName(), fileBody);
            }
        }
        getSingleTimeOutClient(readTimeOut, writeTimeOut).newCall(builder.post(multipartBodyBuilder.build()).build()).enqueue(new UIResultCallBack<>(url, classZZ, mCallBack, mHttpConfig));
    }

    public static <T> void doHttpWithFiles(String url, List<File> fileList, List<String> names, HashMap<String, String> param, Class<T> classZZ, final INetworkCallBack<T> mCallBack) {
        doHttpWithFiles(url, DEFAULT_FILE_UPLOAD_READ_TIME_OUT, DEFAULT_FILE_UPLOAD_WRITE_TIME_OUT, fileList, names, param, null, classZZ, mCallBack);
    }

    static <T> void downLoadFile(String url, HashMap<String, String> header, long readTimeOut, long writeTimeOut, FileLoadResultCallBack<T> callBack) {
        getSingleTimeOutClient(readTimeOut, writeTimeOut).newCall(getDefaultBuild(url, header).build()).enqueue(callBack);
    }

    public static OkHttpClient getSingleTimeOutClient(long readTimeOut, long writeTimeOut) {
        if (readTimeOut == 0 && writeTimeOut == 0)
            return mOkHttpClient;
        return mOkHttpClient.newBuilder().readTimeout(readTimeOut, TimeUnit.MILLISECONDS).writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS).build();
    }

    private static String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    public static class TBuilder extends Request.Builder {
        @Override
        public Request.Builder url(String url) {
            return super.url(url);
        }
    }

    public static Request.Builder getDefaultBuild(String url, HashMap<String, String> header) {
        Request.Builder builder = new Request.Builder().url(url);
        if (header == null)
            header = getDefaultHeader();
        if (header != null && !header.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = header.entrySet().iterator();
            Map.Entry<String, String> entry;
            while (iterator.hasNext()) {
                entry = iterator.next();
                if (entry.getKey() != null && entry.getValue() != null) {
                    builder.addHeader(entry.getKey(), entry.getValue());
                    //Log.e("herb", "头文件信息>>>" + entry.getKey() + ":" + entry.getValue());
                }
            }
        }
        return builder;
    }

    public static Request.Builder getDefaultPostBuild(String url, HashMap<String, String> header, HashMap<String, String> param) {
        Request.Builder builder = getDefaultBuild(url, header);
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (param != null && !param.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = param.entrySet().iterator();
            Map.Entry<String, String> entry;
            while (iterator.hasNext()) {
                entry = iterator.next();
                if (entry.getKey() != null && entry.getValue() != null) {
                    formBodyBuilder.add(entry.getKey(), entry.getValue());
                    //Log.e("herb", "参数信息>>>" + entry.getKey() + ":" + entry.getValue());
                }
            }
        }
        builder.post(formBodyBuilder.build());
        return builder;
    }

//    private static SSLSocketFactory getSSLSocketFactory(Context context, String name) {
//        //CertificateFactory用来证书生成
//        CertificateFactory certificateFactory;
//        InputStream inputStream = null;
//        Certificate certificate;
//        try {
//            inputStream = context.getResources().getAssets().open(name);
//            certificateFactory = CertificateFactory.getInstance("X.509");
//            certificate = certificateFactory.generateCertificate(inputStream);
//            //Create a KeyStore containing our trusted CAs
//            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            keyStore.load(null, null);
//            keyStore.setCertificateEntry(name, certificate);
//            //Create a TrustManager that trusts the CAs in our keyStore
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            trustManagerFactory.init(keyStore);
//            //Create an SSLContext that uses our TrustManager
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
//            return sslContext.getSocketFactory();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (inputStream != null)
//                try {
//                    inputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//        }
//        return null;
//    }

//    private static SSLSocketFactory createSSLSocketFactory() {
//        SSLSocketFactory sSLSocketFactory = null;
//        try {
//            SSLContext sc = SSLContext.getInstance("TLS");
//            sc.init(null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());
//            sSLSocketFactory = sc.getSocketFactory();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return sSLSocketFactory;
//    }
//
//    private static class TrustAllManager implements X509TrustManager {
//        @Override
//        public void checkClientTrusted(X509Certificate[] chain, String authType)
//                throws CertificateException {
//        }
//
//        @Override
//        public void checkServerTrusted(X509Certificate[] chain, String authType)
//                throws CertificateException {
//        }
//
//        @Override
//        public X509Certificate[] getAcceptedIssuers() {
//            return new X509Certificate[0];
//        }
//    }

//    private static class SSLSocketFactoryImp extends SSLSocketFactory {
//        private SSLContext sslContext = SSLContext.getInstance("TLS");
//        private TrustManager trustManager;
//
//        SSLContext getSSLContext() {
//            return sslContext;
//        }
//
//        X509TrustManager getTrustManager() {
//            return (X509TrustManager) trustManager;
//        }
//
//        SSLSocketFactoryImp(KeyStore keyStore) throws NoSuchAlgorithmException, KeyManagementException {
//            trustManager = new X509TrustManager() {
//                @Override
//                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
//
//                }
//
//                @Override
//                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
//                    if (x509Certificates == null || x509Certificates.length <= 0)
//                        throw new IllegalArgumentException("x509Certificates is  null");
//                    for (X509Certificate x509Certificate : x509Certificates) {
//                        if (x509Certificate != null && x509Certificate.getSubjectDN() != null)
//                            Log.d("HttpRequestUtil", x509Certificate.getSubjectDN().getName());
//                        if (x509Certificate == null || x509Certificate.getSubjectDN() == null || x509Certificate.getSubjectDN().getName() == null || x509Certificate.getSubjectDN().getName().contains("O=DO_NOT_TRUST"))
//                            throw new IllegalArgumentException("Certificates DO_NOT_TRUST");
//                    }
//                }
//
//                @Override
//                public X509Certificate[] getAcceptedIssuers() {
//                    return new X509Certificate[0];
//                }
//            };
//            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
//        }
//
//        @Override
//        public String[] getDefaultCipherSuites() {
//            return new String[0];
//        }
//
//        @Override
//        public String[] getSupportedCipherSuites() {
//            return new String[0];
//        }
//
//        @Override
//        public Socket createSocket() throws IOException {
//            return sslContext.getSocketFactory().createSocket();
//        }
//
//        @Override
//        public Socket createSocket(Socket socket, String host, int post, boolean autoClose) throws IOException {
//            return sslContext.getSocketFactory().createSocket(socket, host, post, autoClose);
//        }
//
//        @Override
//        public Socket createSocket(String s, int i) throws IOException, UnknownHostException {
//            return null;
//        }
//
//        @Override
//        public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
//            return null;
//        }
//
//        @Override
//        public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
//            return null;
//        }
//
//        @Override
//        public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
//            return null;
//        }
//    }
//
//    private static class TrustAllHostnameVerifier implements HostnameVerifier {
//        private String trustHostName;
//
//        TrustAllHostnameVerifier(String trustHostName) {
//            this.trustHostName = trustHostName;
//            if (this.trustHostName != null && this.trustHostName.startsWith("https://"))
//                this.trustHostName = trustHostName.replace("https://", "");
//        }
//
//        @Override
//        public boolean verify(String hostname, SSLSession session) {
//            HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
//            return (trustHostName != null && hv.verify(trustHostName, session));
//        }
//    }
}
