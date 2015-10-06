package com.secunet.testbedutils.eac2.cv;

import java.util.LinkedList;

public class TLVObject {
    
    public enum TagClass {
        Universal,
        Application,
        Context,
        Private
    }
    
    private class TLVTag {
        
        public TagClass tagClass;
        public boolean constructed;
        public int size;
        public int value;
        
        // public TLVTag() {
        // this.tagClass = TagClass.Universal;
        // this.constructed = false;
        // this.size = 1;
        // this.value = 0;
        // }
        
        public TLVTag(TagClass tagClass, boolean constructed, int size, int value) {
            this.tagClass = tagClass;
            this.constructed = constructed;
            this.size = size;
            this.value = value;
        }
    }
    
    private class TLVLength {
        public int size;
        public int value;
        
        public TLVLength(int size, int value) {
            this.size = size;
            this.value = value;
        }
    }
    
    private TLVTag tag;
    public byte[] value;
    
    /**
     * Private constructor for internal use only!
     */
    private TLVObject() {
        // this.tag = new TLVTag();
    }
    
    public TLVObject(int tag) {
        this(tag, null);
    }
    
    public TLVObject(int tag, byte value) {
        this.tag = decodeTag(ByteHelper.toBytes(tag), 0);
        byte[] tmp = new byte[1];
        tmp[0] = value;
        this.value = tmp;
    }
    
    public TLVObject(int tag, byte[] value) {
        this.tag = decodeTag(ByteHelper.toBytes(tag), 0);
        this.value = value;
    }
    
    public int getTag() {
        
        return ByteHelper.toInt(encodeTag());
    }
    
    public int getDecodedTag() {
        return tag.value;
    }
    
    public TLVObject getValueAsTLVObject() {
        return generateFromBytes(this.value);
    }
    
    public LinkedList<TLVObject> getValueAsTLVObjectList() {
        
        return generateMultipleFromBytes(this.value);
    }
    
    public byte[] toBytes() {
        int valueLength;
        if (value == null) {
            valueLength = 0;
        } else {
            valueLength = value.length;
        }
        
        // encode tag
        byte[] tagBytes = encodeTag();
        
        // encode length
        byte[] lengthBytes = encodeLength(valueLength);
        
        // concatenate all bytes
        byte[] output = new byte[tagBytes.length + lengthBytes.length + valueLength];
        System.arraycopy(tagBytes, 0, output, 0, tagBytes.length);
        System.arraycopy(lengthBytes, 0, output, tagBytes.length, lengthBytes.length);
        if (valueLength > 0)
            System.arraycopy(value, 0, output, tagBytes.length + lengthBytes.length, valueLength);
        return output;
    }
    
    private byte[] encodeLength(int valueLength) {
        byte[] lengthBytes;
        if (valueLength <= 0x7F) {
            // length can be encoded in a single byte
            lengthBytes = new byte[1];
            lengthBytes[0] = (byte) (valueLength & 0xFF);
        } else {
            // length must be encoded in multiple bytes
            byte[] tmpLengthBytes = ByteHelper.toBytes(valueLength);
            int len = tmpLengthBytes.length;
            lengthBytes = new byte[len + 1];
            lengthBytes[0] = (byte) ((len | 0x80) & 0xFF);
            System.arraycopy(tmpLengthBytes, 0, lengthBytes, 1, len);
        }
        return lengthBytes;
    }
    
    private byte[] encodeTag() {
        // encode tag class
        byte first;
        switch (tag.tagClass) {
            case Universal:
                first = (byte) 0x00;
                break;
            case Application:
                first = (byte) 0x40;
                break;
            case Context:
                first = (byte) 0x80;
                break;
            case Private:
                first = (byte) 0xC0;
                break;
            default:
                throw new TLVException();
        }
        
        // encode constructed bit
        if (tag.constructed)
            first = (byte) ((first | 0x20) & 0xFF);
        
        // encode tag
        byte[] tagBytes;
        if (tag.value < 0x1F) {
            // tag can be encoded in one byte
            tagBytes = new byte[1];
            tagBytes[0] = (byte) ((first | tag.value) & 0xFF);
        } else {
            // tag must be encoded in multiple bytes
            int num = first | 0x1F;
            int i = 7;
            while (tag.value >> i > 0)
                i += 7;
            for (i -= 7; i > 0; i -= 7)
                num = num << 8 | ((((tag.value >> i) & 0x7F) | 0x80) & 0xFF);
            num = num << 8 | (tag.value & 0x7F);
            tagBytes = ByteHelper.toBytes(num);
        }
        return tagBytes;
    }
    
