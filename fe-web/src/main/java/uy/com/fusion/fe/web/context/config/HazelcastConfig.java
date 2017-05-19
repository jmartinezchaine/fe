package uy.com.fusion.fe.web.context.config;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapStore;

import uy.com.fusion.fe.web.util.JsonUtils;

@Configuration
public class HazelcastConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastConfig.class);

    @Value("${hazelcast.network.port:7701}")
    private int port;

    @Value("${hazelcast.heartbeat.interval.seconds:3}")
    private String heartbeatInterval;

    @Value("${hazelcast.max.no.heartbeat.seconds:30}")
    private String heartbeatSeconds;

    @Value("${hazelcast.merge.first.run.delay.seconds:15}")
    private String firstRunDelay;

    @Value("${hazelcast.merge.next.run.delay.seconds:10}")
    private String nextRunDelay;

    @Value("${hazelcast.interface:127.0.0.1}")
    private String hazelcastInterface;

    @Value("${hazelcast.mapstore.delay:0}")
    private int mapStoreDelay;

    @Value("${hazelcast.mapstore.binary:false}")
    private String binary;

    @Value("${hazelcast.mapstore.statistics-enabled:true}")
    private boolean statisticsEnabled;

    @Value("${hazelcast.mapstore.bulk-load:100}")
    private String bulkLoad;

    @Value("${hazelcast.mapstore.backup-count:1}")
    private int backupCount;

    @Value("${hazelcast.mapstore.map-input-name:inputMap}")
    private String inputMap;

    @Value("${hazelcast.mapstore.map-task-name:taskMap}")
    private String mapTaskName;

    @Value("${hazelcast.mapstore.map-processor-name:processorMap}")
    private String mapProcessorName;

    @Value("${hazelcast.map.product.eviction.max.size:10000}")
    private int productMaxSize;

    @Autowired
    private JsonUtils jsonUtils;

    @Bean
    public HazelcastInstance getHazelcast() {

        List<String> joinTcpIpMembers;
        String publicAddress;
        LOGGER.info("Hazelcast Init - cluster info is null");
        publicAddress = null;
        joinTcpIpMembers = Arrays.asList("127.0.0.1");


        // Configure

        Config cfg = new Config();
        cfg.setProperty("hazelcast.heartbeat.interval.seconds", this.heartbeatInterval);
        cfg.setProperty("hazelcast.max.no.heartbeat.seconds", this.heartbeatSeconds);
        cfg.setProperty("hazelcast.merge.first.run.delay.seconds", this.firstRunDelay);
        cfg.setProperty("hazelcast.merge.next.run.delay.seconds", this.nextRunDelay);
        cfg.setProperty("hazelcast.logging.type", "slf4j");
        cfg.setProperty("hazelcast.map.entry.filtering.natural.event.types", "true");


        // TaskMap
        MapConfig taskMapConfig = this.mapConfiguration(cfg, this.mapTaskName, this.backupCount, this.statisticsEnabled, this.mapStoreDelay, null);
        this.addEvictionConfiguration(taskMapConfig);

        // InputMapManager
        MapConfig inputMapConfig = this.mapConfiguration(cfg, this.inputMap, this.backupCount, this.statisticsEnabled, this.mapStoreDelay, null);
        this.addEvictionConfiguration(inputMapConfig);
        this.addIndexConfiguration(inputMapConfig);

        // ProcessorMap
        this.mapConfiguration(cfg, this.mapProcessorName, this.backupCount, this.statisticsEnabled, this.mapStoreDelay, null);

        NetworkConfig network = cfg.getNetworkConfig();
        network.setPort(this.port).setPortAutoIncrement(true);
        if (publicAddress != null) {
            LOGGER.info("Hazelcast Init - setting public address: " + publicAddress);
            network.setPublicAddress(publicAddress);
        }

        network.getInterfaces().addInterface(this.hazelcastInterface).setEnabled(true);

        JoinConfig join = network.getJoin();

        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().setRequiredMember(null).setMembers(joinTcpIpMembers).setEnabled(true);

        HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);

        return instance;
    }

    private void addEvictionConfiguration(MapConfig mapConfig) {
        mapConfig.setEvictionPolicy(EvictionPolicy.LRU);
        MaxSizeConfig sizeConfig = new MaxSizeConfig(997, MaxSizeConfig.MaxSizePolicy.PER_NODE);
        mapConfig.setMaxSizeConfig(sizeConfig);
        mapConfig.setMaxIdleSeconds(3600);
    }

    private void addIndexConfiguration(MapConfig productMapConfig) {
        MapIndexConfig typeIndexConfig = new MapIndexConfig();
        typeIndexConfig.setOrdered(false).setAttribute("type");
        productMapConfig.addMapIndexConfig(typeIndexConfig);

        MapIndexConfig stateIndexConfig = new MapIndexConfig();
        typeIndexConfig.setOrdered(false).setAttribute("state");
        productMapConfig.addMapIndexConfig(stateIndexConfig);

        MapIndexConfig retriesIndexConfig = new MapIndexConfig();
        typeIndexConfig.setOrdered(true).setAttribute("retries");
        productMapConfig.addMapIndexConfig(retriesIndexConfig);

    }


    private MapConfig mapConfiguration(Config cfg, String name, int backupCount, boolean statisticsEnabled, int mapStoreDelay, MapStore implementationStore) {
        MapConfig mapConfig = cfg.getMapConfig(name);
        mapConfig.setBackupCount(backupCount);
        mapConfig.setInMemoryFormat(InMemoryFormat.OBJECT);
        mapConfig.setStatisticsEnabled(statisticsEnabled);


        if (implementationStore != null) {
            MapStoreConfig mapStoreConfig = new MapStoreConfig();
            mapStoreConfig.setEnabled(true);
            mapStoreConfig.setImplementation(implementationStore);
            mapStoreConfig.setWriteDelaySeconds(mapStoreDelay);
            mapStoreConfig.setWriteBatchSize(100);
            mapStoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.LAZY);
            mapConfig.setMapStoreConfig(mapStoreConfig);
        }
        return mapConfig;
    }
}
