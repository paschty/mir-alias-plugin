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
import org.mycore.mir.alias.servletUtils.CharResponseWrapper;
import org.xml.sax.SAXException;

public class MCRXmlLayoutFilter implements Filter {

	private static Logger LOGGER = LogManager.getLogger();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		if (response.getCharacterEncoding() == null) {

			LOGGER.warn("ServletResponse does not have setted character encoding. Use UTF-8 as default.");
			response.setCharacterEncoding("UTF-8"); // Or whatever default. UTF-8 is good for World Domination.
		}

		LOGGER.info(
				"Start to wrap HttpServletReponse -> The original response is handled and closed by the servlet engine.");
		CharResponseWrapper wrappedResponse = new CharResponseWrapper((HttpServletResponse) response);

		/*
		 * pass the wrappers on to the next entry
		 */
		chain.doFilter(request, wrappedResponse);

		/*
		 * Log out the response for debugging
		 */
		LOGGER.info(
				"Look into transformed servlet response after original response was handled and closed by the servlet engine.");
		LOGGER.info("Character Encoding: " + wrappedResponse.getCharacterEncoding());
		LOGGER.info("Content Type: " + wrappedResponse.getContentType());
		LOGGER.info("Response status: " + wrappedResponse.getStatus());

		byte[] byteArray = wrappedResponse.getByteArray();
		
		if (wrappedResponse.getContentType() != null && wrappedResponse.getContentType().contains("xml")) {

			LOGGER.info(
					"Transformed response is an xml. Generate MCRByteContent and use it with MCRLayoutService to generate html.");

			MCRByteContent content = new MCRByteContent(byteArray);
			try {

				if (MCRSessionMgr.isLocked()) {
					
					LOGGER.info("Unlock MCRSession via MCRSessionManager.");
					MCRSessionMgr.unlock();
				}

				LOGGER.info("Generate html with MCRLayoutService.instance().doLayout(..) .");
				MCRLayoutService.instance().doLayout((HttpServletRequest) request, (HttpServletResponse) response,
						content);
			} catch (TransformerException | SAXException e) {

				LOGGER.error("Error on doLayout with MCRLayoutService: " + e.getMessage());
			}
		} else {
			response.getOutputStream().write(byteArray);
		}
	}
}
