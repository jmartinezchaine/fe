package uy.com.fusion.fe.web.persistence.cache;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hazelcast.core.IMap;

import uy.com.fusion.fe.web.api.document.DocumentEx;
import uy.com.fusion.fe.web.persistence.cache.util.AbstractManagerMap;
import uy.com.fusion.fe.web.persistence.cache.util.IMapManager;

/**
 * Created by didier on 22/11/16.
 */
@Component
public class InputMapManager
                extends AbstractManagerMap<DocumentEx>
                implements IMapManager<String, DocumentEx> {

    @Value("${hazelcast.mapstore.map-input-name}")
    private String mapName;

    @Override
    public IMap<String, DocumentEx> getMap() {
        return this.hazelcast.getMap(this.mapName);
    }

    public void update(DocumentEx p) {
        this.getMap().set(p.getId(), p);
    }

    public void update(DocumentEx value, int eviction_value, TimeUnit eviction_unit) {
        this.getMap().set(value.getId(), value, eviction_value, eviction_unit);
    }
}
