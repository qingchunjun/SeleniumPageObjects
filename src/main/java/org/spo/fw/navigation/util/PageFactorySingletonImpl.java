package org.spo.fw.navigation.util;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;

import org.spo.fw.exception.SPOException;
import org.spo.fw.itf.ExtensibleService;
import org.spo.fw.log.Logger1;
import org.spo.fw.navigation.itf.Page;
import org.spo.fw.navigation.itf.PageFactory;
import org.spo.fw.navigation.itf.PageLayoutValidator;
import org.spo.fw.navigation.model.DefaultPage;
import org.spo.fw.navigation.model.MultiPageImpl;


public class PageFactorySingletonImpl implements PageFactory, ExtensibleService{
	protected String packageName="";
	protected Map<String,Page> pageSingletonCache = new LinkedHashMap<String,Page>();
	Logger1 log = new Logger1(this.getClass().getSimpleName());
	protected Map<String, PageLayoutValidator> validators = new LinkedHashMap<String,PageLayoutValidator>();

	@Override
	public void init() {


	}

	@Override
	public void addValidator(String pageNameRegex, PageLayoutValidator validator) {
		validators.put(pageNameRegex, validator);

	}
	
	@Override
	public void removeValidator(String pageNameRegex) {
		validators.remove(pageNameRegex);

	}

	public PageFactorySingletonImpl() {
		log.trace("Creating pagefactory : "+this.toString());
	}

	public  Page getPage(String name) throws SPOException{
		init();

		if(!packageName.endsWith(".")){
			packageName=packageName+".";
		}
		Page page = null;
		if(pageSingletonCache.containsKey(name)){
			page= pageSingletonCache.get(name);
		}
		else{
			try {
				@SuppressWarnings("rawtypes")
				Constructor constructor = Class.forName(packageName+name+"Page").getConstructor(null);
				page = (Page)constructor.newInstance(null);
				pageSingletonCache.put(name,page);
			} catch (ClassNotFoundException e) {
				page = new DefaultPage();
				//e.printStackTrace();
			}catch (Exception e) {
				throw new SPOException("An Exception was thrown trying to getPage object for  "+name+" : "+e.getClass().getName());
				//e.printStackTrace();
			}
		}
		page.init();
		boolean tryRegex=true;
		for(String key:validators.keySet()){
			if(name.equals(key)){
				page.setPageValidator(validators.get(key));
				tryRegex=false;
				break;
			}
		}
		if(tryRegex){
			for(String key:validators.keySet()){
				if(name.matches(key)){
					page.setPageValidator(validators.get(key));
					break;
				}
			}
		}
		return page;

	}
	public  Page getDefaultPage(){
		return new DefaultPage();
	}
	public  Page getMultiPage(){
		return new MultiPageImpl();
	}
}
