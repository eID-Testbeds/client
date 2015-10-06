package com.secunet.ipsmall.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.UUID;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import com.secunet.ipsmall.http.NanoHTTPD.Response.Status;
import com.secunet.ipsmall.log.IModuleLogger.ConformityResult;
import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.log.IModuleLogger.ProtocolDirection;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.log.Protocols;
import com.secunet.ipsmall.test.ITestData;
import com.secunet.ipsmall.test.ITestProtocolCallback.TestEvent;
import com.secunet.ipsmall.tls.BouncyCastleSocket;
import com.secunet.ipsmall.util.CommonUtil;

/**
 * A simple, tiny, nicely embeddable HTTP server in Java
 * <p/>
 * <p/>
 * NanoHTTPD
 * <p>
 * </p>
 * Copyright (c) 2012-2013 by Paul S. Hawke, 2001,2005-2013 by Jarno Elonen, 2010 by Konstantinos Togias</p>
 * <p/>
 * <p/>
 * <b>Features + limitations: </b>
 * <ul>
 * <p/>
 * <li>Only one Java file</li>
 * <li>Java 5 compatible</li>
 * <li>Released as open source, Modified BSD licence</li>
 * <li>No fixed config files, logging, authorization etc. (Implement yourself if you need them.)</li>
 * <li>Supports parameter parsing of GET and POST methods (+ rudimentary PUT support in 1.25)</li>
 * <li>Supports both dynamic content and file serving</li>
 * <li>Supports file upload (since version 1.2, 2010)</li>
 * <li>Supports partial content (streaming)</li>
 * <li>Supports ETags</li>
 * <li>Never caches anything</li>
 * <li>Doesn't limit bandwidth, request time or simultaneous connections</li>
 * <li>Default code serves files and shows all HTTP parameters and headers</li>
 * <li>File server supports directory listing, index.html and index.htm</li>
 * <li>File server supports partial content (streaming)</li>
 * <li>File server supports ETags</li>
 * <li>File server does the 301 redirection trick for directories without '/'</li>
 * <li>File server supports simple skipping for files (continue download)</li>
 * <li>File server serves also very long files without memory overhead</li>
 * <li>Contains a built-in list of most common mime types</li>
 * <li>All header names are converted lowercase so they don't vary between browsers/clients</li>
 * <p/>
 * </ul>
 * <p/>
 * <p/>
 * <b>How to use: </b>
 * <ul>
 * <p/>
 * <li>Subclass and implement serve() and embed to your own program</li>
 * <p/>
 * </ul>
 * <p/>
 * See the separate "LICENSE.md" file for the distribution license (Modified BSD licence)
 */
public abstract class NanoHTTPD {
	/**
	* Maximum time to wait on Socket.getInputStream().read() (in milliseconds)
	* This is required as the Keep-Alive HTTP connections would otherwise
	* block the socket reading thread forever (or as long the browser is open).
	*/
	public static final int SOCKET_READ_TIMEOUT = 60000;
    /**
     * Common mime type for dynamic content: plain text
     */
    public static final String MIME_PLAINTEXT = "text/plain";
    /**
     * Common mime type for dynamic content: SOAP messages
     */
    public static final String c_SOAP_MIME_TYPE = "application/soap+xml; charset=utf-8";
    /**
     * Common mime type for dynamic content: html
     */
    public static final String MIME_HTML = "text/html";
    
    private static final byte[] HTTP_BODY_SEPERATOR = new byte[] { '\r', '\n', '\r', '\n' };
    private static final byte[] HTTP_BODY_SEPERATOR_NON_CONFORM = new byte[] { '\n', '\n' };
    /**
     * Common mime type for dynamic content: binary
     */
    public static final String MIME_DEFAULT_BINARY = "application/octet-stream";
    
    protected Logger logger = Logger.HTTPServer;
    
    protected boolean doSuppressTLSErrors = false;
    
    protected final String hostname;
    protected final int myPort;
    protected final List<String> protocols;
    protected final List<String> cipherSuites;
    protected ServerSocket myServerSocket;
    private Set<Socket> openConnections = new HashSet<Socket>();
    protected Thread myThread;
    
    protected ExternalServerSocketFactory externalServerSocketFactory = null;
    
    /**
     * Pseudo-Parameter to use to store the actual query string in the parameters map for later re-processing.
     */
    public static final String QUERY_STRING_PARAMETER = "NanoHttpd.QUERY_STRING";
    
    protected ITestData testData;
    
    private boolean isHTTP11LineSeparator = true;
    private boolean containsHTTP11Version = true;
    private final String http11 = "HTTP/1.1";
    private Boolean http11GlobalConform = null;
  
    
    /**
     * Constructs an HTTP server on given port.
     */
    public NanoHTTPD(int port) {
        this(null, port, null, null, null);
    }
    
