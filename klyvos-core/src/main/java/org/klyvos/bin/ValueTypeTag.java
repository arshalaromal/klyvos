package org.klyvos.bin;

/**
 * @author Arshal Aromal
 */
public enum ValueTypeTag {
    NULL((byte) 0x70),
    INTEGER((byte) 0x71),
    DECIMAL((byte) 0x72),
    BOOLEAN((byte) 0x73),
    STRING((byte) 0x74),
    LIST((byte) 0x75),
    FUNCTION((byte) 0x76),
    NATIVE_FUNCTION((byte) 0x77),
    ERROR((byte) 0x78);

    public final byte tag;

    ValueTypeTag(byte tag) {
        this.tag = tag;
    }

    public static ValueTypeTag fromByte(byte b) {
        for (ValueTypeTag tag : values()) {
            if (tag.tag == b) return tag;
        }
        throw new IllegalArgumentException("Unknown ValueTypeTag: " + b);
    }
}

