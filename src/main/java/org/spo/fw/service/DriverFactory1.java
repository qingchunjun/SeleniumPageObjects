package org.spo.fw.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.spo.fw.config.Constants;
import org.spo.fw.config.RunStrategy;
import org.spo.fw.config.SessionContext;
import org.spo.fw.exception.SPOException;
import org.spo.fw.exception.ServiceLifeCycleException;
import org.spo.fw.log.Logger1;
import org.spo.fw.service.proxy.ProxyServerController;


//@TempCheckout
public class DriverFactory1{
	public static LinkedList<WebDriver> driverQ = new LinkedList<WebDriver>();

	static WebDriver instance_1= null;
	static WebDriver instance_2= null;
	private static boolean isHtmlUnit=false; 
	private static boolean isIE=false;
	private static boolean isFireFox = false;
	private static boolean isChrome=false;
	public static DesiredCapabilities capabilitiesPhantom = DesiredCapabilities.phantomjs();
	static DesiredCapabilities capabilitiesChrome = DesiredCapabilities.chrome();
	static DesiredCapabilities capabilitiesIE = DesiredCapabilities.internetExplorer();
	static DesiredCapabilities capabilitiesFF = DesiredCapabilities.firefox();
	private static RunStrategy runStrategy;
	private static Logger1 log=new Logger1("DriverFactory");//FIXME : Slightly risky code, a static declaration once caused initialization issue, 

	private static  Constants.LifeCycleState state=Constants.LifeCycleState.NULL;



	public static Constants.LifeCycleState getState() {
		return state;
	}
	private static void configProxyCapabilities() throws Exception{

		if(runStrategy.isProxyServerRequired){
			ProxyServerController.init(runStrategy);
			capabilitiesIE.setCapability(CapabilityType.PROXY, ProxyServerController.getProxy());
			capabilitiesChrome.setCapability(CapabilityType.PROXY, ProxyServerController.getProxy());
			capabilitiesPhantom.setCapability(CapabilityType.PROXY, ProxyServerController.getProxy());
			capabilitiesFF.setCapability(CapabilityType.PROXY, ProxyServerController.getProxy());
		}

	}
	public static WebDriver instance() throws SPOException{
		return instance_1;
		
	}



	public synchronized static void stop() throws SPOException{}

