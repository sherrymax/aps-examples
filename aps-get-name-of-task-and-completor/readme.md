#### The project contains the tips required to get a task name and name of user who completed that task

### Use-Case / Requirement
Upon a task completion, it is required to capture the details of the task. Mainly:
1. Task Name
2. Name of user who completes the task
These details can be useful for routing the workflow or to be maintained as part of the process data.

Here is a tip to quickly capture these details as part of ScriptTaskListener


### Prerequisites to run this demo end-2-end

* Alfresco Process Services (powered by Activiti) (Version 1.9 and above) - If you don't have it already, you can download a 30 day trial from [Alfresco Process Services (APS)](https://www.alfresco.com/products/business-process-management/alfresco-activiti).Instructions & help available at [Activiti Docs](http://docs.alfresco.com/activiti/docs/), [Alfresco BPM Community](https://community.alfresco.com/community/bpm)


## Configuration Steps

### Activiti Setup and Process Deployment
1. Setup Alfresco Activiti if you don't have one already. Instructions & help available at [Activiti Docs](http://docs.alfresco.com/activiti/docs/), [Alfresco BPM Community](https://community.alfresco.com/community/bpm)
2. Import the [Task-Data.zip](Task-Data.zip) app available in this project into Activiti.
3. The process flow.  ![Process-Flow](Process-Flow.png)
4. The process variables configuration. ![Process-Variables](Process-Variables.png)
5. The task listener. ![Task-Listener](Task-Listener.png)
6. The task listener configuration. ![Task-Listener-Configuration](Task-Listener-Configuration.png)
7. Publish/Deploy the App.

### Task Listener Configuration
1. ScriptTaskListener Event Class
```
org.activiti.engine.impl.bpmn.listener.ScriptTaskListener
```
2. language
```
javascript
```

3. script
```
task.execution.setVariable("requestSubmitter", userInfoBean.getFullName(userInfoBean.getCurrentUser().getId())); task.execution.setVariable("submittedTaskName", task.getName());
```

### Run the DEMO


### References
1. http://howtobrothers.com/2018/03/03/how-to-set-a-task-listener-on-the-app-designer-visual-editor-in-alfresco-process-services/
