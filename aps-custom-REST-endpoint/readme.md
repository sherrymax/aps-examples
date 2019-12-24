#### This project contains all the components required to create custom REST endpoints in APS

### Use-Case / Requirement
The workflows can have custom requirements to get certain data from the APS engine or its database. This can be a implemented as a custom REST API and deployed.

### Prerequisites to run this demo end-2-end

* Alfresco Process Services (powered by Activiti) (Version 1.9 and above) - If you don't have it already, you can download a 30 day trial from [Alfresco Process Services (APS)](https://www.alfresco.com/products/business-process-management/alfresco-activiti).Instructions & help available at [Activiti Docs](http://docs.alfresco.com/activiti/docs/), [Alfresco BPM Community](https://community.alfresco.com/community/bpm)

### Activiti Setup and Process Deployment
1. Setup Alfresco Activiti if you don't have one already. Instructions & help available at [Activiti Docs](http://docs.alfresco.com/activiti/docs/), [Alfresco BPM Community](https://community.alfresco.com/community/bpm)
2. Before starting with your REST API implementation make sure to ![set up a proper Activiti Extension project](https://hub.alfresco.com/t5/alfresco-process-services/activiti-enterprise-developer-series-setting-up-an-extension/ba-p/287187).
3. Sample custom API code to greet.
    ```java
    package com.activiti.extension.api;

    import com.activiti.extension.api.GreetingMessage;
    import com.codahale.metrics.annotation.Timed;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/enterprise")
    public class CustomRestEndpoint {

        @RequestMapping(value = "/CustomRestEndpoint", method= RequestMethod.GET)
        public GreetingMessage sayHello(@RequestParam(value="name", required=false,
                defaultValue="World") String name) {
            GreetingMessage msg = new GreetingMessage(name, "Hello " + name + "!");
            return msg;
        }

        @RequestMapping(value = "/CustomRestEndpoint/{name}", method= RequestMethod.GET)
        public GreetingMessage sayHelloAgain(@PathVariable String name) {
            GreetingMessage msg = new GreetingMessage(name, "Hello " + name + "!");
            return msg;
        }

        @Timed
        public static void main(String[] argv) {

            System.out.println("---------------------------------------------------");
            System.out.println("---------------------------------------------------");
            System.out.println("-------- Custom Endpoint Successfully called ------");
            System.out.println("---------------------------------------------------");
            System.out.println("---------------------------------------------------");

        }

    }
    ```
4. Another example of custom REST endpoint to get task count of a user.
    ```java
    package com.activiti.extension.rest;

    import com.activiti.domain.idm.User;
    import com.activiti.security.SecurityUtils;
    import org.activiti.engine.TaskService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestMethod;
    import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequestMapping("/rest/custom-rest-endpoint") //http://localhost:8080/activiti-app/app/rest/custom-rest-endpoint
    public class CustomRESTEndpoint {

        @Autowired
        private TaskService taskService;

        @RequestMapping(method = RequestMethod.GET, produces = "application/json")
        public CustomRestEndpointResponse sampleImplementation() {

        	//Obtain the Task Count for the Current User
            final User currentUser = SecurityUtils.getCurrentUserObject();
            final long taskCount = taskService.createTaskQuery().taskAssignee(String.valueOf(currentUser.getId())).count();

            //Construct the Response
            final CustomRestEndpointResponse myRestEndpointResponse = new CustomRestEndpointResponse();
            myRestEndpointResponse.setFullName(currentUser.getFullName());
            myRestEndpointResponse.setTaskCount(taskCount);
            return myRestEndpointResponse;
        }

        private static final class CustomRestEndpointResponse {

            private String fullName;
            private long taskCount;

    		public String getFullName() {
    			return fullName;
    		}

    		public long getTaskCount() {
    			return taskCount;
    		}

    		public void setFullName(final String fullName) {
    			this.fullName = fullName;
    		}

    		public void setTaskCount(final long taskCount) {
    			this.taskCount = taskCount;
    		}
        }
    }
    ```

5. Build the extension project
```
mvn clean install
```
6. Deploy the jar file to the activiti-app webapp's WEB-INF/lib folder.
   eg: /usr/local/tomcat/webapps/activiti-app/WEB-INF/lib.



### Run the DEMO

### References
1. https://hub.alfresco.com/t5/alfresco-process-services/activiti-enterprise-developer-series-custom-rest-endpoints/ba-p/287712
2. https://github.com/gcodycollins/Cody-Code/blob/master/Activiti%20Java/com/activiti/extension/api/CustomRestEndpoint.java
3. https://docs.alfresco.com/activiti/docs/user-guide/1.5.0/#app-rest-call-task
4. https://docs.alfresco.com/process-services1.9/topics/process_services_api.html
5. https://docs.alfresco.com/process-services1.9/topics/process_engine_rest_api.html
