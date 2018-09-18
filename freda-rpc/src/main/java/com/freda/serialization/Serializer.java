package com.freda.serialization;

import java.io.*;

/**
 * 序列化
 * 
 * @author wukai
 *
 */
public interface Serializer {

	ObjectOutput serialize(OutputStream os) throws IOException;

	ObjectInput deserialize(InputStream is) throws IOException, ClassNotFoundException;
	
}
