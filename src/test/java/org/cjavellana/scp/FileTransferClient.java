package org.cjavellana.scp;

import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>
 * The file transfer client wraps the scp protocol for copying from or sending files to remote machines.
 * </p>
 *
 * @see <a href="https://blogs.oracle.com/janp/entry/how_the_scp_protocol_works">https://blogs.oracle.com/janp/entry/how_the_scp_protocol_works</a>
 */
public class FileTransferClient {

    private static final int DEFAULT_SCP_PORT = 22;
    private static final int PROTOCOL_MSG_MODE_LENGTH = 5;
    private Session session;
    private boolean isConnected = false;


    FileTransferClient(Session session, String username, IdentityKey identityKey, int port) {
        this.session = session;
    }

    /**
     * Enables or disables strict host checking
     *
     * @param enabled
     */
    public void enableStrictHostChecking(boolean enabled) {
        JSch.setConfig("StrictHostKeyChecking", (enabled) ? "yes" : "no");
    }

    /**
     * Initiates connection to the remote host
     *
     * @throws JSchException When it is not able to establish connection
     */
    public void connect() throws JSchException {
        this.session.connect();
        isConnected = true;
    }

    /**
     * Initiates connection to the remote host
     *
     * @throws JSchException When it is not able to establish connection within the given {@code timeout}
     */
    public void connect(int timeout) throws JSchException {
        this.session.connect(timeout);
        isConnected = true;
    }

    /**
     * @param remoteFilePath The full path of the file to be downloaded from the remote host
     * @param localPath      The path in the local machine where the downloaded file will be stored. Can either be a file path or a directory
     * @throws JSchException When the remote host is not accessible
     */
    public void downloadFromRemote(String remoteFilePath, String localPath) throws JSchException {
        if (isConnected) {
            try {
                Channel channel = session.openChannel("exec");
                ((ChannelExec) channel).setCommand(String.format("scp -f %s", remoteFilePath));

                // get I/O streams for remote scp
                OutputStream out = channel.getOutputStream();
                InputStream in = channel.getInputStream();
                channel.connect();

                // get file metadata, i.e. mode, file size, filename
                sendCommand(out);
                messageMode(in);

                // get file content
                sendCommand(out);
            } catch (IOException ioe) {
                throw new RuntimeException("Unable to read from or write to remote stream", ioe);
            }
        } else {
            throw new JSchException("Session is not connected, call connect() first");
        }
    }

    private void sendCommand(OutputStream out) throws IOException {
        // send '\0'
        out.write(new byte[]{0}, 0, 1);
        out.flush();
    }

    /**
     * Reads the file messageMode from the response
     *
     * @param in The input stream
     * @throws IOException
     */
    private String messageMode(InputStream in) throws IOException {
        int msgStatus = in.read();
        if (msgStatus != 'C') {
            throw new IOException("Invalid first byte, expecting 0x43");
        }

        // e.g. "0644 "
        byte[] buff = new byte[PROTOCOL_MSG_MODE_LENGTH];
        in.read(buff, 0, PROTOCOL_MSG_MODE_LENGTH);
        return new String(buff);
    }


    public static FileTransferClient newFileTransferClient(String username, String host, IdentityKey identityKey, int port) {
        try {
            JSch jSch = new JSch();
            jSch.addIdentity(identityKey.privateKeyPath);
            Session scpSession = jSch.getSession(username, host, port);
            return new FileTransferClient(scpSession, username, identityKey, port);
        } catch (JSchException exception) {
            throw new RuntimeException("Unable to read identity key");
        }
    }

    public static FileTransferClient newFileTransferClient(String username, String host, IdentityKey identityKey) {
        return newFileTransferClient(username, host, identityKey, DEFAULT_SCP_PORT);
    }
}
