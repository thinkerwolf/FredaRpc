package com.freda.serialization.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.freda.serialization.Serializer;

import java.io.*;

public class Hessian2Serializer implements Serializer{

    @Override
    public ObjectOutput serialize(OutputStream os) throws IOException {
        Hessian2Output ho = new Hessian2Output(os);
        return new Hessian2ObjectOutput(ho);
    }

    @Override
    public ObjectInput deserialize(InputStream is) throws IOException, ClassNotFoundException {
        Hessian2Input hi = new Hessian2Input(is);
        return new Hessian2ObjectInput(hi);
    }
}
