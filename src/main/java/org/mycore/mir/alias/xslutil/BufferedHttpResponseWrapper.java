package org.mycore.mir.alias.xslutil;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 
 * Response wrapper
 *
 */
public class BufferedHttpResponseWrapper extends HttpServletResponseWrapper {

	private static Logger LOGGER = LogManager.getLogger();


	private BufferedServletOutputStream bufferedServletOut = new BufferedServletOutputStream();

	private PrintWriter printWriter = null;
	private ServletOutputStream outputStream = null;

	public BufferedHttpResponseWrapper(HttpServletResponse origResponse) {
		super(origResponse);
	}

	public byte[] getBuffer() {
		return this.bufferedServletOut.getBuffer();
	}

	public PrintWriter getWriter() throws IOException {
		if (this.outputStream != null) {

			LOGGER.error("Unable to use getWriter() after getOutputStream() was called");

			throw new IllegalStateException("Unable to use getWriter() after getOutputStream() was called");
		}

		if (this.printWriter == null) {
			this.printWriter = new PrintWriter(this.bufferedServletOut);
		}
		return this.printWriter;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		if (this.printWriter != null) {

			LOGGER.error("Unable to use getOutputStream() after getWriter() was called");

			throw new IllegalStateException("Unable to use getOutputStream() after getWriter() was called");
		}

		if (this.outputStream == null) {
			this.outputStream = this.bufferedServletOut;
		}
		return this.outputStream;
	}

	// to overwrite
	public void flushBuffer() throws IOException {
		if (this.outputStream != null) {
			this.outputStream.flush();
		} else if (this.printWriter != null) {
			this.printWriter.flush();
		}
	}

	public int getBufferSize() {
		return this.bufferedServletOut.getBuffer().length;
	}

	public void reset() {
		this.bufferedServletOut.reset();
	}

	public void resetBuffer() {
		this.bufferedServletOut.reset();
	}

	public void setBufferSize(int size) {
		this.bufferedServletOut.setBufferSize(size);
	}
}
