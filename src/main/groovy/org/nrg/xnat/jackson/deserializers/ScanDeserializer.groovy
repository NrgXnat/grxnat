package org.nrg.xnat.jackson.deserializers

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.JsonNode
import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.experiments.Scan

class ScanDeserializer extends CustomDeserializer<Scan> {

    @Override
    Scan deserialize(ObjectCodec codec, JsonNode node) throws IOException, JsonProcessingException {
        final Scan scan

        if (fieldNonnull(node, 'xsiType')) {
            final DataType dataType = DataType.lookup(node.get('xsiType').asText())
            final Class<? extends Scan> associated = dataType.associatedScanClass
            if (associated != null) {
                scan = associated.newInstance()
            } else {
                scan = new Scan()
                scan.setXsiType(dataType.xsiType)
            }
        } else {
            scan = new Scan()
        }

        setStringIfNonnull(node, 'ID', scan.&setId)
        setStringIfNonnull(node, 'series_description', scan.&setSeriesDescription)
        setStringIfNonnull(node, 'type', scan.&setType)
        setStringIfNonnull(node, 'note', scan.&setNote)
        setStringIfNonnull(node, 'quality', scan.&setQuality)

        scan
    }
}
