package org.nrg.xnat.jackson.mappers

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator

class YamlObjectMapper extends ObjectMapper {
    YamlObjectMapper() {
        super(constructYamlFactory())
        enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
        disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        disable(SerializationFeature.WRITE_NULL_MAP_VALUES)
        enable(JsonParser.Feature.ALLOW_YAML_COMMENTS)
        enable(SerializationFeature.INDENT_OUTPUT)
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    private static YAMLFactory constructYamlFactory() {
        final YAMLFactory yamlFactory = new YAMLFactory()
        yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        yamlFactory.disable(YAMLGenerator.Feature.SPLIT_LINES)
        yamlFactory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
        yamlFactory
    }
}
