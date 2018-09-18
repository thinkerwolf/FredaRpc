package com.freda.serialization;

import java.io.IOException;

/**
 * 序列化
 * 
 * @author wukai
 *
 */
public interface Serializer {

	byte[] serialize(Object obj) throws IOException;

	<T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException, ClassNotFoundException;
	
	Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException;
	
}
