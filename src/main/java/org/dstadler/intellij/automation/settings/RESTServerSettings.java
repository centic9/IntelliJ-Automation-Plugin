package org.dstadler.intellij.automation.settings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.security.Principal;

@XmlAccessorType(XmlAccessType.FIELD)
public class RESTServerSettings implements Principal {
    private static final int DEFAULT_REST_PORT = 10081;

    private int port = DEFAULT_REST_PORT;

    public synchronized int getPort() {
        return this.port;
    }

    public synchronized void setPort(int port) {
        this.port = port;
    }

    @Override
    public String getName() {
        return Integer.toString(this.port);
    }
}