    public static LinkedList<TLVObject> generateMultipleFromBytes(byte[] input) {
        LinkedList<TLVObject> objectList = new LinkedList<TLVObject>();
        
        int offset = 0;
        while (offset < input.length) {
            TLVObject t = new TLVObject();
            offset += t.fromBytes(input, offset);
            objectList.add(t);
        }
        if (offset != input.length)
            throw new TLVException("TLV object size does not match input size.");
        
        return objectList; // .toArray( new TLVObject[objectList.size()] );
    }
    
    public static TLVObject generateFromBytes(byte[] input) {
        return generateFromBytes(input, 0);
    }
    
    public static TLVObject generateFromBytes(byte[] input, int offset) {
        if (null == input)
            return null;
        TLVObject t = new TLVObject();
        t.fromBytes(input, offset);
        return t;
    }
    
    /**
     * @param input
     *            the byte array to decode
     * @param offset
     *            offset where TLV object to be decoded starts
     * @return length of decoded TLV object
     */
    private int fromBytes(byte[] input, int offset) {
        // decode tag
        TLVTag tag = decodeTag(input, offset);
        
        // decode length
        TLVLength length = decodeLength(input, offset + tag.size);
        
        if (offset + tag.size + length.size + length.value > input.length)
            throw new TLVException("Size of all TLV parts (" + (tag.size + length.size + length.value)
                    + ") from offset " + offset + " is greater than input size(" + input.length + ").");
        
        // set member
        this.tag = tag;
        if (0 == length.value) {
            this.value = null;
        } else {
            this.value = new byte[length.value];
            System.arraycopy(input, offset + tag.size + length.size, this.value, 0, length.value);
        }
        return tag.size + length.size + length.value;
    }
    
    private static TLVLength decodeLength(byte[] input, int offset) {
        int lengthSize;
        int lengthValue;
        int first = input[offset] & 0xFF;
        if (first <= 0x7F) {
            // length is encoded in a single byte
            lengthValue = first;
            lengthSize = 1;
        } else {
            // length is encoded in multiple bytes
            int len = first & 0x7F;
            if (len < 1 || len > 2)
                throw new TLVException("TLV length is not encoded correctly."); // overflow
            lengthValue = 0;
            for (int i = 1; i <= len; i++)
                lengthValue = (lengthValue << 8) | (input[offset + i] & 0xFF);
            lengthSize = len + 1;
        }
        
        return new TLVObject().new TLVLength(lengthSize, lengthValue);
    }
    
    private static TLVTag decodeTag(byte[] input, int offset) {
        int tagSize;
        int tagValue;
        TagClass tagClass;
        boolean tagConstructed;
        
        // decode tag class
        byte first = input[offset];
        switch (first & 0xC0) {
            case 0x00:
                tagClass = TagClass.Universal;
                break;
            case 0x40:
                tagClass = TagClass.Application;
                break;
            case 0x80:
                tagClass = TagClass.Context;
                break;
            case 0xC0:
                tagClass = TagClass.Private;
                break;
            default:
                throw new TLVException("TLV tag class is not encoded correctly.");
        }
        
        // decode constructed bit
        tagConstructed = (first & 0x20) != 0;
        
        if ((first & 0x1F) < 0x1F) {
            // tag is encoded in single byte
            tagValue = first & 0x1F;
            tagSize = 1;
        } else {
            // tag is encoded in multiple bytes
            int len = 1;
            int num = 0;
            byte c;
            do {
                int numbak = num;
                c = input[offset + len]; // add next bytes to number
                num = (num << 7) | (c & 0x7F);
                if (num >> 7 != numbak)
                    throw new TLVException("TLV tag could not be decoded correctly."); // overflow,
                                                                                       // lost
                                                                                       // some
                                                                                       // bits
                                                                                       // at
                                                                                       // the
                                                                                       // left
                len++;
            } while ((c & 0x80) > 0);
            tagValue = num;
            tagSize = len;
        }
        
        return new TLVObject().new TLVTag(tagClass, tagConstructed, tagSize, tagValue);
    }
    
    public static int getSizeFromBytes(byte[] input) {
        // decode tag
        TLVTag tag;
        try {
            tag = decodeTag(input, 0);
        } catch (TLVException e) {
            return 0;
        }
        
        // decode length
        TLVLength length = null;
        try {
            length = decodeLength(input, tag.size);
        } catch (TLVException e) {
            return 0;
        }
        
        return (tag.size + length.size + length.value);
    }
    
}
