/**
* Pod Template Config
*/

import org.csanchez.jenkins.plugins.kubernetes.*
import org.csanchez.jenkins.plugins.kubernetes.volumes.*
import jenkins.model.*
import java.util.ArrayList
import java.util.List

def NAME="android"
def IMAGE="aerogear/jenkins-android-slave:dev"
def ENVAR_NAME="ANDROID_HOME"
def ENVAR_VALUE="/opt/android-sdk-linux"
def PVC_MOUNT="/opt/android-sdk-linux"
def PVC_NAME="android-sdk"
def WORKDIR="/tmp"
def CONTAINER_NAME="jnlp"
def ARGS='${computer.jnlpmac} ${computer.name}'

// get the jenkins instance
def instance = Jenkins.getInstance()

// we know that the PodTemplates are set in the first element
def templates =  instance.clouds.templates[0]
def cloud = instance.clouds
def bExists = false

if (args.length < 1) {
  println "[ERROR] Argument android-sdk-image:tag missing"
  println " [INFO] Usage groovy <path-to>/podtemplate-config.groovy <android-slave-image:tag>"
  return
} else {
  IMAGE=args[0]
}

// check to see if we have a pod set with name=android
templates.each {
  if (NAME == "${it.name}") {
    bExists = true
  }
}

if (bExists) {
  println "[INFO] Android pod template exists no action needed"
} else {
  println "[INFO] Configuring new android pod template"

  // we first setup the container template
  def c = new ContainerTemplate(IMAGE)
  c.setName(CONTAINER_NAME)
  c.setWorkingDir(WORKDIR)
  c.setArgs(ARGS)
  c.setTtyEnabled(true)
  containerList = new ArrayList<ContainerTemplate>()
  containerList.add(c)

  // now setup pod envars
  def pe = new PodEnvVar(ENVAR_NAME,ENVAR_VALUE)
  podenvarList = new ArrayList<PodEnvVar>()
  podenvarList.add(pe)

  // finally set up volumes
  def pvc = new PersistentVolumeClaim(PVC_MOUNT,PVC_NAME, true)
  volumeList = new ArrayList<PersistentVolumeClaim>()
  volumeList.add(pvc);

  def p = new PodTemplate()
  p.setName(NAME)
  p.setLabel(NAME)
  p.setContainers(containerList)
  p.setEnvVars(podenvarList)
  p.setVolumes(volumeList)

  cloud.templates[0].add(p)
  instance.clouds.replace(cloud)
  instance.save()
  println "[INFO] Successfully updated KubernetesCloud object " + cloud

}
