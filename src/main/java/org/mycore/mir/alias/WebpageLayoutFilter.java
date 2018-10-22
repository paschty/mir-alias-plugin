package org.mycore.mir.alias;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.mycore.mir.alias.xslutil.BufferedHttpResponseWrapper;

public class WebpageLayoutFilter implements Filter {

	private static Logger LOGGER = LogManager.getLogger();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		System.out.println("filterconfig");

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		/*
		 * check on response filename (xml)
		 * 
		 */

		if (!(response instanceof HttpServletResponse)) {

			LOGGER.error("This Filter is only compatible with HTTP");
			throw new ServletException("response is not an instance of HttpServletResponse");
		}

		BufferedHttpResponseWrapper responseWrapper = new BufferedHttpResponseWrapper((HttpServletResponse) response);
		chain.doFilter(request, responseWrapper);

		/*
		 * be sure to write output into response wrapper outputstream
		 */
		byte[] origXML = responseWrapper.getBuffer();
		if (origXML == null || origXML.length == 0) {

			chain.doFilter(request, response);
			return;
		}

		SAXBuilder builder = new SAXBuilder();
		InputStream in = new ByteArrayInputStream(origXML);
		try {
			Document document = builder.build(in);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void destroy() {
		System.out.println("destroy");

	}
}