	public static synchronized void init(RunStrategy strategy) throws SPOException{
		Properties p = new Properties();
	    try {
			p.load(new BufferedReader(new FileReader(new File(strategy.textFilesPath+"system.properties"))));
		} catch (FileNotFoundException e1) {
			//e1.printStackTrace();
			log.error("The file system.properties need to be present in your working directory defined by your strategy.textFilesPath");
		
		}catch (IOException e1) {
			//e1.printStackTrace();
			log.error("The file system.properties there but  not read");
		
		} 
	    for (String name : p.stringPropertyNames()) {
	        String value = p.getProperty(name);
	        System.setProperty(name, value);
	    }
		
		log = new Logger1("org.spo.fw.service.DriverFactory");
		//log = new Logger1(DriverFactory.class.getName());
		if(state==Constants.LifeCycleState.NULL || state==Constants.LifeCycleState.STOPPED ){
			log.debug("Init driver factory with "+strategy.browserName);
			runStrategy = strategy;
			state=Constants.LifeCycleState.STARTED;
		}else if (state==Constants.LifeCycleState.READY || state==Constants.LifeCycleState.STARTED){
			log.error("WARNING: DRIVER FACTORY REINITIALIZED !!! Init driver factory with "+strategy.browserName);
			stop();
			runStrategy = strategy;			
			state=Constants.LifeCycleState.STARTED;
		}else{
			log.error("service life cycle exception");
			throw new ServiceLifeCycleException();
		}


		try {
			if(runStrategy.browserName.contains("html")){
				isHtmlUnit=true;
			}else if (runStrategy.browserName.contains("chrome")){
				isChrome=true;
				LoggingPreferences prefs = new LoggingPreferences();
				prefs.enable(LogType.BROWSER, Level.ALL);
				capabilitiesChrome.setCapability(CapabilityType.LOGGING_PREFS, prefs);
				configProxyCapabilities();

			}else if (runStrategy.browserName.contains("ie")){
				isIE=true;

				LoggingPreferences prefs = new LoggingPreferences();
				prefs.enable(LogType.BROWSER, Level.ALL);

				capabilitiesIE.setCapability(CapabilityType.LOGGING_PREFS, prefs);

				configProxyCapabilities();
			}else if (runStrategy.browserName.contains("firefox")){
				isFireFox=true;

				LoggingPreferences prefs = new LoggingPreferences();
				prefs.enable(LogType.BROWSER, Level.ALL);
				capabilitiesFF.setCapability(CapabilityType.LOGGING_PREFS, prefs);

				configProxyCapabilities();
			}


		} catch (Exception e) {

			e.printStackTrace();
		} 

		//java.util.logging.Logger.getLogger("org.openqa.selenium.phantomjs.PhantomJSDriverService").setLevel(java.util.logging.Level.SEVERE);
		capabilitiesPhantom.setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS,new String[]{ "--webdriver-loglevel=OFF" ,"--acceptSslCerts=True" ,"--web-security=no","--ignore-ssl-errors=yes"});
		capabilitiesPhantom.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS,new String[]{ "--webdriver-loglevel=OFF","--web-security=no","--ignore-ssl-errors=yes" });
		//capabilitiesPhantom.setCapability(CapabilityType.SUPPORTS_ALERTS,true);
		capabilitiesPhantom.setCapability("phantomjs.page.settings.userAgent","Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543a Safari/419.3");
		capabilitiesPhantom.setCapability(CapabilityType.TAKES_SCREENSHOT, "false");
		capabilitiesPhantom.setCapability(CapabilityType.ACCEPT_SSL_CERTS, "True");
		capabilitiesIE.setCapability(InternetExplorerDriverService.IE_DRIVER_LOGFILE_PROPERTY, "C:/ie.log");
		
		
		WebDriver proxy = null;
		if(state==Constants.LifeCycleState.STARTED ||state==Constants.LifeCycleState.READY){			
			state=Constants.LifeCycleState.READY;
		}else{
			throw new ServiceLifeCycleException();
		}		
		
		log.trace("Instance with runStrategy browsername as :"+runStrategy.browserName);
		if(driverQ.size()<SessionContext.appConfig.DRIVER_POOL_SIZE){
			//firstCall = false;			
			log.trace("Q size of" +driverQ.size()+"  Within pool size, hence removing none");
		}else{
			driverQ.peek().quit();
			driverQ.poll();	
			log.trace("Exceeded pool size, removing one");
		}

		if(isHtmlUnit){
			instance_1= new HtmlUnitDriver(true) ;
			instance_2= new HtmlUnitDriver(true) ;
		}else if(isChrome){
		instance_1 = new ChromeDriver(capabilitiesChrome);
			instance_2 = new ChromeDriver(capabilitiesChrome);
		}else if(isIE){
			instance_1 = new InternetExplorerDriver(capabilitiesIE);
		instance_2 = new InternetExplorerDriver(capabilitiesIE);
		}else if(isFireFox){

		
			FirefoxProfile firefoxProfile = new ProfilesIni().getProfile("default");
			//			File pluginAutoAuth = new File("C:\\Program Files (x86)\\Mozilla Firefox\\autoauth-2.1-fx+fn.xpi");
			//			try {
			//				firefoxProfile.addExtension(pluginAutoAuth);
			//			} catch (IOException e) {				
			//				e.printStackTrace();
			//			}
			//firefoxProfile.setPreference(FireFoxPreferences., value)
			//FirefoxDriver  ffDriver =  new FirefoxDriver(firefoxProfile);
			//ffDriver.
			instance_1= new FirefoxDriver(firefoxProfile);
			instance_2= new FirefoxDriver(firefoxProfile);
			//new FirefoxDriver(capabilitiesFF);



		}else{
			instance_1=new PhantomJSDriver(capabilitiesPhantom);
			instance_2=new PhantomJSDriver(capabilitiesPhantom);
		}
		//proxy = new WebDriverProxy(instance_1);//No need for proxy at this point, may be explreo future use
		proxy=instance_1;
		driverQ.add(proxy);
		//log.error("Returning element "+POOL_SIZE/2+"/"+driverQ.size());		
		log.trace("Returning New element And adding to list of size: "+driverQ.size() );
		//return driverQ.get(Math.round(POOL_SIZE/2));
		log.trace("Driver reutrned is of type :"+proxy.toString());
		


	

	}



}
