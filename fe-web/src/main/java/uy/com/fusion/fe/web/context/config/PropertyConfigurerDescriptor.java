package uy.com.fusion.fe.web.context.config;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.io.Resource;

/**
 * Created by juanmartinez on 18/5/17.
 */
public class PropertyConfigurerDescriptor
                implements PriorityOrdered, BeanNameAware {

    private Resource applicationResource;
    private List<Resource> applicationResources;
    private Resource environmentResource;
    private List<Resource> environmentResources;

    private String beanName;
    private int order;

    public void setApplicationResource(Resource appResource) {
        this.applicationResource = appResource;
    }

    public void setApplicationResources(List<Resource> appResources) {
        this.applicationResources = appResources;
    }

    /**
     * Composes the application resource list by mixing the values of {@link #applicationResource} and
     * {@link #applicationResources} in one list, in that order.
     *
     * @return
     */
    public List<Resource> getApplicationResources() {
        final List<Resource> composedList = new LinkedList<Resource>();
        if (this.applicationResource != null) {
            composedList.add(this.applicationResource);
        }
        if (this.applicationResources != null) {
            composedList.addAll(this.applicationResources);
        }
        return composedList;
    }

    public void setEnvironmentResource(Resource environmentResource) {
        this.environmentResource = environmentResource;
    }

    public void setEnvironmentResources(List<Resource> envResources) {
        this.environmentResources = envResources;
    }

    /**
     * Composes the application resource list by mixing the values of {@link #environmentResource} and
     * {@link #environmentResources} in one list, in that order.
     *
     * @return
     */
    public List<Resource> getEnvironmentResources() {
        final List<Resource> composedList = new LinkedList<Resource>();
        if (this.environmentResource != null) {
            composedList.add(this.environmentResource);
        }
        if (this.environmentResources != null) {
            composedList.addAll(this.environmentResources);
        }
        return composedList;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }

    public String getBeanName() {
        return this.beanName;
    }
}
