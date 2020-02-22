package org.kondle.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class WebServerSocket
{
    private ServerSocket serverSocket;
    private String webSocketKey;

    public WebServerSocket(int port,InetAddress bindAddr) throws IOException
    {
        this.serverSocket = new ServerSocket(port,0,bindAddr);
    }


    public WebSocket accept() throws IOException
    {
        WebSocket retSock = new WebSocket();

        Socket s = serverSocket.accept();

        char[] rel = new char[]{'\r','\n'};
        char[] buf = new char[]{' ',' '};

        InputStream is = s.getInputStream();
        OutputStream os = s.getOutputStream();

        InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

        {
            StringBuilder reqLine = new StringBuilder();
            while (!Arrays.equals(rel, buf))
            {
                buf[0] = buf[1];
                buf[1] = (char) reader.read();
            }
            reqLine.delete(reqLine.length() - 2, reqLine.length());
            retSock.reqLine = reqLine.toString();
        }

        rel = new char[]{'\r','\n','\r','\n'};
        buf = new char[]{' ',' ',' ',' '};
        StringBuilder headersBuilder = new StringBuilder();

        while (!Arrays.equals(rel,buf))
        {
            buf[0] = buf[1];
            buf[1] = buf[2];
            buf[2] = buf[3];
            buf[3] = (char) reader.read();
            headersBuilder.append(buf[3]);
        }

        reader = null;

        String[] headers = headersBuilder.toString().split("\\r\\n");

        for (int i = 1; i < headers.length; i++)
        {
            String[] header = headers[i].split(": ");
            if (header[0].equals("Sec-WebSocket-Key"))
                this.webSocketKey = header[1];
        }

        String keyCat = this.webSocketKey.concat("258EAFA5-E914-47DA-95CA-C5AB0DC85B11");
        String keyRes = null;
        MessageDigest crypt = null;
        try
        {
            crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(keyCat.getBytes(StandardCharsets.UTF_8));
            keyRes = Base64.getEncoder().encodeToString(crypt.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            //SHA-1 not found...     what?              TODO: impl SHA-1    :)
        }


        String response = String.format
                (
                    "HTTP/1.1 101 Switching Protocols\r\n" +
                    "Upgrade: websocket\r\n" +
                    "Connection: Upgrade\r\n" +
                    "Sec-WebSocket-Accept: %s\r\n\r\n", keyRes
                );

        os.write(response.getBytes(StandardCharsets.UTF_8));
        retSock.s = s;

        return retSock;
    }
}
