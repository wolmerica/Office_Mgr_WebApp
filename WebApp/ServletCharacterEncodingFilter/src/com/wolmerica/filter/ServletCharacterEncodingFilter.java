/*
 * ServletCharacterEncodingFilter.java
 *
 * Created on January 17, 2009, 01:10 PM
 *
 * in web.xml you add
 *
 * <filter>
 *   <filter-name>setCharacterEncoding</filter-name>
 *
 *   <filter-class>
 *     com.wolmerica.filter.ServletCharacterEncodingFilter
 *   </filter-class>
 *     <init-param>
 *       <param-name>encoding</param-name>
 *       <param-value>UTF-8</param-value>
 *     </init-param>
 * </filter>
 *
 */

package com.wolmerica.filter;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;

/**
 *
 * @author Richard
 */
public class ServletCharacterEncodingFilter implements Filter {

  private FilterConfig config;
  /** Creates a new instance of SetCharacterEncodingFilter */

  public ServletCharacterEncodingFilter() { }

  public void doFilter(javax.servlet.ServletRequest servletRequest,
                       javax.servlet.ServletResponse servletResponse,
                       javax.servlet.FilterChain filterChain)
   throws java.io.IOException, javax.servlet.ServletException {

    String charset = config.getInitParameter("encoding");
    // System.out.println("getFilterName() = " + config.getFilterName());
    // System.out.println("charset parameter = " + charset);
    servletRequest.setCharacterEncoding(config.getInitParameter("encoding"));
    servletResponse.setCharacterEncoding(config.getInitParameter("encoding"));
    
    filterChain.doFilter(servletRequest, servletResponse);
  }

  public void destroy() { }

  public void init(javax.servlet.FilterConfig filterConfig)
   throws javax.servlet.ServletException {
    config = filterConfig;
  }

}
