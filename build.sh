#!/usr/bin/env bash
native-image -cp target/fj.jar -H:Name=target/fj -H:IncludeResources='help.txt' -H:+ReportUnsupportedElementsAtRuntime --delay-class-initialization-to-runtime=org.mozilla.javascript.VMBridge --no-server com.gxk.fj.Main
