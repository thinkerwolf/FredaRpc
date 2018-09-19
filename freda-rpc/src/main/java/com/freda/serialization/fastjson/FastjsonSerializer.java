package com.freda.serialization.fastjson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.freda.serialization.ObjectInput;
import com.freda.serialization.ObjectOutput;
import com.freda.serialization.Serializer;

public class FastjsonSerializer implements Serializer {

	@Override
	public ObjectOutput serialize(OutputStream os) throws IOException {
		SerializeWriter out = new SerializeWriter(new OutputStreamWriter(os));
		JSONSerializer serializer = new JSONSerializer(out);
		return new FastjsonObjectOutput(serializer);
	}

	@Override
	public ObjectInput deserialize(InputStream is) throws IOException, ClassNotFoundException {
		return new FastjsonObjectInput(is);
	}

}
