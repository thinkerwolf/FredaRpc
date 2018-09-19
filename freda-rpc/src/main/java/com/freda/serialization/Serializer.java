package com.freda.serialization;

import java.io.*;

import com.freda.common.SLI;

/**
 * 序列化
 * 
 * @author wukai
 *
 */
@SLI("hessian2")
public interface Serializer {

	ObjectOutput serialize(OutputStream os) throws IOException;

	ObjectInput deserialize(InputStream is) throws IOException, ClassNotFoundException;

}
