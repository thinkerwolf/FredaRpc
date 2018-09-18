package com.freda.serialization.json;

import java.io.IOException;
import java.nio.charset.Charset;

import com.freda.common.util.JsonUtils;
import com.freda.serialization.Serializer;

public class JsonSerializer implements Serializer {

	@Override
	public byte[] serialize(Object obj) throws IOException {
		return JsonUtils.obj2Json(obj).getBytes();
	}

	@Override
	public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException, ClassNotFoundException {
		return JsonUtils.json2Obj(new String(bytes, Charset.forName("UTF-8")), clazz);
	}

	@Override
	public Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		throw new UnsupportedOperationException("Json serizlize need class");
	}

}