    /**
     * Constructs an HTTP server on given hostname and port.
     */
    public NanoHTTPD(String hostname, int port) {
        this(hostname, port, null, null, null);
    }
    
    public NanoHTTPD(int port, ExternalServerSocketFactory factory) {
        this(null, port, factory, null, null);
    }
    
    /**
     * Constructs an HTTP server on given hostname and port.
     */
    public NanoHTTPD(String hostname, int port, ExternalServerSocketFactory factory, List<String> protocols, List<String> cipherSuites) {
        
        this.hostname = hostname;
        this.myPort = port;
        this.protocols = protocols;
        this.cipherSuites = cipherSuites;
        
        setAsyncRunner(new DefaultAsyncRunner(this.getClass().getSimpleName()));
        externalServerSocketFactory = factory;
    }
    
    /**
     * Start the server.
     * 
     * @throws Exception
     */
    public void start() throws Exception {
        
        if (externalServerSocketFactory != null) {
            myServerSocket = externalServerSocketFactory.create(hostname, myPort, protocols, cipherSuites);
            
            if (myServerSocket instanceof javax.net.ssl.SSLServerSocket) {
                javax.net.ssl.SSLServerSocket sslSocket = (javax.net.ssl.SSLServerSocket) myServerSocket;
                
                logger.logState("Server-enabled Protocols: "
                        + CommonUtil.arrayToCommaSeparatedString(Java7NanoHTTPSocketFactory.toProtocolEnumNames(sslSocket.getEnabledProtocols())));
                logger.logState("Server-enabled CipherSuites: " + CommonUtil.arrayToCommaSeparatedString(sslSocket.getEnabledCipherSuites()));
                
            } else if (myServerSocket instanceof ServerSocket) {
            	// EIDCLIENTC-258: add http in addition to https
            	logger.logState("HTTP Server Socket added.");
            }
            else {
                logger.logState("Unkown Server Socket instance", LogLevel.Warn);
            }
            
        } else {
        	// check if the modified ssl library shall be used
        	if(!testData.useModifiedSSL()) {
        		myServerSocket = new ServerSocket();
        		myServerSocket.bind((hostname != null) ? new InetSocketAddress(hostname, myPort) : new InetSocketAddress(myPort));
        	} else {
        		myServerSocket = null;
        	}
        }
        
        // no external server is used (e.g. openssl)
        if (myServerSocket != null) {
        	myThread = new Thread(new Runnable() {
                @SuppressWarnings("unused")
                @Override
                public void run() {
                    do {
                        try {
                            logger.logState("calling socket accept (block)");
                            String address = hostname + ":" + myPort;
                            final Socket finalAccept = myServerSocket.accept();
                            registerConnection(finalAccept);
                            finalAccept.setSoTimeout(SOCKET_READ_TIMEOUT);
                            logger.logState("socket accepted, type: " + finalAccept.getClass().getName());
                            if (finalAccept instanceof SSLSocket) {
                                SSLSocket sslSocket = ((SSLSocket) finalAccept);
                                
                                // if we ask for cipherSuites/protocols here, we get the
                                // enabled cipherSuites/protocols for the connection, i.e.
                                // those set by server, even if we call the method of the
                                // client socket as below.
                                // Therefore, this is for debugging only, but this was
                                // already logged on server-startup
                                String[] enabledCipherSuites = sslSocket.getEnabledCipherSuites();
                                String[] enabledProtocols = sslSocket.getEnabledProtocols();
                                
                                logger.logState("SSL socket accepted, starting handshake");
                                sslSocket.startHandshake();
                                // sslSocket.addHandshakeCompletedListener(listener);
                                // // would be possible, but not available for iaik
                                // anyway
                                // SSLSession handshakeSession =
                                // sslSocket.getHandshakeSession();
                                SSLSession sslSession = sslSocket.getSession();
                                
                                boolean valid = sslSession.isValid();
                                logger.logState("TLS handshake done. Session-valid: " + valid);
                                testData.sendMessageToCallbacks(TestEvent.TLS_HANDSHAKE_DONE, address, null, getServerImplementation());
                                
                                // after the TLS session is established, do log the
                                // agreed cipherSuites/protocols:
                                String agreedCipherSuite = sslSession.getCipherSuite();
                                String agreedProtocol = sslSession.getProtocol();
                                logger.logState("server-client-agreed protocol after TLS-handshake: "
                                        + CommonUtil.arrayToCommaSeparatedString(Java7NanoHTTPSocketFactory.toProtocolEnumNames(new String[] { agreedProtocol })));
                                logger.logState("server-client-agreed cipherSuite after TLS-handshake: " + agreedCipherSuite);
                                
                                // for debugging: diff of list of enabled cipherSuites compared to initial list
                                enabledCipherSuites = sslSocket.getEnabledCipherSuites();
                                
                            } else if (finalAccept instanceof BouncyCastleSocket) {
                                BouncyCastleSocket sslSocket = ((BouncyCastleSocket) finalAccept);
                                sslSocket.startHandshake();
                                testData.sendMessageToCallbacks(TestEvent.TLS_HANDSHAKE_DONE, address, null, getServerImplementation());
                            }
                            
                            final InputStream inputStream = finalAccept.getInputStream();
                            if (inputStream == null) {
                                logger.logState("inputStream null.");
                                logger.logState("Closing ClientSocket");
                                safeClose(finalAccept);
                            } else {
                                logger.logState("inputStream not-null.", LogLevel.Debug );
                                logger.logState("inputStream available: " + inputStream.available(), LogLevel.Debug);
                                asyncRunner.exec(new Runnable() {
                                    @Override
                                    public void run() {

                                    	OutputStream outputStream = null;
                                        try {
                                            outputStream = finalAccept.getOutputStream();
                                            
                                            HTTPSession session = new HTTPSession(finalAccept, inputStream, outputStream, logger);
                                            while (!finalAccept.isClosed()) {
                                                session.execute();
                                            }
                                        } catch (Exception e) {
                                            // When the socket is closed by the
                                            // client, we throw our own
                                            // SocketException
                                            // to break the "keep alive" loop above.
                                            if (!(e instanceof SocketException && "NanoHttpd Shutdown".equals(e.getMessage()))) {
                                                logger.logException(e);
                                            }
                                        } finally {
                                            logger.logState("Closing ClientSocket");
                                            safeClose(outputStream);
                                            safeClose(inputStream);
                                            safeClose(finalAccept);
                                            unRegisterConnection(finalAccept);
                                        }
                                    }
                                });
                            }
                        } catch (SocketException e) {
                            logger.logState(e.getMessage(), LogLevel.Error);
                        } catch (IOException e) {
                            logger.logException(e);
                        }
                    } while (!myServerSocket.isClosed());
                }

            });
            myThread.setDaemon(true);
            myThread.setName("NanoHttpd Main Listener (" + getServerImplementationName() + ")");
            myThread.start();
        }
        
        logger.logState(getServerImplementationName() + " started.");
    }
    
