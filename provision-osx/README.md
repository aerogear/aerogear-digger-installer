# Provision OSX role

Provision Jenkins node for the build farm. This role performs a few tasks, these are:

* [Install Homebrew](#prerequisites)
* [Install RVM and Ruby](#install-homebrew)
* [Install NVM and Node](#install-rvm-and-ruby)
* [Install Xcode](#install-xcode)
* [Download certs](#download-certs)
* [Update Cocoapods](#update-cocoapods)
* [Configure Buildfarm node](#configure-buildfarm-node)
* [Confifure Proxy](#configure-proxy)

## Prerequisites
* SSH access as a user with sudo permissions. A password can be provided if required with the `ansible_become_pass` variable.
* `ansible_sudo_pass` variable set when running the job.
* `xcode_install_user` variable set when running the job, otherwise Xcode tasks will be skipped.
* `xcode_install_password` variable set when running the job, otherwise Xcode tasks will be skipped.

## Network access
The job supports the use of a proxy.

The node will need access to the following external hosts:
* `https://github.com` - To download and install github and Cocoapods.
* `https://raw.githubusercontent.com` - To download and install NVM.
* `https://rvm.io` - To download and install RVM.
* `http://developer.apple.com` - To download developer certificates and Xcode.
* `https://npmjs.org` - To install NPM packages.

***Note: Other external hosts may be required depending on what other packages you specify to install.***

## Install Homebrew
Installs Homebrew on the node.

This will also add any taps and packages specified in the options.

To run this section as a standalone step you must specify the `osx_install_homebrew` tag.

### Options
* `homebrew_version` - The version of Homebrew to install (git tag).
* `homebrew_packages` - The packages to install using Homebrew. Format: `{ name: <PACKAGE_NAME> }`.
* `homebrew_repo` - The git repo where Homebrew resides (defaults to GitHub repo).
* `homebrew_prefix` -
* `homebrew_install_path` - Where Homebrew will be installed, used as `homebrew_prefix/homebrew_install_path`.
* `homebrew_brew_bin_path` - Where `brew` will be installed.
* `homebrew_taps` - A list of taps to add.

## Install RVM and Ruby
Installs RVM along with a single version of Ruby. The version of Ruby
to install can be defined by a variable. The cocoapods gem will be installed,
other gems can be specified using the `gem_packages` variable.

To run this section as a standalone step you must specify the `osx_install_ruby` tag.

### Options
* `rvm_install_url` - The URL of RVM installation script (defaults to GitHub release).
* `rvm_install_file_name` - What to name the file on the node.
* `rvm_gpg_url` - The URL of the RVM prerequisite gpg key.
* `rvm_gpg_file_name` - What to name the file on the node.
* `ruby_version` - The version of Ruby to install on the node.
* `cocoapods_version` - The version of the Cocoapods gem to install.
* `gem_packages` - A list of gems to install on the node. Format: `{ name: <PACKAGE_NAME>, version: <PACKAGE_VERSION> }`.

## Install NVM and Node
Installs NVM along with any versions of Node specified through variables.
Packages from NPM can be defined also, however they will be installed globally.

To run this section as a standalone step you must specify the `osx_install_nodejs` tag.

### Options
* `nvm_install_url` - The URL of NVM installation script (defaults to GitHub release).
* `nvm_install_file_name` - What to name the file on the node.
* `node_versions` - A list of Node versions to install.
* `npm_packages` - A list of global NPM packages to install. Format: `{ name: <PACKAGE_NAME>, version: <PACKAGE_VERSION> }`.

## Install Xcode
Installs Xcode CLI tools and Xcode versions specified.
Installing Xcode can take some time (~ 30 minutes without a cached copy) so please
be patient during these stages.

If you have 2FA enabled for the Apple Developer Account you're specifying then
you will need to set the `xcode_install_session_token` option to a cookie provided
by authenticating with Apple. This can be done using `Fastlane Spaceship`.

To run this section as a standalone step you must specify the `osx_install_xcode` tag.

***Note: Installed fastlane version must be >= 2.42.0***

```
gem install fastlane

fastlane spaceauth -u <USERNAME>
```

Follow the prompts. Once authenticated successfully a cookie will be logged to
the screen. Copy the cookie from `---\n` to the final `\n` and provide this as
the value for `xcode_install_session_token`.

Alternatively disable 2FA on the Apple Developer Account for the duration of the
Ansible job.

### Options
* `xcode_install_user` - Apple Developer Account username. If this is not set then Xcode will not be installed.
* `xcode_install_password` - Apple Developer Account password. If this is not set then Xcode will not be installed.
* `xcode_install_session_token` - Apple Developer Account auth cookie from `fastlane spaceauth` command (For accounts with 2FA enabled).
* `xcode_versions` - A list of Xcode versions to install. These may take over 30 minutes each to install.

## Download certs
Downloads some required certificates into the node. Right now, the only required certificate is Apple's WWDR certificate.
This certificate will be downloaded into the user's home directory.

To run this section as a standalone step you must specify the `osx_download_certs` tag.

### Options:
* `apple_wwdr_cert_url` - Apple WWDR certificate URL. Defaults to Apple's official URL
* `apple_wwdr_cert_file_name` - Output file name of the downloaded file. Defaults to `AppleWWDRCA.cer`.

## Update Cocoapods
Executes `pod repo update`.

To run this section as a standalone step you must specify the `osx_pod_repo_update` tag.

## Configure Buildfarm node
Creates a credential set in the build farm for the macOS nodes using the provided keys. Add each machine as a node in the build farm, connecting through SSH.

You will need to create a key pair using a tool such as `ssh-keygen` to allow the Jenkins instance to connect with the macOS nodes.

To run this section as a standalone step you must specify the `osx_configure_buildfarm` tag.

### Options
* `credential_private_key` - Private key stored in Jenkins and used to SSH into the macOS node. If this is not set then a key pair will be generated.
* `credential_public_key` - Public key of the pair. If this is not set then a key pair will be generated.
* `credential_passphrase` - Passphrase of the private key. This is stored in Jenkins and used to SSH into the macOS node. If this is not set the private key will not be password protected.
* `buildfarm_node_port` - The port to connect to the macOS node on. Defaults to `22`.
* `buildfarm_node_root_dir` - Root node of the node in Jenkins. Defaults to `/Users/jenkins`.
* `buildfarm_credential_id` - Identifier for the Jenkins credential object. Defaults to `macOS_buildfarm_cred`.
* `buildfarm_credential_description` - Description of the Jenkins credential object.
* `buildfarm_node_name` - Name of the slave/node in Jenkins. Defaults to `macOS (<node_host_address>)`.
* `buildfarm_node_labels` - List of labels to give the macOS node. Defaults to only `ios`.
* `buildfarm_user_id` - Jenkins user. Defaults to `admin`.
* `buildfarm_node_executors` - Number of executors (Jenkins configuration) on the macOS node. Defaults to `1`.
* `buildfarm_node_mode` - How the macOS node should be utilised. Should be one of:
  - `NORMAL` - Use this node as much as possible
  - `EXCLUSIVE` - Only build jobs with labels matching this node.
* `buildfarm_node_description` - Description of the macOS node in Jenkins.

## Other options
* `remote_tmp_dir` - A directory where downloaded scripts and other miscellaneous files can be stored for the duration of the job.
* `project_name` - Name of the Jenkins project in OpenShift. Defaults to `digger`.


## Configure Proxy

Configures a proxy to be used whithin the osx node.

To run this section as a standalone step you must specify the `osx_configure_proxy` tag.

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

#### Known Issues

Authenticated proxies are not supported (yet), you will need to manually apply the proxy config by running the command yourself if using one:


```
networksetup -setwebproxy Ethernet $PROXY_HOST $PROXY_PORT on $PROXY_USER $PROXY_PASS
```
