
# Ansible Roles for installing AeroGear Digger

## Project Info

|                 | Project Info  |
| --------------- | ------------- |
| License:        | Apache License, Version 2.0  |
| Documentation:  | https://github.com/aerogear/digger-installer  |
| Issue tracker:  | https://issues.jboss.org/browse/AGDIGGER  |
| Mailing lists:  | [aerogear-users](http://aerogear-users.1116366.n5.nabble.com/) ([subscribe](https://lists.jboss.org/mailman/listinfo/aerogear-users))  |
|                 | [aerogear-dev](http://aerogear-dev.1069024.n5.nabble.com/) ([subscribe](https://lists.jboss.org/mailman/listinfo/aerogear-dev))  |
| IRC:            | [#aerogear](https://webchat.freenode.net/?channels=aerogear) channel in the [freenode](http://freenode.net/) network.  |

## Requirements

Ansible 2.2.0 or higher.


## Running Digger on existing OpenShift instance

1. Stand up an OpenShift cluster from origin master, installing the standard image streams to the OpenShift namespace:

        oc cluster up

### Notes on log-in setup for the playbook

1. If you are running the playbook locally (i.e. using ansible_connection=local) but pointing at a remote master node then login_url must be set when running the playbook.

2. If you are running the playbook remotely (i.e. while in an ssh session) then login_url does not need to be set as the default address for oc login is https://localhost:8443.

### Using insecure connections

If you wish to use an insecure connection you can pass the skip_tls environment variable which will bypass the certificate check when logging in to openshift.

To pass it with the ansible-playbook, it should look like:

```
-e "skip_tls=true"
```

### Using self signed certs

In the case of oc cluster up self signed certs are used. Pass the following variable when running the playbook (or set it in your inventory file) in 
this case.

```
 '-e jenkins_protocol=http'
```

### Execute the playbook:

#### Example command line to execute aerogear digger ansible install
```
ansible-playbook -i <your-inventories-file> sample-build-playbook.yml
```

The playbook executes the following steps for you:

- Creates a Project in OpenShift
- Checks that java is installed
- Installs Jenkins
- Configures Jenkins
- Installs AndroidSDK to a PV
- [Configures an OSX node](./provision-osx/README.md) (optional)
- Installs Nagios and triggers checks
- Prints out urls and credentials.


## Running Digger on OpenShift dedicated

### Prerequisites:

* Persistent volumes x 3 - Default sizes 40Gib, 10Gib, 1GiB
* OpenShift command line client installed locally
* Java installed locally
* An existing SSH Key Pair locally. Public key will need to be uploaded to the Jenkins server
* SSH access to a macOS server outside the OpenShift cluster if required
* Set all host groups variables except for `macos` to `ansible_connection=local`

A subset of the roles in this repository should be run using the `deploy` tag in `sample-build-playbook.yml`

Execute the following:

`ansible-playbook -i <your-inventories-file> sample-build-playbook.yml --tags=deploy,provision-osx`

If you do not have an external macOS node setup, run the following:

`ansible-playbook -i <your-inventories-file> sample-build-playbook.yml --tags=deploy`

## License

Apache 2.0
