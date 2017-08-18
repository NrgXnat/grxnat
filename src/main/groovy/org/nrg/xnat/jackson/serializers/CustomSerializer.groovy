package org.nrg.xnat.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.apache.commons.lang3.StringUtils
import org.nrg.xnat.pogo.resources.Resource

@SuppressWarnings("GrMethodMayBeStatic")
abstract class CustomSerializer<T> extends StdSerializer<T> {

    protected CustomSerializer(Class<T> aClass) {
        super(aClass)
    }

    protected CustomSerializer() {
        this(null)
    }

    protected writeStringFieldIfNonnull(JsonGenerator generator, String field, String value) {
        if (value != null) generator.writeStringField(field, value)
    }

    protected writeListAsSpacedString(JsonGenerator generator, String field, List value) {
        if (value != null) generator.writeStringField(field, value.join(' '))
    }

    protected writeBooleanFieldIfTrue(JsonGenerator generator, String field, boolean value) {
        if (value) generator.writeBooleanField(field, value)
    }

    protected writeStringFieldIfNonempty(JsonGenerator generator, String field, String value) {
        if (StringUtils.isNotEmpty(value)) generator.writeStringField(field, value)
    }

    protected writeEnumToString(JsonGenerator generator, String field, Object value) {
        if (value != null) generator.writeStringField(field, value.toString())
    }

    protected writeIntFieldIfNonzero(JsonGenerator generator, String field, int value) {
        if (value != 0) generator.writeNumberField(field, value)
    }

    protected writeListFieldIfNonempty(JsonGenerator generator, String field, List list) {
        if (list != null && !list.isEmpty()) generator.writeObjectField(field, list)
    }

    protected writeObjectFieldIfNonnull(JsonGenerator generator, String field, Object object) {
        if (object != null) generator.writeObjectField(field, object)
    }

    protected writeObjectListIfNonempty(JsonGenerator generator, String field, List objects) {
        if (objects != null && !objects.isEmpty()) {
            generator.writeObjectFieldStart(field)
            objects.each { object ->
                generator.writeObject(object)
            }
            generator.writeEndObject()
        }
    }

    protected writeXnatResources(JsonGenerator generator, List<Resource> resources) {
        writeObjectListIfNonempty(generator, 'resources', resources)
    }

}
