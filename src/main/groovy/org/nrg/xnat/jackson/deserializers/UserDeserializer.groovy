package org.nrg.xnat.jackson.deserializers

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.JsonNode
import org.nrg.xnat.pogo.users.User

class UserDeserializer extends CustomDeserializer<User> {

    @Override
    User deserialize(ObjectCodec codec, JsonNode node) throws IOException, JsonProcessingException {
        final User user = new User()

        setStringIfNonnull(node, 'email', user.&setEmail)
        setStringIfNonnull(node, 'lastname', user.&setLastName)
        setStringIfNonnull(node, 'firstname', user.&setFirstName)
        setStringIfNonnull(node, 'login', user.&setUsername)

        user
    }

}
