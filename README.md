# JustInTime
Solution that uses MQTT for connecting your washing machine when the light price is below a limit.

## Global architecture
This system is based in three main modules:
* **Two Node-Red flows:**
  * **Light prices parser and publisher:** every hour, this flow requests the current light price to the *Red Electrica Española* API
  and publishes it to the topic *currentLightPrice*.
  * **Washing machine simulator:** This flow contains a node subscribed to the *turnWashingMachineOn* topic. When a message is received, 
    the flow publishes a "ON" message to the *washingMachineStatus* topic. In parallel, it waits for a certain amount of time 
    (simulating a washing cycle) and publishes an "OFF" message to the *washingMachineStatus* topic.
* **Android App**: this simple application lets the user choose the maximum amount
  desired to pay for the light (in €/MWh). By using the Paho MQTT Client library, it subscribes to the *currentLightPrice* topic.
  When a message is received, if the price is below the user's maximum and the user has enabled the option "Automatically turn 
  washing machine on", the application will publish a message to the *turnWashingMachineOn* topic. The application is also subscribed
  to the *washingMachineStatus* topic, so that washing machine is not turned on if it is already working.
* **Mosquitto MQTT broker:** During the development of this system, the Mosquitto MQTT broker was deployed in a local machine

## QoS and retain flags

* **currentLightPrice MQTT Publisher**: QoS = 1, Retain = true 
* **currentLightPrice MQTT Subscriber**: QoS = 1
* **turnWashingMachineOn MQTT Publisher**: QoS = 2 
* **turnWashingMachineOn MQTT Suscriber**: QoS = 2 
* **washingMachineStatus MQTT Publishe**r: QoS = 2, Retain = true
* **washingMachineStatus MQTT Suscriber**: QoS = 2 
