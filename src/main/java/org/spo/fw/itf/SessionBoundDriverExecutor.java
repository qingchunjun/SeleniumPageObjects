package org.spo.fw.itf;

import org.openqa.selenium.WebDriver;
import org.spo.fw.exception.SPOException;



public interface SessionBoundDriverExecutor {

	public void create(String browser, String testName) throws SPOException ;
	
	public WebDriver getDriver();
	
	
	public void create(WebDriver driver);
}
