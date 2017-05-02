package com.haizhi.bqd.service.factory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by chenbo on 17/4/13.
 */
@Slf4j
@Data
@AllArgsConstructor
public class ESClientFactory {

    private static final String HOSTS = "hosts";
    private static final String CLUSTER_NAME = "cluster.name";

    public static Client generate(String clusterName, String hosts) {
        Settings settings = Settings.builder()
                .put(CLUSTER_NAME, clusterName)
                .build();
//        Settings settings = Settings.builder()
//                .put("cluster.name", "myClusterName").build();
        TransportClient client = new PreBuiltTransportClient(settings);
        //2.4.x
//        TransportClient client = TransportClient.builder().settings(settings).build();

        for (String address : hosts.toString().split(",")) {
            if (address != null && !address.equals("")) {
                try {
                    client.addTransportAddress(
                            new InetSocketTransportAddress(
                                    InetAddress.getByName(address.split(":")[0].trim()),
                                    Integer.valueOf(address.split(":")[1].trim())));
                } catch (UnknownHostException e) {
                    log.error("{}", e);
                }
            }
        }
        return client;
    }
}
