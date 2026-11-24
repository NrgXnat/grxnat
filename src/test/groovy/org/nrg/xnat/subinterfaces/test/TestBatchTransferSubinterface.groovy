package org.nrg.xnat.subinterfaces.test

import org.nrg.xnat.interfaces.XnatInterface
import org.nrg.xnat.pogo.XnatPlugin
import org.nrg.xnat.pogo.transfer.TransferRunStatus
import org.nrg.xnat.subinterfaces.BatchTransferSubinterface
import org.nrg.xnat.subinterfaces.XnatFunctionalitySubinterface
import org.testng.annotations.Test

import java.lang.reflect.Method
import java.lang.reflect.Modifier

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue
import static org.testng.Assert.expectThrows

class TestBatchTransferSubinterface {

    /**
     * Tests call the client through XnatInterface, which delegates to every subinterface. When two subinterfaces have a
     * method with the same signature, XnatInterface calls only the first, so the client's names must be its own.
     */
    @Test
    void testClientMethodsAreDelegatedToThisClient() {
        final List<String> clientMethods = BatchTransferSubinterface.declaredMethods.findAll { method ->
            Modifier.isPublic(method.modifiers) && !Modifier.isStatic(method.modifiers) && !method.synthetic &&
                    !method.name.contains('$') && !(method.name in ['getHandledEndpoints', 'getMetaClass', 'setMetaClass', 'invokeMethod', 'getProperty', 'setProperty'])
        }.collect { signature(it) }
        assertTrue('waitForTransferRun(String, TransferRunStatus)' in clientMethods, clientMethods.toString())

        final Set<String> onXnatInterface = XnatInterface.methods.collect { signature(it) } as Set
        assertEquals(clientMethods.findAll { !(it in onXnatInterface) }, [])

        final Set<String> onOtherSubinterfaces = XnatInterface.declaredFields.findAll { field ->
            XnatFunctionalitySubinterface.isAssignableFrom(field.type) && field.type != BatchTransferSubinterface
        }.collectMany { field ->
            field.type.methods.collect { signature(it) }
        } as Set
        assertEquals(clientMethods.findAll { it in onOtherSubinterfaces }, [])
    }

    /**
     * Without the plugin, every call fails with a message naming it. The calls go through XnatInterface, which also
     * checks that its delegating copies can evaluate the client's default arguments.
     */
    @Test
    void testCallsFailClearlyWithoutThePlugin() {
        final XnatInterface withoutPlugin = new XnatInterface() {
            @Override
            List<XnatPlugin> readInstalledPlugins() {
                []
            }
        }
        withoutPlugin.@batchTransferSubinterface = new BatchTransferSubinterface(xnatInterface: withoutPlugin)

        final List<Closure> calls = [
                { withoutPlugin.readTransferCapabilities() },
                { withoutPlugin.launchBatchTransfer([]) },
                { withoutPlugin.validateTransferScript('version "6.3"') },
                { withoutPlugin.validateTransferManifestUpload('SRC', 'source_subject_label\n') },
                { withoutPlugin.readTransferHistory() },
                { withoutPlugin.readTransferHistory('SRC') },
                { withoutPlugin.readTransferRun('batch_transfer_1') },
                { withoutPlugin.waitForTransferRun('batch_transfer_1') },
                { withoutPlugin.waitForTransferRun('batch_transfer_1', TransferRunStatus.COMPLETED) },
                { withoutPlugin.stopTransferRun('batch_transfer_1') }
        ]
        calls.each { call ->
            final UnsupportedOperationException exception = expectThrows(UnsupportedOperationException) {
                call()
            }
            assertTrue(exception.message.contains("'batchTransferPlugin'"), exception.message)
        }
    }

    private static String signature(Method method) {
        "${method.name}(${method.parameterTypes*.simpleName.join(', ')})".toString()
    }

}
