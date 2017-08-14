package org.nrg.xnat.jackson.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import org.nrg.xnat.pogo.users.User

class UserSerializer extends CustomSerializer<User> {

    @Override
    void serialize(User value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject()

        writeStringFieldIfNonempty(gen, 'username', value.username)
        writeStringFieldIfNonempty(gen, 'password', value.password)
        writeStringFieldIfNonempty(gen, 'email', value.email)
        writeStringFieldIfNonempty(gen, 'firstName', value.firstName)
        writeStringFieldIfNonempty(gen, 'lastName', value.lastName)
        gen.writeBooleanField('verified', value.verified)
        gen.writeBooleanField('enabled', value.enabled)

        gen.writeEndObject()
    }
}
