/**
 *  Unifi Access Lock driver for Hubitat
 *
 *  Copyright © 2023 Garth Braithwaite
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 * Hubitat is the Trademark and Intellectual Property of Hubitat, Inc.
 * Unifi is the Trademark and Intellectual Property of Ubiquiti Networks, Inc.
 *
 * -------------------------------------------------------------------------------------------------------------------
 *
 *  Changes:
 *  1.0.0 - Initial release
 *
 */

metadata {
  definition(
    name: "Unifi Access Lock",
    namespace: "unifi",
    author: "Garth Braithwaite",
    importUrl: "https://raw.githubusercontent.com/garthdb/hubitat-unifi-access-lock/main/drivers/Unifi-Access-Lock.groovy"
  ) {
    capability "Lock"
    capability "Polling"
    capability "Refresh"
  }

  preferences {
    input name: "host",
      type: "string",
      title: "The API host address of the Unifi Access Controller (e.g. https://console-ip:12445)",
      required: true,
      defaultValue: ""
    input name: "token",
      type: "string",
      title: "The API token for the Unifi Access Controller",
      required: true,
      defaultValue: ""
    input name: "doorId",
      type: "string",
      title: "The ID of the door to control",
      required: true,
      defaultValue: ""
  }
}


def updated() {
  refresh()
}


def lock() {
  log.debug "Unifi API does not support locking"
}


def unlock() {
  def params = [
    uri: "${host}/api/v1/developer/doors/${doorId}/unlock",
    ignoreSSLIssues: true,
    headers: [
      "Authorization": "Bearer ${token}",
      "accept": "application/json",
      "content-type": "application/json"
    ]
  ]
  try {
    httpPut(params) {
      resp ->
        options = resp.data
      if (options.code == "SUCCESS") {
        sendEvent(name: lock, value: "unlocked", descriptionText: "Door ${state.name} is unlocked", isStateChange: true)
      } else {
        log.error "Error retrieving door info: ${options.msg}"
      }
    }
  } catch (e) {
    log.error "Error updating door options: $e"
  }
}


def poll() {
  refresh()
}


def refresh() {
  def params = [
    uri: "${host}/api/v1/developer/doors/${doorId}",
    ignoreSSLIssues: true,
    headers: [
      "Authorization": "Bearer ${token}",
      "accept": "application/json",
      "content-type": "application/json"
    ]
  ]
  try {
    httpGet(params) {
      resp ->
        options = resp.data
      if (options.code != "SUCCESS") {
        log.error "Error retrieving door info: ${options.msg}"
      } else {
        state.doorPositionStatus = options.data.door_position_status
        state.floorId = options.data.floor_id
        state.fullName = options.data.full_name
        state.name = options.data.name
        state.type = options.data.type
        if (getState().lock != toLockStatus(options.data.door_lock_relay_status)) {
          state.lock = toLockStatus(options.data.door_lock_relay_status)
          sendEvent(name: lock, value: state.lock, descriptionText: "Door ${state.name} is ${state.lock}", isStateChange: true)
        } else {
          state.lock = toLockStatus(options.data.door_lock_relay_status)
          sendEvent(name: lock, value: state.lock, descriptionText: "Door ${state.name} is ${state.lock}", isStateChange: false)
        }
      }
    }
  } catch (e) {
    log.error "Error retrieving door info: $e"
  }
}


private static toLockStatus(door_lock_relay_status) {
  switch (door_lock_relay_status) {
    case "lock":
      return "locked"
    case "unlock":
      return "unlocked"
    default:
      return "unknown"
  }
}