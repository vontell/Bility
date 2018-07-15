package io.github.ama_csail.ama.util.storage;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A collection of properties regarding the functioning of the AMA library on different devices.
 * For example, this class provides functionality for determining if a given device will run AMA
 * @author Aaron Vontell
 */
public class SystemConfig {


    public static final String VERSION = "0.0.1";
    public static final String HOMEPAGE = "http://ama-csail.github.io";
    public static final String API_HOOK = "http://api.ama-project.com";
    public static final String STORAGE_LOCATION = "ama/storage/";
    public static final String FURTHER_INFO = "http://ama-project.com";
    public static final String UPDATE_ENDPOINT = "http://api.ama-project.com/update";


    private static final Map<String, String> endpoints = new LinkedHashMap<>();
    // TODO: Add endpoints here

    public static final Map<String, String> ENDPOINTS =
            Collections.unmodifiableMap(new LinkedHashMap<>(endpoints));

}
