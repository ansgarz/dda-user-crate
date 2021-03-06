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
(ns dda.pallet.dda-user-crate.infra.schema
  (:require
   [schema.core :as s]
   [dda.config.commons.ssh-key :as ssh-key]))

(def GpgKey {:public-key s/Str
             (s/optional-key :passphrase) s/Str
             (s/optional-key :private-key) s/Str})

(def Gpg
  {(s/optional-key :gpg) {:trusted-key GpgKey}})

(def Ssh
 {(s/optional-key :authorized-keys) [ssh-key/PublicSshKey]
  (s/optional-key :personal-key) ssh-key/SshKeyPair})

(def Settings {(s/optional-key :settings)
               (hash-set (s/enum :sudo))})

(def User
  (s/either
    (merge {:hashed-password s/Str}
           Gpg Ssh Settings)
    (merge {:clear-password s/Str}
           Gpg Ssh Settings)))

(def UserCrateConfig
  {s/Keyword User})
