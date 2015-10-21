package org.spo.fw.service.domain;

import java.util.List;

/**
 * 
 * @author Prem
 * Stateful Proxy to a domian Server , only closeSession will reset it to ground state.
 * This could be served up from any kind of implementation, but a preferred one would be a json document based restful server
 * The overall contract is that the domainsvc should have a state change based on events and send back text representations of page
 * baed on such state. 
 * The groovy templating mechanism could help here, run it as a jetty restful server suggestion
 * 
 *
 */
public interface StatefulDomainService {

	public String getState();
	public void openSession();
	public void reset();
	public void closeSession();
	
	public String event_domain(String Actor, String event);
	
	
	public List<String> getPage(String expression);
	
}
