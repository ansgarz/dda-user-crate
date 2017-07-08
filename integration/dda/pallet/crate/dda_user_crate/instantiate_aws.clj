; Licensed to the Apache Software Foundation (ASF) under one
; or more contributor license agreements. See the NOTICE file
; distributed with this work for additional information
; regarding copyright ownership. The ASF licenses this file
; to you under the Apache License, Version 2.0 (the
; "License"); you may not use this file except in compliance
; with the License. You may obtain a copy of the License at
;
; http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.
(ns dda.pallet.crate.dda-user-crate.instantiate-aws
  (:require
    [clojure.inspector :as inspector]
    [org.domaindrivenarchitecture.pallet.commons.session-tools :as session-tools]
    [org.domaindrivenarchitecture.pallet.commons.pallet-schema :as ps]
    [dda.cm.operation :as operation]
    [dda.cm.aws :as cloud-target]
    [dda.pallet.crate.dda-user-crate.user.os-user :as os-user]
    [dda.pallet.crate.dda-user-crate.group :as group]
    [dda.pallet.domain.dda-user-crate :as domain]))

(def shantanu-key
  {:type "ssh-rsa"
   :public-key "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDY96t89DVhJsCw1yulH1fi6YUiguAc2a6nKCXwvo+IxP/JZyq5j2zM+j84Sj9vdGcRnpeFDW/OhiNIA1gxmgvWnCbm3AI6uoLF08bWXCWaGpcQIANpuIWyh2oQhHD+3twaL8jPZXHZvBWNYxlXY+z1JSpSJ2r8JHebwe4mcypCWtXCkoBw4+/j4iU3ksPpFhJFRY1ij1bWEFnUSYhMWNCIvps4OPz9tLKRDjBd7rWYSSia04AuFjRgMHiZ79rY+brxQSVj4a0fnppomfe9QOsGzl0LlQMAea7ahOxFOtuenngyHA56U2kv5Fhu71ZBtEikIJpY6S6TNJEhiITfvEdB"
   :comment "kumar.shantanu@gmail.com"})

(def jem-key-host
 {:type "ssh-rsa"
  :public-key "AAAAB3NzaC1yc2EAAAADAQABAAABAQDd0NIMownb4CSsifH2OBoO3+Hv7I04EjblR5S1VdEOZ2a59nVjWJMIwVj+JkFoon7YaYhgRoqzmDuR7nX8yfHXTljJ2VRwecvbcPV3exaNTcWSMUZMwBKIAEKdTwaZ5wHogJRYeGtPTBYf6k433sGS3TH2zy6YOCwftGKFKc4LkhB7ZnjHTQ4AWefmazt6FV8xi4ohZv/sgy3Tnm9ylxI7vHdVwvwZM4MzOoCIQTHNJWvOMgxuFmSj9vZlwj/IpwmHimxEjBszMf1gzoA7lb/3MShfCB8u3WFpTUiHOlNu1xsbrzC3f0sK9PO1qpQ2QunModw7r3Avx7lE5mK0xPW/"
  :comment "mje@host"})

(def jem-key-vm
 {:type "ssh-rsa"
  :public-key "AAAAB3NzaC1yc2EAAAADAQABAAABAQCeO+eiYDonq3OfxyaUx259y/1OqbhLciD4UlCkguD5PgOuXw+kCXS1Wbdor9cvU8HnsL2j70sPSwCWkcDrrGQ0kpC0GuNO47pKawAOSv07ELpSIIp/nPK5AX2+qI1H3MADBWBE5N1L7sdgatON2A/cC3u5pzcWDaEH7/IJdOkRm8H+qqG+uva6ceFUoYFiJKDixmsmaUXhhDcfYhfpAPBUCSes+HTeT/hk6pdLTX9xXd4H5wyAc+j1e6kPq9ZcxvzZNr9qEMIFjnNL/S9w1ozxQa3sKJQHj8SyVZDlwjvepGS7fKrdlRps938A7I3Y4BaXGX//M1y2HNbUWbMOllLL"
  :comment "mje@jergerProject"})

(def ssh-pub-key
  (os-user/read-ssh-pub-key-to-config))

(def ssh-priv-key "$YOUR_PRIVATE_KEY")

(def ssh-key-pair
  {:public-key ssh-pub-key
   :private-key ssh-priv-key})

(def domain-config
  {:jem {:encrypted-password "kpwejjj0r04u09rg90rfj"
         :authorized-keys [jem-key-host jem-key-vm]}
   :shantanu {:encrypted-password "kpwejjj0r04u09rg90rfj"
              :authorized-keys [shantanu-key]}
   :test {:encrypted-password  "USER_PASSWORD"
          :authorized-keys [ssh-pub-key]
          :personal-key ssh-key-pair}})

(defn integrated-group-spec [count]
  (merge
    (group/dda-user-group (domain/crate-stack-configuration domain-config))
    (cloud-target/node-spec "jem")
    {:count count}))

(defn converge-install
  ([count]
   (operation/do-converge-install (cloud-target/provider) (integrated-group-spec count)))
  ([key-id key-passphrase count]
   (operation/do-converge-install (cloud-target/provider key-id key-passphrase) (integrated-group-spec count))))

(defn server-test
  ([count]
   (operation/do-server-test (cloud-target/provider) (integrated-group-spec count)))
  ([key-id key-passphrase count]
   (operation/do-server-test (cloud-target/provider key-id key-passphrase) (integrated-group-spec count))))
