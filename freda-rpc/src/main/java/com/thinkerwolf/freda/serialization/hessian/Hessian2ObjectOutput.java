package com.thinkerwolf.freda.serialization.hessian;

import com.caucho.hessian.io.Hessian2Output;
import com.thinkerwolf.freda.serialization.ObjectOutput;

import java.io.IOException;

public class Hessian2ObjectOutput implements ObjectOutput {

    private Hessian2Output output;

    public Hessian2ObjectOutput(Hessian2Output hessian2Output) {
        this.output = hessian2Output;
    }

    @Override
    public void writeObject(Object obj) throws IOException {
        output.writeObject(obj);
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
    public void flush() throws IOException {
        output.flush();
    }

    @Override
    public void close() throws IOException {
        output.close();
    }
}
