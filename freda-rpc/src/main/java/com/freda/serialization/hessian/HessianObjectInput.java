package com.freda.serialization.hessian;


import com.caucho.hessian.io.HessianInput;

import java.io.IOException;
import java.io.ObjectInput;

public class HessianObjectInput implements ObjectInput {

    private HessianInput input;

    public HessianObjectInput(HessianInput input) {
        this.input = input;
    }

    @Override
    public Object readObject() throws ClassNotFoundException, IOException {
        return input.readObject();
    }

    @Override
    public int read() throws IOException {
        return input.readByte();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return input.readBytes(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return input.readBytes(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        throw new UnsupportedOperationException("skip unsupport");
    }

    @Override
    public int available() throws IOException {
        return input.readLength();
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        input.readBytes(b, 0, b.length);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        input.readBytes(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        throw new UnsupportedOperationException("skipBytes unsupport");
    }

    @Override
    public boolean readBoolean() throws IOException {
        return input.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return (byte) input.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return input.readByte();
    }

    @Override
    public short readShort() throws IOException {
        return input.readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return input.readShort();
    }

    @Override
    public char readChar() throws IOException {
        return (char) input.readChar();
    }

    @Override
    public int readInt() throws IOException {
        return input.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return input.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return input.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return input.readDouble();
    }

    @Override
    public String readLine() throws IOException {
        return input.readString();
    }

    @Override
    public String readUTF() throws IOException {
        return input.readString();
    }
}
