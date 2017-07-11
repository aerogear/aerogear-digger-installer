
# Ansible Roles for installing AeroGear Digger


## Requirements

Ansible 2.2.0 or higher.


## Running Digger on existing OpenShift instance

### Prerequisites:

#### Persistent volume (by default 10Gi, but it can be changed for different needs)

1. Unless you have built OpenShift locally, be sure to grab the [oc command, v1.3+](https://github.com/openshift/origin/releases/tag/v1.3.1)

1. Stand up an OpenShift cluster from origin master, installing the standard image streams to the OpenShift namespace:

        oc cluster up

1. Setup simple persistent volume on new cluster execute:

**Note**: jenkins-persistent-template.json and android-sdk-persistent-template.json
template files require OpenShift persistent volumes.
Persistent volume setup is not part of the templates and requires separate steps.
If you already have persistent volumes feel free to skip this step.
Other wise take the following steps twice to create both pvs, replacing `<folder>` with `jenkins` and `android-sdk-linux`
```
        rm -R /tmp/digger/<folder>
        mkdir -p /tmp/digger/<folder>
        chmod -R 777 /tmp/digger/<folder>
        # creating a cluster wide persistent volume like the one we use requires
        # an admin user on OpenShift.
        oc login -u system:admin
        oc create -f pv-sample-templates/<folder>/sample-pv.json
```
Note that `mkdir` and `chmod` commands above should be executed in the Docker-machine, in case of using Docker-machine (boot2docker) on Mac.

You can find templates for creating PV's backed by glusterFS or NFS in pv-sample-templates/android-sdk-linux and pv-sample-templates/jenkins

### Execute the playbook:

#### Example command line to execute aerogear-digger ansible install
```
ansible-playbook -i <your-inventories-file> sample-build-playbook.yml 
```

The playbook executes the following steps for you:

- Creates a Project in OpenShift
- Installs Jenkins
- Configures Jenkins
- Installs AndroidSDK to a PV
- Configures an OSX node


## License

Apache 2.0
