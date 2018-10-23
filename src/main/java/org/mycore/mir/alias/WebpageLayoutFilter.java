package org.mycore.mir.alias;

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
		
		if("text/xml".equals(responseWrapper.getContentType())) {
			MCRByteContent content = new MCRByteContent( responseWrapper.getBuffer());
			try {
				MCRSessionMgr.unlock();
			
				MCRLayoutService.instance().doLayout((HttpServletRequest) request, (HttpServletResponse) response,
						content);
			} catch (TransformerException | SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

//		SAXBuilder builder = new SAXBuilder();
//		InputStream in = new ByteArrayInputStream(origXML);
//		try {
//			Document document = builder.build(in);
//
//			MCRContent editedXML = new MCRJDOMContent(document);
//
//			MCRLayoutService.instance().doLayout((HttpServletRequest) request, (HttpServletResponse) response,
//					editedXML);
//		} catch (JDOMException | TransformerException | SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	@Override
	public void destroy() {
		System.out.println("destroy");

	}
}
