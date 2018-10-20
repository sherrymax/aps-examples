#### The project contains all the components required to create an ACS folder from APS via REST calls

### Use-Case / Requirement
Build a process to create a listener that listens to an event.


### Prerequisites to run this demo end-2-end

* Alfresco Process Services (powered by Activiti) (Version 1.9 and above) - If you don't have it already, you can download a 30 day trial from [Alfresco Process Services (APS)](https://www.alfresco.com/products/business-process-management/alfresco-activiti).Instructions & help available at [Activiti Docs](http://docs.alfresco.com/activiti/docs/), [Alfresco BPM Community](https://community.alfresco.com/community/bpm)


## Configuration Steps

### Activiti Setup and Process Deployment
1. Import the ![Event-Listener.zip](Event-Listener.zip) app available in this project into Activiti.
2. Process Flow.  ![Process-Flow](Process-Flow.png)
3. Process Configuration. ![Request-Mapping](Request-Mapping.png)
4. Task Configuration   ![Human-Task-Configuration](Human-Task-Configuration.png)
5. Event Listener Task Configuration ![Request-Mapping-2](Request-Mapping-2.png)
6. Code Snippet. ![Code-Snippet](Java-Code.png)
7. Source Code. ![Source-Code](activiti-extension-event-listener-java-code.zip)
8. The JAR File. ![Event-Listener.jar](activiti-extension-event-listener-jar-1.0-SNAPSHOT.jar)
9. Copy the jar file to tomcat-lib location. eg: /usr/local/tomcat/webapps/activiti-app/WEB-INF/lib/ 
10. Publish/Deploy the APS App.


### Run the DEMO

### References
1. https://www.activiti.org/userguide/#eventDispatcherListener
2. https://grokonez.com/java-integration/activiti-event-listener-spring-boot-example