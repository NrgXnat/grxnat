package org.nrg.xnat.pogo.test

import org.nrg.xnat.pogo.Uptime
import org.testng.Assert
import org.testng.annotations.Test

class TestUptime {

    @Test
    void testUptimeCalc() {
        Assert.assertEquals(new Uptime(days: 1, hours: 1, minutes: 1, seconds: 1.0).totalSeconds(), 90061.0, 0.000001)
    }

}
