#### Example of integration between APS and IVR (Twilio)

### Use-Case / Requirement
Digitization of Customer Address Change by integrating IVR (Twilio) System with APS and should have the following capabilities.
1. Customer calls to Customer Care and makes a request for Change of Address
2. IVR System prompts for Member ID and invokes Alfresco Process Services to generate an APS instance.

### Design
Following design is used to accomplish the requirements.
![Design](images/design.png)

### Prerequisites to run this demo end-2-end
* Alfresco Process Services (powered by Activiti) (Version 1.9 and above) - If you don't have it already, you can download a 30 day trial from [Alfresco Process Services (APS)](https://www.alfresco.com/products/business-process-management/alfresco-activiti).Instructions & help available at [Activiti Docs](http://docs.alfresco.com/activiti/docs/), [Alfresco BPM Community](https://community.alfresco.com/community/bpm)
* Active [Twilio Account](https://www.twilio.com/console/studio/dashboard).

## Configuration Steps

### Activiti : Setup and Process Deployment
1. Import the [aps-ivr-app.zip](aps-ivr-app.zip) app available in this project into Activiti.
2. Process Flow. ![Process-Flow-1](images/Process-Flow-1.png)
3. REST Configuration.   ![REST-Configuration-1](images/REST-Configuration-1.png)
4. Publish/Deploy the APS App.

### Twilio : APS-IVR-Integration Flow
1. Create a APS-IVR-Integration flow in the Twilio Studio. ![Twilio-1.png](images/Twilio-1.png)
2. Modeler view of flow. ![Twilio-2.png](images/Twilio-2.png)
3. The json representation of flow.

```json
{
  "description": "APS-IVR-Integration-Flow",
  "states": [
    {
      "name": "Trigger",
      "type": "InitialState",
      "properties": {
        "offset": {
          "x": -2370,
          "y": -1210
        },
        "flow_url": "https://webhooks.twilio.com/v1/Accounts/AC2850e86ab55946b2cf439e7140e08616/Flows/FWf7381be82c7b36d3fb06c2333fb98507"
      },
      "transitions": [
        {
          "event": "incomingMessage",
          "conditions": [],
          "next": null,
          "uuid": "7d9414ea-24d0-4bd1-8ef0-340d8d967778"
        },
        {
          "event": "incomingCall",
          "conditions": [],
          "next": "FF3ced66af639653d2c19f8bc05224d869",
          "uuid": "1f6d9434-15e0-4b6a-81fb-39667acfaa55"
        },
        {
          "event": "incomingRequest",
          "conditions": [],
          "next": null,
          "uuid": "ac510418-4c8c-471c-a189-834ab3b57b97"
        }
      ],
      "sid": "FF6905ac78cc83836d2fc3d16b5930cf13"
    },
    {
      "name": "CollectMemberId",
      "type": "Gather",
      "properties": {
        "offset": {
          "x": -2150,
          "y": -940
        },
        "timeout": 10,
        "finish_on_key": "#",
        "stop_gather": true,
        "number_of_digits": null,
        "save_response_as": null,
        "say": "Welcome to Alfresco Life Insurance. Please enter your member ID followed by the pin number.",
        "play": null,
        "voice": "Polly.Salli",
        "language": "en-US",
        "loop": 1,
        "hints": null,
        "gather_language": "en",
        "speech_timeout": null,
        "speech_model": null,
        "profanity_filter": null
      },
      "transitions": [
        {
          "event": "keypress",
          "conditions": [],
          "next": "FFe89a0d064315b9336c36b14f33672143",
          "uuid": "fdc738a4-2a32-4119-bb43-45d137a8399f"
        },
        {
          "event": "speech",
          "conditions": [],
          "next": null,
          "uuid": "63db6645-d3f1-4a7f-a1c7-aa9b1db413ab"
        },
        {
          "event": "timeout",
          "conditions": [],
          "next": null,
          "uuid": "086f63d7-cc5b-4cc2-a2b0-98266178c43d"
        }
      ],
      "sid": "FF3ced66af639653d2c19f8bc05224d869"
    },
    {
      "name": "KeyPressed",
      "type": "Branch",
      "properties": {
        "offset": {
          "x": -1780,
          "y": -290
        },
        "input": "{{widgets.CollectUserKeyPress.Digits}}"
      },
      "transitions": [
        {
          "event": "noMatch",
          "conditions": [],
          "next": null,
          "uuid": "a122ba5e-7d87-48cf-aa43-bc7898a28044"
        },
        {
          "event": "match",
          "conditions": [
            {
              "friendly_name": "Address Change",
              "type": "equal_to",
              "arguments": [
                "{{widgets.CollectUserKeyPress.Digits}}"
              ],
              "value": "2"
            }
          ],
          "next": "FF8ae5aa0e97559514ac33bc5a64bcd8df",
          "uuid": "ff209802-c800-4e35-96c3-e8e452ea37bb"
        }
      ],
      "sid": "FF42dfc80b09c296958da989c34fa9620b"
    },
    {
      "name": "InvokeAPS",
      "type": "Webhook",
      "properties": {
        "offset": {
          "x": -1510,
          "y": 0
        },
        "method": "POST",
        "url": "http://demo:demo@ec2-11-22-33-444.compute-1.amazonaws.com/activiti-app/api/enterprise/process-instances",
        "body": "{\n  \"processDefinitionId\": \"Process_sid-D810C013-4641-47E1-9009-B1D0E44AC3CB:2:38167\",\n\"variables\": [\n    {\n      \"name\": \"memberID\",\n      \"scope\": \"string\",\n      \"type\": \"string\",\n      \"value\": {{widgets.CollectMemberId.Digits}} \n    }\n  ]\n}",
        "timeout": null,
        "parameters": null,
        "save_response_as": null,
        "content_type": "application/json;charset=utf-8"
      },
      "transitions": [
        {
          "event": "success",
          "conditions": [],
          "next": "FFccb3aca1da8966090d35840579e4754f",
          "uuid": "7b11380f-3e84-4316-af0e-2764b1d70be7"
        },
        {
          "event": "failed",
          "conditions": [],
          "next": "FF02a75dc2d432475606b4bac8d0941bd6",
          "uuid": "51f80238-afb5-4bcd-856a-5d48c85d0726"
        }
      ],
      "sid": "FF8ae5aa0e97559514ac33bc5a64bcd8df"
    },
    {
      "name": "Exception",
      "type": "SayPlay",
      "properties": {
        "offset": {
          "x": -1090,
          "y": 320
        },
        "say": "Sorry...HTTP Invocation failed.",
        "play": null,
        "voice": null,
        "language": "en-US",
        "loop": 1,
        "digits": null
      },
      "transitions": [
        {
          "event": "audioComplete",
          "conditions": [],
          "next": "FF49e36487f70378c45b388307e277d714",
          "uuid": "b9ef67c0-3d88-4c8d-a419-fafef46a898b"
        }
      ],
      "sid": "FF02a75dc2d432475606b4bac8d0941bd6"
    },
    {
      "name": "Emailtriggered",
      "type": "SayPlay",
      "properties": {
        "offset": {
          "x": -1820,
          "y": 310
        },
        "say": "An email with change of address form, will be send to your email address in our records.",
        "play": null,
        "voice": "Polly.Salli",
        "language": "en-US",
        "loop": 1,
        "digits": null
      },
      "transitions": [
        {
          "event": "audioComplete",
          "conditions": [],
          "next": "FF700648ad40fc3af2975ea384450a73f3",
          "uuid": "eec08ec2-cea1-470e-93c8-bf4b932885eb"
        }
      ],
      "sid": "FFccb3aca1da8966090d35840579e4754f"
    },
    {
      "name": "CollectUserKeyPress",
      "type": "Gather",
      "properties": {
        "offset": {
          "x": -2110,
          "y": -620
        },
        "timeout": 3,
        "finish_on_key": "#",
        "stop_gather": true,
        "number_of_digits": null,
        "save_response_as": null,
        "say": "Thank you for that information. Press 1 to request insurance policy documents.\nPress 2 to request address change form.\nPress 0 to speak to a customer representative",
        "play": null,
        "voice": "Polly.Salli",
        "language": "en-US",
        "loop": 1,
        "hints": null,
        "gather_language": "en",
        "speech_timeout": null,
        "speech_model": null,
        "profanity_filter": null
      },
      "transitions": [
        {
          "event": "keypress",
          "conditions": [],
          "next": "FF42dfc80b09c296958da989c34fa9620b",
          "uuid": "05996a7a-ca21-4961-8bd4-941ceb3f779e"
        },
        {
          "event": "speech",
          "conditions": [],
          "next": null,
          "uuid": "065c364b-57cc-4318-88fd-ae82e4376aa4"
        },
        {
          "event": "timeout",
          "conditions": [],
          "next": null,
          "uuid": "14543d30-572d-4361-9428-77b5eb0eaa49"
        }
      ],
      "sid": "FFe89a0d064315b9336c36b14f33672143"
    },
    {
      "name": "CollectEndKeyPress",
      "type": "Gather",
      "properties": {
        "offset": {
          "x": -1780,
          "y": 670
        },
        "timeout": 5,
        "finish_on_key": "#",
        "stop_gather": true,
        "number_of_digits": null,
        "save_response_as": null,
        "say": "Is there anything else, that I can help you? \nPress 1 to go to the main menu.\nPress 2 to end the call.",
        "play": null,
        "voice": "Polly.Salli",
        "language": "en-US",
        "loop": 1,
        "hints": null,
        "gather_language": "en",
        "speech_timeout": null,
        "speech_model": null,
        "profanity_filter": null
      },
      "transitions": [
        {
          "event": "keypress",
          "conditions": [],
          "next": "FF3dba86980d86047da866965cd4ee7975",
          "uuid": "e5c30c40-eb4c-4907-a8f1-5d97956fc8cb"
        },
        {
          "event": "speech",
          "conditions": [],
          "next": null,
          "uuid": "6db58313-a6e5-4df8-9412-1e53c77ee0d7"
        },
        {
          "event": "timeout",
          "conditions": [],
          "next": null,
          "uuid": "6f4c63fb-6f46-42aa-a921-d727e7e3e8e1"
        }
      ],
      "sid": "FF700648ad40fc3af2975ea384450a73f3"
    },
    {
      "name": "EndKeyPressed",
      "type": "Branch",
      "properties": {
        "offset": {
          "x": -2460,
          "y": 960
        },
        "input": "{{widgets.CollectEndKeyPress.Digits}}"
      },
      "transitions": [
        {
          "event": "noMatch",
          "conditions": [],
          "next": "FF49e36487f70378c45b388307e277d714",
          "uuid": "3c34f7ca-5a3f-421d-bb72-530a3ee12bad"
        },
        {
          "event": "match",
          "conditions": [
            {
              "friendly_name": "End call",
              "type": "equal_to",
              "arguments": [
                "{{widgets.CollectEndKeyPress.Digits}}"
              ],
              "value": "2"
            }
          ],
          "next": "FF49e36487f70378c45b388307e277d714",
          "uuid": "8fa96582-183b-4684-8214-364da75029d5"
        },
        {
          "event": "match",
          "conditions": [
            {
              "friendly_name": "If value equal_to 1",
              "type": "equal_to",
              "arguments": [
                "{{widgets.CollectEndKeyPress.Digits}}"
              ],
              "value": "1"
            }
          ],
          "next": "FFe89a0d064315b9336c36b14f33672143",
          "uuid": "0edd7939-2c9c-4834-ba53-84900a8f34fd"
        }
      ],
      "sid": "FF3dba86980d86047da866965cd4ee7975"
    },
    {
      "name": "Thankyou",
      "type": "SayPlay",
      "properties": {
        "offset": {
          "x": -1050,
          "y": 1340
        },
        "say": "Thank you for calling Alfresco Life Insurance. \nHave a nice day !",
        "play": null,
        "voice": null,
        "language": null,
        "loop": 1,
        "digits": null
      },
      "transitions": [
        {
          "event": "audioComplete",
          "conditions": [],
          "next": null,
          "uuid": "952f292f-1a32-4932-943b-49c8377aba4d"
        }
      ],
      "sid": "FF49e36487f70378c45b388307e277d714"
    }
  ]
}
```

### Publish the Twilio Flow and run the DEMO

### References
1. https://github.com/cijujoseph/activiti-examples/tree/master/activiti-alexa-demo
2. https://www.twilio.com/docs/studio/tutorials/how-to-build-an-ivr
