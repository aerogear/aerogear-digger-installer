
# Ansible Roles for installing Aerogear Digger


## Requirements

Ansible 2.2.0 or higher.


## Role Variables

TODO

## Example
```
- host: master
  roles:
  - {role: aerogear.digger-installer, tags: aerogear-digger}
```

## Adding changes to Ansible Galaxy

* Create an account on [Ansible Galaxy](https://galaxy.ansible.com/) using your github account

* Log in to ansible galaxy 

* Using username and password
	```
	ansible-galaxy login
	```
	Enter you username and password when prompted

* Using github token
	```
	ansible-galaxy login --github-token <github-token>
	```

* Import the changes to ansible-galaxy

	Make the necessary changes and merge them to master. Update the package on Ansible Galaxy by running:

	```
	ansible-galaxy import github_user github_repo
	```

## License

Apache 2.0
