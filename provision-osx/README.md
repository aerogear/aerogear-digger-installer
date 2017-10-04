# Provision macOS Ansible role

Provision Jenkins node for the Digger. This role performs the following tasks:

* [Install Homebrew](#prerequisites)
* [Install RVM and Ruby](#install-homebrew)
* [Install NVM and Node](#install-rvm-and-ruby)
* [Install Xcode](#install-xcode)
* [Download certs](#download-certs)
* [Update Cocoapods](#update-cocoapods)
* [Configure Buildfarm node](#configure-buildfarm-node)
* [Confifure Proxy](#configure-proxy)

## Prerequisites
* SSH access as a user with `sudo` permissions. If a password is required, declare it in the  `ansible_become_pass` variable.

* `ansible_sudo_pass` variable set

* `xcode_install_user` and `xcode_install_password` variables set, otherwise Xcode tasks will be skipped.

## Required Network Access

The node will need access to the following external hosts:
* `https://github.com` - To download GitHub repositories and Cocoapods.
* `https://raw.githubusercontent.com` - To download and install NVM.
* `https://rvm.io` - To download and install RVM.
* `http://developer.apple.com` - To download developer certificates and Xcode.
* `https://npmjs.org` - To install NPM packages.

***Note: Other external hosts may be required depending on what other packages you specify to install.***

## Install Homebrew Package Manager
To run this section as a standalone step, use `osx_install_homebrew` tag in the playbook.

This will also add "taps" and packages specified in the options.


### Variables
* `homebrew_version` - The version of Homebrew to install (git tag).
* `homebrew_packages` - The packages to install using Homebrew. Format: `{ name: <PACKAGE_NAME> }`.
* `homebrew_repo` - The git repo where Homebrew resides (defaults to GitHub repo).
* `homebrew_prefix`
* `homebrew_install_path` - Where Homebrew will be installed, used as `homebrew_prefix/homebrew_install_path`.
* `homebrew_brew_bin_path` - `brew` will be installed in this directory.
* `homebrew_taps` - A list of taps to add.

## Install RVM, Ruby and Cocoapods
To run this section as a standalone step, use `osx_install_ruby` tag in the playbook.

Installs RVM, a single version of Ruby and `cocoapods` gem. Other gems can be installed by configuring the `gem_packages` variable.


### Variables
* `ruby_version` - The version of Ruby to install on the node.
* `cocoapods_version` - The version of the `cocoapods` gem to install.
* `gem_packages` - A list of gems to install on the node. Format: `{ name: <PACKAGE_NAME>, version: <PACKAGE_VERSION> }`.

## Install NVM and Node
To run this section as a standalone step, use `osx_install_nodejs` tag in the playbook.

Installs NVM along with versions of Node specified in variables.
Packages from NPM can be specified for installation too, but they will be installed globally.


### Variables
* `nvm_install_url` - The URL of NVM installation script (defaults to GitHub release).
* `nvm_install_file_name` - NVM installation script file name 
* `node_versions` - A list of Node versions to install.
* `npm_packages` - A list of global NPM packages to install. Format: `{ name: <PACKAGE_NAME>, version: <PACKAGE_VERSION> }`.

## Install Xcode and Xcode CLI tools
To run this section as a standalone step use `osx_install_xcode` tag in the playbook.

***Installing Xcode can take ~ 30 minutes without a cached copy. (per version - if installing multiple versions) ***

If you have 2FA enabled for your Apple Developer Account, then
you will need to set the `xcode_install_session_token` variable to point to a cookie created
by authenticating with Apple. This is done using `Fastlane Spaceship`.


***Note: fastlane version has to be 2.42.0 or higher***

```
gem install fastlane

fastlane spaceauth -u <USERNAME>
```

Follow the instructions in the terminal. A cookie will be generated and displayed after successful authentication. Copy the cookie from `---\n` to the final `\n` and provide this as
the value for `xcode_install_session_token`.

Alternatively you can disable the 2FA on the Apple Developer Account for the duration of the Ansible job.

### Variables
* `xcode_install_user` - Apple Developer Account username. If this is not set then Xcode will not be installed.
* `xcode_install_password` - Apple Developer Account password. If this is not set then Xcode will not be installed.
* `xcode_install_session_token` - Insert Apple auth cookie in this variable if you are using 2FA 
* `xcode_versions` - A list of Xcode versions to install.

## Download OSX certificates
To run this section as a standalone step use the `osx_download_certs` tag in the playbook.

Downloads required certificates into the node. Currently the only required certificate is Apple's WWDR certificate.
This certificate will be downloaded into the user's home directory.


### Variables
* `apple_wwdr_cert_url` - Apple WWDR certificate URL. Defaults to Apple's official URL
* `apple_wwdr_cert_file_name` - Output file name of the downloaded file. Defaults to `AppleWWDRCA.cer`.

## Update Cocoapods
To run this section as a standalone step, use the `osx_pod_repo_update` tag in the playbook.

Executes `pod repo update`.


## Configure Buildfarm node
To run this section as a standalone step, use the `osx_configure_buildfarm` tag in the playbook.

Creates a credential set in the build farm for the macOS nodes using the provided keys. Add each machine as a node in the build farm, connecting through SSH.

You will need to create a key pair using a tool such as `ssh-keygen` to allow the Jenkins instance to connect with the macOS nodes.


### Variables
* `credential_private_key` - Private key stored in Jenkins and used to SSH into the macOS node. If not set, then asymmetric key pair will be generated.
* `credential_public_key` - Public key from asymmetric key pair. If this is not set then a key pair will be generated.
* `credential_passphrase` - Password for the encryption of the private key. This is stored in Jenkins and used to SSH into the macOS node. If this is not set the private key will not be encrypted.
* `buildfarm_node_port` - The port to connect the macOS node to. Defaults to `22`.
* `buildfarm_node_root_dir` - Root node of the node in Jenkins. Defaults to `/Users/jenkins`.
* `buildfarm_credential_id` - Identifier for the Jenkins credential object. Defaults to `macOS_buildfarm_cred`.
* `buildfarm_credential_description` - Description of the Jenkins credential object.
* `buildfarm_node_name` - Name of the slave/node in Jenkins. Defaults to `macOS (<node_host_address>)`.
* `buildfarm_node_labels` - List of labels to give to the macOS node. Defaults to only `ios`.
* `buildfarm_user_id` - Jenkins user. Defaults to `admin`.
* `buildfarm_node_executors` - Number of executors (Jenkins configuration) on the macOS node. Defaults to `1`.
* `buildfarm_node_mode` - How the macOS node should be utilised. Should be one of:
  - `NORMAL` - Use this node as much as possible
  - `EXCLUSIVE` - Only build jobs with labels matching this node.
* `buildfarm_node_description` - Description of the macOS node in Jenkins.

* `remote_tmp_dir` - A directory where downloaded scripts and other miscellaneous files can be stored for the duration of the job.
* `project_name` - Name of the Jenkins project in OpenShift. Defaults to `digger`.


## Provisioning macOS Role Using Proxy
To run this section as a standalone step use the `osx_configure_proxy` tag in the playbook.

Configures a proxy to be used within the macOS node.

***CAUTION: Only proxies that do not require authentication are supported by the Ansible job.***



### Options

* `proxy_host` - proxy url/base hostname to be used.
* `proxy_port` - proxy port to be used.
* `proxy_device` - the proxy network device to use the proxy config from the [list of devices](#list-of-devices).
* `proxy_ctx` - a list of proxies to be set, defaults to "webproxy" and "securewebproxy", see the full list of [proxy types](#proxy-types)


#### List of Devices

To list all available devices/services:

```
networksetup -listallnetworkservices
```

The list should usually contain a device that handles external connections such as "Ethernet" or "Wi-Fi".

#### Proxy Types

* webproxy
* securewebproxy
* streamingproxy
* socksfirewallproxy