    /**
     * Stop the server.
     */
    public void stop() {
        try {
        	if(!testData.disableHTTP11ConformityTest() && null != http11GlobalConform) {
        	    if (http11GlobalConform.booleanValue())
        	        logger.logConformity(ConformityResult.passed, "The client exclusively communicated using HTTP/1.1 compliant messages");
        	    else
        	        logger.logConformity(ConformityResult.failed, "The client sent at least one non-HTTP/1.1 compliant message", LogLevel.Error);
        	}
            safeClose(myServerSocket);
            closeAllConnections();
            if (myThread != null) {
                myThread.join(10 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Registers that a new connection has been set up.
     *
     * @param socket the {@link Socket} for the connection.
     */
    public synchronized void registerConnection(Socket socket) {
        openConnections.add(socket);
    }

    /**
     * Registers that a connection has been closed
     *
     * @param socket
     *            the {@link Socket} for the connection.
     */
    public synchronized void unRegisterConnection(Socket socket) {
        openConnections.remove(socket);
    }

    /**
     * Forcibly closes all connections that are open.
     */
    public synchronized void closeAllConnections() {
        for (Socket socket : openConnections) {
            safeClose(socket);
        }
    }
    
    public final int getListeningPort() {
    	return myServerSocket == null ? -1 : myServerSocket.getLocalPort();
    }

    public final boolean wasStarted() {
    	return myServerSocket != null && myThread != null;
    }
    
    public final boolean isAlive() {
    		return wasStarted() && !myServerSocket.isClosed() && myThread.isAlive();
    }
    
    /**
     * Override this to customize the server.
     * <p/>
     * <p/>
     * (By default, this delegates to serveFile() and allows directory listing.)
     * 
     * @param session
     *            The HTTP session
     * @return HTTP response, see class Response for details
     */
    public abstract Response serve(HTTPSession httpReq);
    
    /**
     * Decode percent encoded <code>String</code> values.
     * 
     * @param str
     *            the percent encoded <code>String</code>
     * @return expanded form of the input, for example "foo%20bar" becomes "foo bar"
     */
    protected String decodePercent(String str) {
        String decoded = null;
        try {
            decoded = URLDecoder.decode(str, "UTF8");
        } catch (UnsupportedEncodingException ignored) {
        }
        return decoded;
    }
    
    /**
     * Decode parameters from a URL, handing the case where a single parameter name might have been supplied several times, by return lists of values. In
     * general these lists will contain a single element.
     * 
     * @param parms
     *            original <b>NanoHttpd</b> parameters values, as passed to the <code>serve()</code> method.
     * @return a map of <code>String</code> (parameter name) to <code>List&lt;String&gt;</code> (a list of the values supplied).
     */
    protected Map<String, List<String>> decodeParameters(Map<String, String> parms) {
        return this.decodeParameters(parms.get(QUERY_STRING_PARAMETER));
    }
    
    /**
     * Decode parameters from a URL, handing the case where a single parameter name might have been supplied several times, by return lists of values. In
     * general these lists will contain a single element.
     * 
     * @param queryString
     *            a query string pulled from the URL.
     * @return a map of <code>String</code> (parameter name) to <code>List&lt;String&gt;</code> (a list of the values supplied).
     */
    protected Map<String, List<String>> decodeParameters(String queryString) {
        Map<String, List<String>> parms = new HashMap<String, List<String>>();
        if (queryString != null) {
            StringTokenizer st = new StringTokenizer(queryString, "&");
            while (st.hasMoreTokens()) {
                String e = st.nextToken();
                int sep = e.indexOf('=');
                String propertyName = (sep >= 0) ? decodePercent(e.substring(0, sep)).trim() : decodePercent(e).trim();
                if (!parms.containsKey(propertyName)) {
                    parms.put(propertyName, new ArrayList<String>());
                }
                String propertyValue = (sep >= 0) ? decodePercent(e.substring(sep + 1)) : null;
                if (propertyValue != null) {
                    parms.get(propertyName).add(propertyValue);
                }
            }
        }
        return parms;
    }
    
    /**
     * HTTP Request methods, with the ability to decode a <code>String</code> back to its enum value.
     */
    public enum Method {
        GET,
        PUT,
        POST,
        DELETE,
        HEAD;
        
        static Method lookup(String method) {
            for (Method m : Method.values()) {
                if (m.toString().equalsIgnoreCase(method)) {
                    return m;
                }
            }
            return null;
        }
    }
    
    // -------------------------------------------------------------------------------
    // //
    //
    // Threading Strategy.
    //
    // -------------------------------------------------------------------------------
    // //
    
    /**
     * Pluggable strategy for asynchronously executing requests.
     */
    private AsyncRunner asyncRunner;
    
    /**
     * Pluggable strategy for asynchronously executing requests.
     * 
     * @param asyncRunner
     *            new strategy for handling threads.
     */
    public void setAsyncRunner(AsyncRunner asyncRunner) {
        this.asyncRunner = asyncRunner;
    }
    
    /**
     * Pluggable strategy for asynchronously executing requests.
     */
    public interface AsyncRunner {
        void exec(Runnable code);
    }
    
    /**
     * Default threading strategy for NanoHttpd.
     * 
     * <p>
     * By default, the server spawns a new Thread for every incoming request. These are set to <i>daemon</i> status, and named according to the request number.
     * The name is useful when profiling the application.
     * </p>
     */
    public static class DefaultAsyncRunner implements AsyncRunner {
        private long requestCount;
        private String baseClassName = "";
        
        public DefaultAsyncRunner(String baseClassname) {
            baseClassName = baseClassname;
        }
        
        @Override
        public void exec(Runnable code) {
            ++requestCount;
            Thread t = new Thread(code);
            t.setDaemon(true);
            t.setName(baseClassName + " NanoHttpd RP(#" + requestCount + ")");
            t.start();
        }
    }
    
    /**
     * HTTP response. Return one of these from serve().
     */
    public static class Response {
        /**
         * HTTP status code after processing, e.g. "200 OK", HTTP_OK
         */
        private Status status;
        /**
         * MIME type of content, e.g. "text/html"
         */
        private String mimeType;
        /**
         * Data of the response, may be null.
         */
        private InputStream data;
        /**
         * not really nice but use full for debugging
         */
        private String txtResponse = null;
        /**
         * Headers for the HTTP response. Use addHeader() to add lines.
         */
        private final Map<String, String> header = new HashMap<String, String>();
        /**
         * The request method that spawned this response.
         */
        private Method requestMethod;
        private int chunkSize;
        
        /**
         * Default constructor: response = HTTP_OK, mime = MIME_HTML and your supplied message
         */
        public Response(String msg, int chunkSize) {
            this(Status.OK, MIME_HTML, msg, chunkSize);
        }
        
        public Response(String msg, String mimeType, int chunkSize) {
            this(Status.OK, mimeType, msg, chunkSize);
        }
        
        public Response(Status status, String msg, int chunkSize) {
            this(status, MIME_HTML, msg, chunkSize);
        }
        
        /**
         * Basic constructor.
         */
        public Response(Status status, String mimeType, InputStream data, int chunkSize) {
            this.status = status;
            this.mimeType = mimeType;
            this.data = data;
            this.chunkSize = chunkSize;
        }
        
        /**
         * Convenience method that makes an InputStream out of given text.
         */
        public Response(Status status, String mimeType, String txt, int chunked) {
            this.status = status;
            this.mimeType = mimeType;
            this.txtResponse = txt;
            this.chunkSize = chunked;
            try {
                this.data = txt != null ? new ByteArrayInputStream(txt.getBytes("UTF-8")) : null;
            } catch (java.io.UnsupportedEncodingException uee) {
                uee.printStackTrace();
            }
        }
        
        /**
         * Adds given line to the header.
         */
        public void addHeader(String name, String value) {
            header.put(name, value);
        }
        
        /**
         * Sends given response to the socket.
         */
        private String send(OutputStream outputStream) {
            String response = null;
            String mime = mimeType;
            SimpleDateFormat gmtFrmt = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
            gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            
            try {
                if (status == null) {
                    throw new Error("sendResponse(): Status can't be null.");
                }
                
                CharArrayWriter charWriter = new CharArrayWriter();
                PrintWriter pw = new PrintWriter(charWriter);
                pw.print("HTTP/1.1 " + status.getDescription() + " \r\n");
                
                if (mime != null) {
                    pw.print("Content-Type: " + mime + "\r\n");
                }
                
                if (header == null || header.get("Date") == null) {
                    pw.print("Date: " + gmtFrmt.format(new Date()) + "\r\n");
                }
                
                if (header != null) {
                    for (String key : header.keySet()) {
                        String value = header.get(key);
                        pw.print(key + ": " + value + "\r\n");
                    }
                }
                
                prepareConnectionHeaderIfNotAlreadyPresent(pw, header);

                if (requestMethod != Method.HEAD && chunkSize != -1) {
                    response = sendAsChunked(outputStream, pw, charWriter);
                } else {
                    int pending = data != null ? data.available() : 0;
                    prepareContentLengthHeaderIfNotAlreadyPresent(pw, header, pending);
                    pw.print("\r\n");
                    pw.flush();
                    response = charWriter.toString();
                    outputStream.write(response.getBytes());
                    response += sendAsFixedLength(outputStream, pending);
                }
                outputStream.flush();
                safeClose(data);
            } catch (IOException ioe) {
                // Couldn't write? No can do.
            }
            
            return response;
        }
        
        protected void prepareContentLengthHeaderIfNotAlreadyPresent(PrintWriter pw, Map<String, String> header, int size) {
            if (!headerAlreadySent(header, "content-length")) {
                pw.print("Content-Length: "+ size +"\r\n");
            }
        }

        protected void prepareConnectionHeaderIfNotAlreadyPresent(PrintWriter pw, Map<String, String> header) {
            if (!headerAlreadySent(header, "connection")) {
                pw.print("Connection: keep-alive\r\n");
            }
        }

        private boolean headerAlreadySent(Map<String, String> header, String name) {
            boolean alreadySent = false;
            for (String headerName : header.keySet()) {
                alreadySent |= headerName.equalsIgnoreCase(name);
            }
            return alreadySent;
        }

        private String sendAsChunked(OutputStream outputStream, PrintWriter pw, CharArrayWriter charWriter) throws IOException {
            String msg = null;
            pw.print("Transfer-Encoding: chunked\r\n");
            pw.print("\r\n");
            pw.flush();
            msg = charWriter.toString();
            outputStream.write(msg.getBytes());
            int BUFFER_SIZE = chunkSize;
            byte[] CRLF = "\r\n".getBytes();
            byte[] buff = new byte[BUFFER_SIZE];
            int read;
            while ((read = data.read(buff)) > 0) {
                msg += String.format("%x\r\n", read) + new String(buff, 0, read) + new String(CRLF);
                outputStream.write(String.format("%x\r\n", read).getBytes());
                outputStream.write(buff, 0, read);
                outputStream.write(CRLF);
            }
            outputStream.write(String.format("0\r\n\r\n").getBytes());
            
            return msg;
        }

        private String sendAsFixedLength(OutputStream outputStream, int pending) throws IOException {
            String msg = null;
            if (requestMethod != Method.HEAD && data != null) {
                msg = "";
                int BUFFER_SIZE = 16 * 1024;
                byte[] buff = new byte[BUFFER_SIZE];
                while (pending > 0) {
                    int read = data.read(buff, 0, ((pending > BUFFER_SIZE) ? BUFFER_SIZE : pending));
                    if (read <= 0) {
                        break;
                    }
                    msg += new String(buff, 0, read);
                    outputStream.write(buff, 0, read);
                    pending -= read;
                }
            }
            
            return msg;
        }
        
        public Status getStatus() {
            return status;
        }
        
        public void setStatus(Status status) {
            this.status = status;
        }
        
        public String getMimeType() {
            return mimeType;
        }
        
        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }
        
        public InputStream getData() {
            return data;
        }
        
        public void setData(InputStream data) {
            this.data = data;
        }
        
        public Method getRequestMethod() {
            return requestMethod;
        }
        
        public void setRequestMethod(Method requestMethod) {
            this.requestMethod = requestMethod;
        }
        
        public String getTxtResponse() {
            
            return txtResponse;
        }
        
        /**
         * Some HTTP response status codes
         */
        public enum Status {
            OK(
                    200,
                    "OK"),
            CREATED(
                    201,
                    "Created"),
            ACCEPTED(
                    202,
                    "Accepted"),
            NO_CONTENT(
                    204,
                    "No Content"),
            PARTIAL_CONTENT(
                    206,
                    "Partial Content"),
            REDIRECT(
                    301,
                    "Moved Permanently"),
            FOUND(
                    302,
                    "Found"),
            SEE_OTHER(
                    303,
                    "See Other"),
            NOT_MODIFIED(
                    304,
                    "Not Modified"),
            TEMPORARY_REDIRECT(
                    307,
                    "Temporary Redirect"),
            BAD_REQUEST(
                    400,
                    "Bad Request"),
            UNAUTHORIZED(
                    401,
                    "Unauthorized"),
            FORBIDDEN(
                    403,
                    "Forbidden"),
            NOT_FOUND(
                    404,
                    "Not Found"),
            RANGE_NOT_SATISFIABLE(
                    416,
                    "Requested Range Not Satisfiable"),
            INTERNAL_ERROR(
                    500,
                    "Internal Server Error");
            private final int requestStatus;
            private final String description;
            
            Status(int requestStatus, String description) {
                this.requestStatus = requestStatus;
                this.description = description;
            }
            
            public int getRequestStatus() {
                return this.requestStatus;
            }
            
            public String getDescription() {
                return "" + this.requestStatus + " " + description;
            }
        }
    }
    
    /**
     * Handles one session, i.e. parses the HTTP request and returns the response.
     */
    public class HTTPSession {
        
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private final Socket socket;
        
        private final Logger logger;
        private String uri;
        private Method method;
        private Map<String, String> params;
        private Map<String, String> headers;
        private Map<String, Object> socketInfo = new HashMap<String, Object>();
        private final UUID sessionID;
        private String rawHeader;
        private String rawContent;
        
        public HTTPSession(Socket socket, InputStream inputStream, OutputStream outputStream, Logger logger) {
            this.logger = logger;
            this.logger.logState("New HTTPSession");
            
            this.socket = socket;
            this.inputStream = inputStream;
            this.outputStream = outputStream;
            sessionID = UUID.randomUUID();
            
            if (externalServerSocketFactory != null) {
                socketInfo = externalServerSocketFactory.getAdditionalSocketInfo(socket);
            }
        }
        
        public Map<String, Object> getSocketInfo() {
            
            return socketInfo;
        }
        
        public void execute() throws IOException {
            try {
                // get header
                rawHeader = null;
                ByteArrayOutputStream incomingHeader = new ByteArrayOutputStream();
                byte[] tmpHeader = new byte[4];
                
                while (!checkHeaderEnd(tmpHeader)) {
                    byte newByte = (byte) inputStream.read();
                    
                    if (newByte == -1) {
                        throw new ResponseException(Status.BAD_REQUEST, "Bad communication", new Exception("Malformed HTTP header"));
                    }
                    
                    incomingHeader.write(newByte);
                    tmpHeader[0] = tmpHeader[1];
                    tmpHeader[1] = tmpHeader[2];
                    tmpHeader[2] = tmpHeader[3];
                    tmpHeader[3] = newByte;
                }
                
                rawHeader = new String(incomingHeader.toByteArray());
                
                // Decode the header into parms and header java properties
                decodeHeader();
                
                // get content
                rawContent = null;
                if (headers.containsKey("content-length")) {
                    int size = Integer.parseInt(headers.get("content-length"));
                    
                    byte[] tmpContent = new byte[size];
                    try {
                        inputStream.read(tmpContent, 0, size);
                    } catch (Exception e) {
                        throw new ResponseException(Status.BAD_REQUEST, "Bad communication", e);
                    }
                    rawContent = new String(tmpContent);
                }   
                
                // log incomming http message
                String reqMsg = this.getRawHeader();
                String content = this.getContent();
                if (content != null)
                    reqMsg += content;
                logRequest(reqMsg);
                
                // check for http11 conformity if the flag is set
                if (!testData.disableHTTP11ConformityTest()) {
                	HashMap<String, String> faultyHeaders = checkHTTP11Headers();
                	if (faultyHeaders.size() == 0 && containsHTTP11Version && isHTTP11LineSeparator) {
                        if(null == http11GlobalConform) http11GlobalConform = Boolean.TRUE;
                	    this.logger.logConformity(ConformityResult.passed, "Message was conform to HTTP/1.1");
                	}
                	else {
                		http11GlobalConform = Boolean.FALSE;
                		String logMessage = "Message was not conform to HTTP/1.1. Reasons:\r\n";
                		if (!isHTTP11LineSeparator) {
                			logMessage += "The line separator that was used is not conform to the HTTP sepcification.\r\n";
                		}                			
                		if (!containsHTTP11Version) {
               				logMessage += "The header field did not contain the proper version number (HTTP/1.1).\r\n";
                		}
                		if (faultyHeaders.size() > 0) {
                			logMessage += "Non-conform headers:\r\n";
                			for(String header: faultyHeaders.keySet()) {
                				logMessage += (header + ": " + faultyHeaders.get(header)); 
                			}
                		}
                		this.logger.logConformity(ConformityResult.failed, logMessage, LogLevel.Warn);
                	}
                }
                
                if (method == null) {
                    throw new ResponseException(Response.Status.BAD_REQUEST, "BAD REQUEST: Syntax error.");
                }

                // Ok, now do the serve()
                Response r = serve(this);
                if (r == null) {
                    throw new ResponseException(Response.Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: Serve() returned a null response.");
                } else {
                    r.setRequestMethod(method);
                    String respMsg = r.send(outputStream);
                    logResponse(respMsg);
                    
                    /* 
                     * FIXME Ugly workaround for EIDCLIENTC-71. Remove once EIDCLIENTC-69 is finished.
                     * This will also cause the socket variable and the getSocket() method to become obsolete
                     */
                    if (r.getTxtResponse() != null && r.getTxtResponse().startsWith("<TCTokenType>") && testData.tcTokenDisconnect()) {
                    	this.getInputStream().close();
                    	this.getSocket().close();
                    }
                }
            } catch (SocketException e) {
                // throw it out to close socket object (finalAccept)
                throw e;
            } catch (SocketTimeoutException ste) {
            	throw ste;
        	}catch (IOException ioe) {
        	    //TODO hack to suppress TLS errors
        	    if(!doSuppressTLSErrors) {
                    Response r = new Response(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage(), testData.chunkedTransfer());
                    this.logger.logState("Error while processing HTTP request: " + ioe.getMessage(), LogLevel.Error);
                    String respMsg = r.send(outputStream);
                    logResponse(respMsg);
                    safeClose(outputStream);
        	    }
            } catch (ResponseException re) {
                Response r = new Response(re.getStatus(), MIME_PLAINTEXT, re.getMessage(), testData.chunkedTransfer());
                String errorTxt = re.getMessage();
                if (re.getCause() != null)
                    errorTxt += " (" + re.getCause().getMessage() + ")";
                this.logger.logState("Unable to process HTTP request: " + errorTxt, LogLevel.Warn);
                String respMsg = r.send(outputStream);
                logResponse(respMsg);
                safeClose(outputStream);
            } finally {
                
            }
        }
        
        private void logRequest(String msg)
        {
            if (msg != null)
                this.logger.logProtocol(
                        Protocols.HTTP.toString(),
                        ProtocolDirection.received,
                        this.socket.getRemoteSocketAddress().toString(),
                        this.socket.getLocalAddress().getHostAddress() + ":" + this.socket.getLocalPort(),
                        msg);
        }
        
        private void logResponse(String msg)
        {
            if (msg != null)
                this.logger.logProtocol(
                        Protocols.HTTP.toString(),
                        ProtocolDirection.sent,
                        this.socket.getLocalAddress().getHostAddress() + ":" + this.socket.getLocalPort(),
                        this.socket.getRemoteSocketAddress().toString(),
                        msg);
        }
        
        private boolean checkHeaderEnd(byte[] temp) {
            if (Arrays.equals(temp, HTTP_BODY_SEPERATOR)) {
            	isHTTP11LineSeparator = true;
                return true;
            }
            isHTTP11LineSeparator = false;
            
            if (testData.eIDServiceAccpetNonConformHTTP11Message() && Arrays.equals(new byte[] { temp[2], temp[3] }, HTTP_BODY_SEPERATOR_NON_CONFORM)) {
                return true;
            }
            
            return false;
        }
        
        /**
         * Check for HTTP/1.1 conformity by validating the headers.
         * Note that these checks may be expanded by optional header checks in upcoming versions.
         * @return HashMap containing the faulty headers and their data
         */
        private HashMap<String, String> checkHTTP11Headers() {
        	HashMap<String, String> faultyHeaders = new HashMap<String, String>();
        	// evaluate the mandatory HOST header
        	if (headers.containsKey("host")) {
        		String urlString = headers.get("host");
        		try {
        			/*
        			 * The URL class needs the protocol version and performs other checks by itself.
        			 * Add http:// as prefix if none is present and try to create an URL instance.
        			 */
        			if (!urlString.startsWith("http://") && !urlString.startsWith("https://"))
        				urlString = "http://" + urlString;
        			@SuppressWarnings("unused")
					URL testURL = new URL(urlString);
        		}
        		catch (MalformedURLException e) {
        			faultyHeaders.put("host", headers.get("host"));
        		}
        	}
        	else {
        		faultyHeaders.put("host", "This mandatory entry was missing");
        	}
        	return faultyHeaders;
        }
        
        /**
         * Decodes the sent headers and loads the data into Key/value pairs
         */
        private void decodeHeader() throws ResponseException {
            
            params = new HashMap<String, String>();
            headers = new HashMap<String, String>();
            try {
                // Read the request line
                BufferedReader in = new BufferedReader(new StringReader(rawHeader));
                String inLine = in.readLine();
                if (inLine == null) {
                    return;
                }
                
                StringTokenizer st = new StringTokenizer(inLine);
                if (!st.hasMoreTokens()) {
                    throw new ResponseException(Response.Status.BAD_REQUEST, "BAD REQUEST: Syntax error. Usage: GET /example/file.html");
                }
                
                method = Method.lookup(st.nextToken());
                
                if (!st.hasMoreTokens()) {
                    throw new ResponseException(Response.Status.BAD_REQUEST, "BAD REQUEST: Missing URI. Usage: GET /example/file.html");
                }
                
                String localURI = st.nextToken();
                
                // Decode parameters from the URI
                int qmi = localURI.indexOf('?');
                if (qmi >= 0) {
                    decodeParms(localURI.substring(qmi + 1), params);
                    localURI = decodePercent(localURI.substring(0, qmi));
                } else {
                    localURI = decodePercent(localURI);
                }
                
                // If there's another token, it's protocol version,
                // followed by HTTP headers.
                // NOTE: this now forces header names lowercase since they are
                // case insensitive and vary by client.
                if (st.hasMoreTokens()) {
                	containsHTTP11Version = st.nextToken().equalsIgnoreCase(http11);
                    String line = in.readLine();
                    while (line != null && line.trim().length() > 0) {
                        int p = line.indexOf(':');
                        if (p >= 0)
                            headers.put(line.substring(0, p).trim().toLowerCase(), line.substring(p + 1).trim());
                        line = in.readLine();
                    }
                }
                uri = localURI;
                
            } catch (IOException ioe) {
                throw new ResponseException(Response.Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage(), ioe);
            }
        }
        
        /**
         * Decodes parameters in percent-encoded URI-format ( e.g. "name=Jack%20Daniels&pass=Single%20Malt" ) and adds them to given Map. NOTE: this doesn't
         * support multiple identical keys due to the simplicity of Map.
         */
        private void decodeParms(String parms, Map<String, String> p) {
            if (parms == null) {
                p.put(QUERY_STRING_PARAMETER, "");
                return;
            }
            
            p.put(QUERY_STRING_PARAMETER, parms);
            StringTokenizer st = new StringTokenizer(parms, "&");
            while (st.hasMoreTokens()) {
                String e = st.nextToken();
                int sep = e.indexOf('=');
                if (sep >= 0) {
                    p.put(decodePercent(e.substring(0, sep)).trim(), decodePercent(e.substring(sep + 1)));
                } else {
                    p.put(decodePercent(e).trim(), "");
                }
            }
        }
        
        public final Map<String, String> getParms() {
            return params;
        }
        
        public final Map<String, String> getHeaders() {
            return headers;
        }
        
        public final String getUri() {
            return uri;
        }
        
        public final Method getMethod() {
            return method;
        }
        
        public final InputStream getInputStream() {
            return inputStream;
        }
        
        public String getContent() throws IOException { 
            return rawContent;
        }
        
        public String getPostContent() throws IOException {
            if (method == Method.POST) {
                return getContent();
            }
            
            return null;
        }
        
        public final String getRawHeader() {
            return rawHeader;
        }
        
        public final UUID getSessionID() {
            return sessionID;
        }

		/**
		 * @return the socket
		 */
		public Socket getSocket() {
			return socket;
		}
    }
    
    private static final class ResponseException extends Exception {
        
        /**
		 * 
		 */
        private static final long serialVersionUID = -3696895264403652196L;
        private final Response.Status status;
        
        public ResponseException(Response.Status status, String message) {
            super(message);
            this.status = status;
        }
        
        public ResponseException(Response.Status status, String message, Exception e) {
            super(message, e);
            this.status = status;
        }
        
        public Response.Status getStatus() {
            return status;
        }
    }
    
    private static final void safeClose(ServerSocket serverSocket) {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
            }
        }
    }
    
    private static final void safeClose(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
    
    private static final void safeClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }
    
    /**
     * Return "EIDService" or "EService" depending on which server-implementation was requested.
     * 
     * @return
     */
    private String getServerImplementationName() {
        return getServerImplementation().getClass().getSimpleName();
    }
    
    /**
     * Returns the EID- or E-Service object. Pulled out to be callable from asyncRunner (inner classes)
     */
    private Object getServerImplementation() {
        return this;
    }
    
}
