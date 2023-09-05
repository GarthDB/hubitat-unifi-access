# Hubitat Unifi Access

Unifi recently released a [release candidate](https://community.ui.com/releases/UniFi-Access-Application-1-9-3/c8e99eba-397a-4431-8d7b-497da987b4b7) of Unifi Access which includes an Open API. This repo contains a Hubitat driver that is an initial attempt to control the lock via this local API.

## Installation

Use [Hubitat Package Manager](https://hubitatpackagemanager.hubitatcommunity.com/installPkg.html) by looking for Unifi Access.

### Manual installation

* Import raw driver code from [Unifi-Access-Lock.groovy](https://raw.githubusercontent.com/GarthDB/hubitat-unifi-access/main/drivers/Unifi-Access-Lock.groovy) file
* Create a new virtual device using the driver
* Update the preferences for the device:
  * "The API host address of the Unifi Access Controller (e.g. https://console-ip:12445)" is usually the API of the controller. See the Unifi Access Open API documentation for details on "Obtain Your Hostname". It should contain `https://` and the port (e.g., `:12445`).
  * "The API token for the Unifi Access Controller" is something that can be generated in the Unifi Access App. It needs the correct permissions to be able to unlock and view the lock status. See the Unifi Access Open API documentation for details on "Create API Token & Download API Documentation"
  * "The ID of the door to control" can be obtained using the Open API to get a list of available doors and their corresponding IDs. See the Unifi Access Open API documentation for details on "Fetch Devices"

## Capabilities

* Unlock door
* See door lock status

### Polling

Since the local Unifi Access API requires polling to check the status of the lock, it's recommended to use [Rule Machine](https://docs2.hubitat.com/apps/rule-machine) to refresh or poll the lock status every 2-3 seconds.

## Future feature ideas:

Just some functionality that should be possible. Pull requests welcome.

* Use the API to list doors and select the door ID from a dropdown.
* Add support for managing codes(?)
* Automate polling. Currently, something external to the driver is required to poll the API to check the status.
* Create a driver to check and handle doorbell (`access.remotecall.request`) events. Using Rule Machine, this could allow for setting up a 3rd party door chime.
