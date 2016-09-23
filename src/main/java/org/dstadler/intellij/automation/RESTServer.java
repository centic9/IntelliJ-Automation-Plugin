package org.dstadler.intellij.automation;

import com.intellij.openapi.application.ApplicationInfo;
import org.dstadler.commons.http.NanoHTTPD;
import org.dstadler.commons.logging.jdk.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class to create a tiny REST daemon and populate it with
 * some content to provide basic administrative actions.
 */
public class RESTServer {
	private static final Logger logger = LoggerFactory.make();

	private static final int REST_PORT = 10081;

	public static NanoHTTPD create(Map<String,ActionListener> actionListeners) throws IOException {
		logger.info("Starting REST Server");
		return new MyNanoHTTPD(REST_PORT, actionListeners);
	 }

	private static class MyNanoHTTPD extends NanoHTTPD {
		private final Map<String,ActionListener> actionListeners;

		public MyNanoHTTPD(int port, Map<String,ActionListener> actionListeners)
						throws IOException {
			super(port);

			this.actionListeners = actionListeners;
		}

		/**
		 * Method to provide the REST-response for the various known actions
		 */
		@Override
		public Response serve(String uri, String method, Properties header, Properties parms) {
			logger.info("Calling REST interface: " + uri);

			for(Map.Entry<String,ActionListener> entry : actionListeners.entrySet()) {
				final String actionName = entry.getKey();
				if(uri.equals("/" + actionName)) {
					try {
						long start = System.currentTimeMillis();
						ActionEvent e = new ActionEvent(this, 0, null);
						entry.getValue().actionPerformed(e);
						String msg = "<html><body>Executed " + actionName + ", took " + (System.currentTimeMillis() - start) + " ms</body></html>";
						return new Response(HTTP_OK, MIME_HTML, msg);
					} catch (IllegalStateException e) {
						logger.log(Level.WARNING, "Had Exception while executing " + actionName, e);

						String msg = "<html><body>Error while executing " + actionName + ": " + e.getMessage() + "</body></html>";
						return new Response(HTTP_INTERNALERROR, MIME_HTML, msg);
					}
				}
			}

			// additional hardcoded actions and default handling
			if(uri.equals("/Version")) {
				return new Response(HTTP_OK, MIME_HTML, "<html><body>" + ApplicationInfo.getInstance().getFullVersion() + "</body></html>");
			}

			// no match found => report an error
			String msg = "<html><body>Wrong link, sorry, I currently only know /Version and " + actionListeners.keySet() + "</body></html>";
			return new Response(HTTP_NOTFOUND, MIME_HTML, msg);
		}
	}
}
