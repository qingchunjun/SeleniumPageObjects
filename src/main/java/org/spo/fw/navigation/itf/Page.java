package org.spo.fw.navigation.itf;

import org.spo.fw.itf.SessionBoundDriverExecutor;
import org.spo.fw.web.KeyWords;

public interface Page{
		void setLastPage(boolean isLast);
		boolean isLastPage();
		public boolean isReady();//equivaluent to $.ready() in jquery.
		
		void setState(String stateExpression, SessionBoundDriverExecutor driver);
		void followLink(NavLink link, SessionBoundDriverExecutor executor) throws NavException;
		
		String getStateExpression();
		void setStateExpression(String stateExpression);
		
		void setName(String name);
		String getName();
		void setUrl(String url);
		
		String getIdentifier();
		String getFormData(KeyWords kw);
		
		void init();
	}