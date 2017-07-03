#!/usr/bin/env groovy

import groovy.json.JsonSlurper;
import jenkins.model.*
import java.util.ArrayList

/**
 * Script accepts a JSON formatted string as an arg and checks if the
 * plugins provided are currently installed and their version is met.
 * The name of any plugin whose minimum version is not met or where the plugin
 * is not intalled is logged to stdout.
 */


def jsonParser = new JsonSlurper()
def pluginList = jsonParser.parseText(args[0])


pluginList.each {
	requiredPlugin ->
		def found = false
		for (installedPlugin in Jenkins.instance.pluginManager.plugins) {
			if ("${requiredPlugin.name}" == "${installedPlugin.getShortName()}") {
				found = true
				def requiredVersion = "${requiredPlugin.version}".tokenize(".")
				def currentVersion = "${installedPlugin.getVersion()}".tokenize(".")
				for (i=0; i<requiredVersion.size(); i++) {
					if (requiredVersion[i].toInteger() > currentVersion[i].toInteger()) {
						println("${requiredPlugin.name}\n")
						break
					}
				}
			}
		}
		if (!found) {
			println("${requiredPlugin.name}\n")
		}
}



