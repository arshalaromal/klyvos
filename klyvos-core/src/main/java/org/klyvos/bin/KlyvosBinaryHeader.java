package org.klyvos.bin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Handles writing and validating the header of a .klvb file.<br>
 * Format:<br>
 * - 4 bytes: Magic number (e.g. {@code KLVB})<br>
 * - 1 byte: Major version<br>
 * - 1 byte: Minor version<br>
 * - 2 bytes: Reserved (for future use, currently set to 0)<b>
 *
 * @author Arshal Aromal
 */
public class KlyvosBinaryHeader {
    public static final int HEADER_SIZE = 8;
    private static final byte[] MAGIC = {'K', 'L', 'V', 'B'};
    private static final byte VERSION_MAJOR = 1;
    private static final byte VERSION_MINOR = 0;

    public static void write(DataOutputStream out) throws IOException {
        out.write(MAGIC);
        out.writeByte(VERSION_MAJOR);
        out.writeByte(VERSION_MINOR);
        out.writeShort(0);
    }

    public static void validate(DataInputStream in) throws IOException {
        byte[] magic = new byte[4];
        in.readFully(magic);

        for (int i = 0; i < 4; i++) {
            if (magic[i] != MAGIC[i]) {
                throw new IOException("Invalid Klyvos binary: bad magic number");
            }
        }

        byte major = in.readByte();
        byte minor = in.readByte();

        if (major != VERSION_MAJOR) {
            throw new IOException("Unsupported .klvb file version: " + major + "." + minor);
        }

        // Consume reserved bytes
        in.readShort(); // currently unused
    }

}
