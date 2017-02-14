package org.cjavellana.scp;

/**
 * <p>
 * Represents the first message from the remote process;
 * </p>
 *
 * @see <a href="https://blogs.oracle.com/janp/entry/how_the_scp_protocol_works">https://blogs.oracle.com/janp/entry/how_the_scp_protocol_works</a>
 */
public class ProtocolMessage {

    private int header;
    private String fileMode;
    private long fileSizeInBytes;
    private String filename;

    public ProtocolMessage(int header, String fileMode, long fileSizeInBytes, String filename) {
        this.header = header;
        this.fileMode = fileMode;
        this.fileSizeInBytes = fileSizeInBytes;
        this.filename = filename;
    }

    /**
     * Create a protocol message to mark the end of directory
     *
     * @param header
     */
    public ProtocolMessage(int header) {
        // this one arg constructor is used to mark the end of directory
        assert header == 69;
        this.header = header;
    }

    public int getHeader() {
        return header;
    }

    public String getFileMode() {
        return fileMode;
    }

    public long getFileSizeInBytes() {
        return fileSizeInBytes;
    }

    public String getFilename() {
        return filename;
    }
}
