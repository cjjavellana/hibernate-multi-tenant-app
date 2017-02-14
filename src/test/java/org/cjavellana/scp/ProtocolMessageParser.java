package org.cjavellana.scp;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cjavellana on 15/2/17.
 */
public class ProtocolMessageParser {

    private static final int PROTOCOL_MSG_MODE_LENGTH = 5;
    private InputStream in;

    public ProtocolMessageParser(InputStream in) {
        this.in = in;
    }

    public ProtocolMessage parse() throws IOException {
        return new ProtocolMessage('C', mode(in), 0, "");
    }

    private int header(InputStream in) throws IOException {
        int msgStatus = in.read();
        return msgStatus;
    }

    private String mode(InputStream in) throws IOException {
        // e.g. "0644 "
        byte[] buff = new byte[PROTOCOL_MSG_MODE_LENGTH];
        in.read(buff, 0, PROTOCOL_MSG_MODE_LENGTH);
        return new String(buff);
    }
}
