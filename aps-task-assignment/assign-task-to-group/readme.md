#### The project contains all the components required to assign an APS human task to one or more groups.

### Use-Case / Requirement
Build a process to select a `Reviewer group` and assign the `Review Task` to that selected `Reviewer group`.


### Prerequisites to run this demo end-2-end

* Alfresco Process Services (powered by Activiti) (Version 1.9 and above) - If you don't have it already, you can download a 30 day trial from [Alfresco Process Services (APS)](https://www.alfresco.com/products/business-process-management/alfresco-activiti).Instructions & help available at [Activiti Docs](http://docs.alfresco.com/activiti/docs/), [Alfresco BPM Community](https://community.alfresco.com/community/bpm)


## Configuration Steps

### Activiti Setup and Process Deployment
1. Import the [Assign-Task-to-Selected-Group.zip](Assign-Task-to-Selected-Group.zip) app available in this project into APS.
2. The process flow.  ![Process-Flow](images/Process-Flow.png)
3. The Groovy script configuration.
   ```
    import com.activiti.domain.idm.User;
    import com.activiti.domain.idm.Group;
    import com.activiti.security.SecurityUtils;
    import com.activiti.service.idm.GroupServiceImpl;

    out.println('Start - Init');
    User currentUser = SecurityUtils.getCurrentUserObject();
    ArrayList idList = new ArrayList();
    Long tenantId = currentUser.getTenantId();

    List<Group> groups = groupServiceImpl.getGroupByNameAndTenantId(reviewerteam_LABEL, tenantId);

    for (Group g : groups) {
        idList.add(String.valueOf(g.getId()));
    }

    execution.setVariable('assigneeGroupList', idList);
    out.println('End - Init');
    ```

4. Configure Task Assignment Property.
   ![Task-Assignment-Property](images/Task-Assignment-Property.png)
   ![Task-Assignment-Property-Popup](images/Task-Assignment-Property-Popup.png)

5. Publish/Deploy the App.


### Run the DEMO
1. Run the process instance and come to 'Review Team' task. Please note that '1 group(s) involved' would be the one that was selected earlier.
    ![Final-Result](images/Final-Result.png)

### Tip of the day
1. There are beans and services created and available through out activiti and APS code.
   1. com.activiti.service.idm.UserServiceImpl.java - Beans
   2. com.activiti.service.idm.GroupServiceImpl.java - Beans
2. Those files with `impl` in their file name are the implementation (beans) of corresponding interface/services.
   eg: UserServiceImpl.java is the implementation for UserService.java
3. The UserServiceImpl.java implementation file has a `@Service` name.
   1. So invoking points in APS will be the value of `@Service`.
   2. eg: UserServiceImpl.java has the service name as `@Service("userService")`.
   3. In APS, this service is available as ` List<User> users = userService.getAllUsers(0,999,tenantId);`
4. The GroupServiceImpl.java implementation file does not have a `@Service` name.
   1. No service name is mentioned in `@Service` of GroupServiceImpl.java.
   2. Since service name is empty, the implementation file's name, with first letter in lower case, is assigned as default service name.
   3. Invoking points in APS will be `groupServiceImpl`.
   4. In APS, this service is available as ` List<Group> groups = groupServiceImpl.getGroupByNameAndTenantId('FBIAnalyst', 1L);`

### References
1. http://docs.alfresco.com/activiti/docs/user-guide/1.5.0/
2. http://docs.alfresco.com/activiti/docs/user-guide/1.5.0/#_assigning_tasks
