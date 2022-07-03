package com.cyecize.domainrouter.constants;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class General {
    private final static String START_UP_PACKAGE_PATH = General.class.getName()
            .replace(General.class.getSimpleName(), "")
            .replaceAll("\\.", "/");

    public static final String WORKING_DIRECTORY = URLDecoder.decode(General.class.getResource("").toString()
            .replace("file:/", "")
            .replace(START_UP_PACKAGE_PATH, ""), StandardCharsets.UTF_8);

    public static final String SETTINGS_FILE_NAME = "options.json";

    public static final String ENV_VAR_OPTIONS_NAME = "options";
}
