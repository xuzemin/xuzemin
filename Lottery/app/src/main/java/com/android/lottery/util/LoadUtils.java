package com.android.lottery.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class LoadUtils {

	public static byte[] loadByte(HttpURLConnection conn) throws IOException {
		InputStream in = conn.getInputStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len = 0;
		while ((len = in.read(buf)) != -1)
			out.write(buf, 0, len);
		in.close();
		out.close();
		return out.toByteArray();
	}

	public static String loadString(HttpURLConnection conn) throws IOException {
		return new String(loadByte(conn), "utf-8");
	}
}