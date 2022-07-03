package com.cyecize.domainrouter.api.options;

import com.cyecize.domainrouter.constants.General;
import com.cyecize.ioc.annotations.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OptionsServiceImpl implements OptionsService {
    private final ObjectMapper objectMapper;

    @Override
    public List<RouteOption> getOptions() {
        final String options = System.getenv(General.ENV_VAR_OPTIONS_NAME);
        if (options == null) {
            log.info("Reading options from resource file.");
            return this.readFromFile();
        }

        log.info("Reading options from env variable {}.", General.ENV_VAR_OPTIONS_NAME);
        return this.readFromEnvVariable(options);
    }

    private List<RouteOption> readFromEnvVariable(String options) {
        try {
            return this.objectMapper
                    .readerForListOf(RouteOption.class)
                    .readValue(options);
        } catch (Exception ex) {
            log.error("Error while parsing the options file!", ex);
            return new ArrayList<>();
        }
    }

    private List<RouteOption> readFromFile() {
        try (InputStream inputStream = new FileInputStream(General.WORKING_DIRECTORY + General.SETTINGS_FILE_NAME)) {
            try {
                return this.objectMapper
                        .readerForListOf(RouteOption.class)
                        .readValue(inputStream);
            } catch (Exception ex) {
                log.error("Error while parsing the options file!", ex);
                return new ArrayList<>();
            }
        } catch (IOException ex) {
            log.error("Error while reading the options file!", ex);
            return new ArrayList<>();
        }
    }
}
