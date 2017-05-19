package uy.com.fusion.fe.web.persistence.cache.util;

import com.hazelcast.core.IMap;

/**
 * Created by didier on 24/11/16.
 */
public interface IMapManager<K, V> {

    IMap<K, V> getMap();
}
