package com.cyecize.domainrouter.api.options;

import lombok.Data;

import java.util.List;

@Data
public class RouteOption {
    private String host;
    private List<String> subdomains;
    private String destinationHost;
    private int destinationPort;
}
