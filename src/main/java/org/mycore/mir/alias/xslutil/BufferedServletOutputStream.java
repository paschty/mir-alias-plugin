package org.mycore.mir.alias.xslutil;

import java.io.ByteArrayOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class BufferedServletOutputStream extends ServletOutputStream {

	/*
	 * buffer
	 */
	private ByteArrayOutputStream bos = new ByteArrayOutputStream();

	public byte[] getBuffer() {
		return this.bos.toByteArray();
	}

	/**
	 * To overwrite for own servletoutputstream
	 */
	public void write(int data) {
		this.bos.write(data);
	}

	public void reset() {
		this.bos.reset();
	}

	public void setBufferSize(int size) {

		this.bos = new ByteArrayOutputStream(size);
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setWriteListener(WriteListener writeListener) {
		// TODO Auto-generated method stub

	}
}
