package org.nrg.xnat.pogo

class Uptime {

    int days = 0
    int hours = 0
    int minutes = 0
    double seconds = 0

    double totalSeconds() {
        seconds + 60 * (minutes + 60 * (hours + 24 * (days)))
    }

}
