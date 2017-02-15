package manager.webdebug;

import java.util.HashMap;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {

	private static final Logger LOG = LoggerFactory.getLogger(WebServer.class);
	
	private static HashMap<String, Object> savedData = new HashMap<String,Object>();
	
	
	@SuppressWarnings("unchecked")
	public static synchronized <T> T getData(String key)
	{
	    if(!savedData.containsKey(key))
	        return null;
	    
	    return (T)savedData.get(key);
	}
	
	public static synchronized boolean contains(String key)
	{
	    return savedData.containsKey(key);
	}
	
	public static synchronized void setData(String key, Object obj)
	{
	    savedData.put(key, obj);
	}
	
	public void StartJetty(int port, String ContextPath)
		{
		    for (String s : se.sics.kompics.simulator.instrumentation.CodeInstrumentation.instrumentationExceptedClass) {
                LOG.debug(s);
            }
		    //se.sics.kompics.simulator.instrumentation.CodeInstrumentation.instrumentationExceptedClass.add("se.kth.news.webserver.WebServer");
		    //se.sics.kompics.simulator.instrumentation.CodeInstrumentation.instrumentationExceptedClass.add("org.eclipse.jetty.server.Server");
		    //se.sics.kompics.simulator.instrumentation.CodeInstrumentation.instrumentationExceptedClass.add("org.eclipse.jetty.server.ShutdownMonitor");
		    
		    
		    
		    LOG.error("Starting jetty!");
			try {
					Server server = new Server(port);
					
					// 2. Creating the WebAppContext for the created content
					WebAppContext ctx = new WebAppContext();
					ctx.setResourceBase("src/main/webapp");
					ctx.setContextPath(ContextPath);
					
					//3. Including the JSTL jars for the webapp.
					ctx.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",".*/[^/]*jstl.*\\.jar$");
				
					//4. Enabling the Annotation based configuration
					org.eclipse.jetty.webapp.Configuration.ClassList classlist = org.eclipse.jetty.webapp.Configuration.ClassList.setServerDefault(server);
			        classlist.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration", "org.eclipse.jetty.plus.webapp.EnvConfiguration", "org.eclipse.jetty.plus.webapp.PlusConfiguration");
			        classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", "org.eclipse.jetty.annotations.AnnotationConfiguration");
					
			        server.setHandler(ctx);
			  
			        server.start();
			        //server.join();
				} catch(Exception e) {
					LOG.error("Jetty error: {}", e.toString());
				}
		}
}
