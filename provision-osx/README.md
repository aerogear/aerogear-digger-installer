# Provision OSX role

Provision Jenkins node for the build farm. This role performs a few tasks, these are:

* [Install Homebrew](#prerequisites)
* [Install RVM and Ruby](#install-homebrew)
* [Install NVM and Node](#install-rvm-and-ruby)
* [Install Xcode](#install-xcode)
* [Download certs](#download-certs)

## Prerequisites
* SSH access as a user with sudo permissions.
* `ansible_sudo_pass` variable set when running the job.

## Network access
The job supports the use of a proxy.

The node will need access to the following external hosts:
* `https://github.com`
* `https://raw.githubusercontent.com`
* `https://rvm.io`

## Install Homebrew
Installs Homebrew on the node.

This will also add any taps and packages specified in the options.

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
* `xcode_install_user` - Apple Developer Account username.
* `xcode_install_password` - Apple Developer Account password.
* `xcode_install_session_token` - Apple Developer Account auth cookie from `fastlane spaceauth` command (For accounts with 2FA enabled).
* `xcode_versions` - A list of Xcode versions to install. These may take over 30 minutes each to install.

## Download certs
Downloads some required certificates into the node. Right now, the only required certificate is Apple's WWDR certificate.
This certificate will be downloaded into the user's home directory.
 
### Options:
* `apple_wwdr_cert_url` - Apple WWDR certificate URL. Defaults to Apple's official URL
* `apple_wwdr_cert_file_name` - Output file name of the downloaded file. Defaults to `AppleWWDRCA.cer`.

## Other options
* `remote_tmp_dir` - A directory where downloaded scripts and other miscellaneous files can be stored for the duration of the job.