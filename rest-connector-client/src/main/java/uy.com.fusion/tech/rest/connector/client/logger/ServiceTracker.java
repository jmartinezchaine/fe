package uy.com.fusion.tech.rest.connector.client.logger;

import java.util.Date;


public interface ServiceTracker {
    public void log(Date date, String description, String serviceType, Object debug, Object data);
}
