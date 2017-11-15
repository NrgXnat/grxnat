package org.nrg.xnat.dicom

import org.dcm4che3.data.Attributes
import org.dcm4che3.data.ElementDictionary
import org.dcm4che3.io.DicomInputStream
import org.dcm4che3.net.ApplicationEntity
import org.dcm4che3.net.Connection
import org.dcm4che3.net.Device
import org.dcm4che3.tool.storescu.StoreSCU
import org.nrg.xnat.pogo.dicom.DicomScpReceiver

import java.nio.file.Files
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class CStore {

    DicomScpReceiver receiver
    final StoreSCU storeSCU

    static CStore to(DicomScpReceiver receiver) {
        new CStore(receiver)
    }

    static CStore to(String aeTitle, String host, int port) {
        to(new DicomScpReceiver().aeTitle(aeTitle).host(host).port(port))
    }

    CStore(DicomScpReceiver receiver) {
        setReceiver(receiver)
        final Device localDevice = new Device('GRXNAT')
        final Connection connection = new Connection()
        final ApplicationEntity localAE = new ApplicationEntity('GRXNAT')
        localDevice.addApplicationEntity(localAE)
        localDevice.addConnection(connection)
        localAE.addConnection(connection)
        storeSCU = new StoreSCU(localAE)
        final ExecutorService executorService = Executors.newSingleThreadExecutor()
        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        localDevice.setExecutor(executorService)
        localDevice.setScheduledExecutor(scheduledExecutorService)
        storeSCU.getAAssociateRQ().setCalledAET(receiver.aeTitle)
        final Connection remoteConnection = storeSCU.getRemoteConnection()
        remoteConnection.setHostname(receiver.host)
        remoteConnection.setPort(receiver.port)
    }

    CStore directory(File scannedDirectory) {
        directories([scannedDirectory])
    }

    CStore directories(List<File> scannedDirectories) {
        storeSCU.scanFiles(scannedDirectories.collect { it.absolutePath }, false)
        this
    }

    CStore file(File fileToAdd) {
        files([fileToAdd])
    }

    CStore files(List<File> filesToAdd) {
        final File tempFile = Files.createTempFile('storefilelist', 'txt').toFile()
        tempFile.deleteOnExit()
        storeSCU.setTmpFile(tempFile)
        final BufferedWriter fileInfos = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile)))
        filesToAdd.each { file ->
            final DicomInputStream dicomInputStream = new DicomInputStream(file)
            final Attributes fileMetaInfo = dicomInputStream.readFileMetaInformation()
            final long datasetPosition = dicomInputStream.getPosition()
            final Attributes dataset = dicomInputStream.readDataset(-1, -1)
            storeSCU.addFile(fileInfos, file, datasetPosition, fileMetaInfo, dataset)
            dicomInputStream.close()
        }
        fileInfos.close()
        this
    }

    CStore headers(Attributes attributes) {
        storeSCU.setAttributes(attributes)
        this
    }

    CStore headers(Map<Integer, String> headerMap) {
        final Attributes attributes = new Attributes()
        headerMap.each { tag, value ->
            attributes.setString(tag, ElementDictionary.vrOf(tag, null), value)
        }
        headers(attributes)
    }

    void send() {
        storeSCU.open()
        storeSCU.sendFiles()
        storeSCU.close()
    }

}
