package com.zero.votes.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RedirectToHttpsFilter implements Filter {

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
                String uri = req.getRequestURI();
		if (!req.isSecure() && (uri.endsWith(".xhtml") || uri.endsWith(".html"))) {
			HttpServletResponse resp = (HttpServletResponse) response;
			resp.sendRedirect("https://" + req.getServerName() + ":8181/" + request.getServletContext().getContextPath());
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
	}
}
