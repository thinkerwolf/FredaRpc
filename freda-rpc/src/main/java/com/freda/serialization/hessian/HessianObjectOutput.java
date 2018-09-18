package com.freda.serialization.hessian;

import com.caucho.hessian.io.HessianOutput;

import java.io.IOException;
import java.io.ObjectOutput;

public class HessianObjectOutput implements ObjectOutput {

    private HessianOutput output;

    public HessianObjectOutput(HessianOutput output) {
        this.output = output;
    }

    @Override
    public void writeObject(Object obj) throws IOException {
        output.writeObject(obj);
    }

    @Override
    public void write(int b) throws IOException {
        output.writeInt(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        output.writeBytes(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        output.writeBytes(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        output.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        output.writeInt(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        output.writeInt(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        output.writeInt(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        output.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        output.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        output.writeDouble(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        output.writeDouble(v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        output.writeString(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        output.writeString(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        output.writeString(s);
    }

    @Override
    public void flush() throws IOException {
        output.flush();
    }

    @Override
    public void close() throws IOException {
        output.close();
    }
}
