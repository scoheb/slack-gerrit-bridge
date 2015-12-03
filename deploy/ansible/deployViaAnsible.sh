#!/bin/bash

ansible-playbook  playbook-install-jdk8.yml --connection=local
ansible-playbook  playbook-install-jdk8.yml --connection=local
ansible-playbook  playbook-deploy-sgb.yml --connection=local
ansible-playbook  playbook-launch-sgb.yml --connection=local

