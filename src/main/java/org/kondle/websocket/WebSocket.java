package org.kondle.websocket;



import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class WebSocket
{
    private boolean isClient;
    private Socket s;
    private String webSocketKey;
    private InputStream is;
    private OutputStream os;

    public WebSocket(Socket socket) throws IOException
    {
        this.s = socket;
        this.isClient = false;

        StringBuilder builder = new StringBuilder();
        char[] rel = new char[]{'\r','\n','\r','\n'};
        char[] buf = new char[]{' ',' ',' ',' '};


        this.is = this.s.getInputStream();
        this.os = this.s.getOutputStream();

        InputStreamReader reader = new InputStreamReader(this.is, StandardCharsets.UTF_8);

        while (!Arrays.equals(rel,buf))
        {
            buf[0] = buf[1];
            buf[1] = buf[2];
            buf[2] = buf[3];
            buf[3] = (char) reader.read();
            builder.append(buf[3]);
        }

        reader = null;

        String[] req = builder.toString().split("\\r\\n");
        for (int i = 1; i < req.length; i++)
        {
            String[] header = req[i].split(": ");
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
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }


        String response = String.format
                (
                        "HTTP/1.1 101 Switching Protocols\r\n" +
                        "Upgrade: websocket\r\n" +
                        "Connection: Upgrade\r\n" +
                        "Sec-WebSocket-Accept: %s\r\n\r\n", keyRes
                );

        this.os.write(response.getBytes(StandardCharsets.UTF_8));


        WebSocketFrame fr = WebSocketFrame.readWebSocketFrame(this.is);
        System.out.println(fr);

    }

    public WebSocket(URI uri)
    {
        this.isClient = true;
    }

    public void send(byte[] data)
    {

    }


    public void close()
    {

    }

    public void receive(byte[] buffer)
    {

    }

}
