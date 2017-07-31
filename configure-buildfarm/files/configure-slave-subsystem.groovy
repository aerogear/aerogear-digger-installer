import jenkins.security.s2m.*
import jenkins.model.*

def currentState = Jenkins.instance.injector.getInstance(AdminWhitelistRule.class).getMasterKillSwitch()

	if(currentState) {
	Jenkins.instance.injector.getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false);
	Jenkins.instance.save()
	println("Done")
	} else {
	println("Nothing to do!")
	}


