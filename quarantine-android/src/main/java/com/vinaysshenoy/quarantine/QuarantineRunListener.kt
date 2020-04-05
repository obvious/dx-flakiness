package com.vinaysshenoy.quarantine

import android.app.Instrumentation
import android.os.Bundle
import androidx.test.internal.runner.listener.InstrumentationRunListener
import org.junit.runner.Result
import java.io.PrintStream

class QuarantineRunListener : InstrumentationRunListener() {

    override fun setInstrumentation(instr: Instrumentation) {
        super.setInstrumentation(instr)
        Quarantine.classLoader = instr.context.classLoader
    }

    override fun instrumentationRunFinished(
        streamResult: PrintStream,
        resultBundle: Bundle,
        junitResults: Result
    ) {
        if (Quarantine.isEnabled) {
            Quarantine.pushResults()
        }
    }
}