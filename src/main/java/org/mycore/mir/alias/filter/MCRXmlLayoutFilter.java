package org.mycore.mir.alias.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.content.MCRByteContent;
import org.mycore.common.xml.MCRLayoutService;
import org.mycore.mir.alias.xslutil.BufferedHttpResponseWrapper;
import org.xml.sax.SAXException;

public class MCRXmlLayoutFilter implements Filter {

	private static Logger LOGGER = LogManager.getLogger();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		LOGGER.info("Add MCR xml Layoutfilter on URL-Pattern /go/*");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		/**
		 * check on HttpServletResponse
		 */
		if (!(response instanceof HttpServletResponse)) {

			LOGGER.error("This Filter is only compatible with HTTP");
			throw new ServletException("response is not an instance of HttpServletResponse");
		}

		BufferedHttpResponseWrapper responseWrapper = new BufferedHttpResponseWrapper((HttpServletResponse) response);
		/*
		 * no filter on request
		 */
		chain.doFilter(request, responseWrapper);

		/*
		 * Do Layout filtering only on xml
		 */
		byte[] xmlAsByteArray = responseWrapper.getBuffer();

		if (responseWrapper.getContentType() != null && responseWrapper.getContentType().equals("text/xml")
				&& xmlAsByteArray != null && xmlAsByteArray.length != 0) {

			MCRByteContent content = new MCRByteContent(responseWrapper.getBuffer());
			try {
				MCRSessionMgr.unlock();

				MCRLayoutService.instance().doLayout((HttpServletRequest) request, (HttpServletResponse) response,
						content);
			} catch (TransformerException | SAXException e) {

				LOGGER.error("Error on doLayout with MCRLayoutService: " + e.getMessage());
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
}
