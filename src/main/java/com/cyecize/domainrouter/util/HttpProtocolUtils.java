package com.cyecize.domainrouter.util;

import com.cyecize.domainrouter.constants.General;
import com.cyecize.domainrouter.error.CannotParseRequestException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpProtocolUtils {
    public static List<String> parseMetadataLines(InputStream inputStream, boolean allowNewLineWithoutReturn)
            throws CannotParseRequestException {
        try {
            final List<String> metadataLines = new ArrayList<>();

            StringBuilder metadataBuilder = new StringBuilder();
            boolean wasNewLine = true;
            int lineNumber = 1;
            int readBytesCount = 0;
            int b;

            while ((b = inputStream.read()) >= 0) {
                readBytesCount++;
                if (b == '\r') {
                    // expect new-line
                    int next = inputStream.read();
                    if (next < 0 || next == '\n') {
                        lineNumber++;
                        if (wasNewLine) break;
                        metadataLines.add(metadataBuilder.toString());
                        if (next < 0) break;
                        metadataBuilder = new StringBuilder();
                        wasNewLine = true;
                    } else {
                        inputStream.close();
                        throw new CannotParseRequestException(
                                String.format("Illegal character after return on line %d.", lineNumber)
                        );
                    }
                } else if (b == '\n') {
                    if (!allowNewLineWithoutReturn) {
                        throw new CannotParseRequestException(
                                String.format("Illegal new-line character without preceding return on line %d.", lineNumber)
                        );
                    }

                    // unexpected, but let's accept new-line without returns
                    lineNumber++;
                    if (wasNewLine) break;
                    metadataLines.add(metadataBuilder.toString());
                    metadataBuilder = new StringBuilder();
                    wasNewLine = true;
                } else {
                    metadataBuilder.append((char) b);
                    wasNewLine = false;
                }
            }

            if (metadataBuilder.length() > 0) {
                metadataLines.add(metadataBuilder.toString());
            }

            if (readBytesCount < 2) {
                throw new CannotParseRequestException("Request is empty");
            }

            return metadataLines;
        } catch (IOException ex) {
            throw new CannotParseRequestException(ex.getMessage(), ex);
        }
    }

    public static Map<String, String> getHeaders(List<String> requestMetadata) {
        final Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < requestMetadata.size(); i++) {
            final String[] headerKeyValuePair = requestMetadata.get(i).split(":\\s+");
            headers.put(headerKeyValuePair[0], headerKeyValuePair[1]);
        }

        return headers;
    }

    public static int getContentLength(InputStream inputStream, Map<String, String> headers) throws IOException {
        if (headers.containsKey("Content-Length")) {
            return Integer.parseInt(headers.get("Content-Length"));
        }

        return inputStream.available();
    }

    public static String getHost(Socket socket, Map<String, String> headers) {
        if (headers.containsKey("Host")) {
            return headers.get("Host");
        }

        return socket.getInetAddress().getHostName();
    }

    /**
     * First send the data that was already read.
     * Then proceed to transfer the rest of the stream.
     */
    public static void transferHttpRequest(InputStream inputStream,
                                           List<String> metadata,
                                           int contentLength,
                                           OutputStream outputStream) throws IOException {
        final String metadataFormatted = String.join("\r\n", metadata)
                + "\r\n"
                + "\r\n";

        outputStream.write(metadataFormatted.getBytes(StandardCharsets.UTF_8));

        final byte[] bytes = new byte[General.READ_BUFFER_SIZE];
        while (contentLength > 0) {
            final int chunkRead = Math.min(General.READ_BUFFER_SIZE, contentLength);
            inputStream.read(bytes, 0, chunkRead);
            contentLength -= chunkRead;

            outputStream.write(bytes, 0, chunkRead);
        }
    }

    /**
     * Read the metadata first to try and get the content length of the body.
     */
    public static void transferHttpResponse(InputStream inputStream, OutputStream outputStream) throws IOException {
        final List<String> metadataLines;
        try {
            metadataLines = parseMetadataLines(inputStream, true);
        } catch (CannotParseRequestException ex) {
            log.error("Could not parse server's response.", ex);
            return;
        }

        final Map<String, String> headers = getHeaders(metadataLines);
        int contentLength = getContentLength(inputStream, headers);

        final String metadataFormatted = String.join("\r\n", metadataLines)
                + "\r\n"
                + "\r\n";

        outputStream.write(metadataFormatted.getBytes(StandardCharsets.UTF_8));

        final byte[] bytes = new byte[General.READ_BUFFER_SIZE];
        while (contentLength > 0) {
            final int chunkRead = Math.min(General.READ_BUFFER_SIZE, contentLength);
            inputStream.read(bytes, 0, chunkRead);
            contentLength -= chunkRead;

            outputStream.write(bytes, 0, chunkRead);
        }
    }
}
