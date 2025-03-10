#### The project contains the tips required to generate a document with image uploaded from an APS Form

### Use-Case / Requirement

As part of the process, it is required to capture an image using APS Form and generate a document out of a template and upload to ECM.

### Prerequisites to run this demo end-2-end

* Alfresco Process Services (powered by Activiti) (Version 1.9 and above) - If you don't have it already, you can download a 30 day trial from [Alfresco Process Services (APS)](https://www.alfresco.com/products/business-process-management/alfresco-activiti).Instructions & help available at [Activiti Docs](http://docs.alfresco.com/activiti/docs/), [Alfresco BPM Community](https://community.alfresco.com/community/bpm)

## Configuration Steps

### Activiti Setup and Process Deployment

1. Setup Alfresco Activiti if you don't have one already. Instructions & help available at [Activiti Docs](http://docs.alfresco.com/activiti/docs/), [Alfresco BPM Community](https://community.alfresco.com/community/bpm)
2. Import the [Attach-Image-To-Document.zip](Attach-Image-To-Document.zip) app available in this project into Activiti.
3. The process flow.  ![Process-Flow](images/Process-Flow.png)
4. The process variables configuration. ![Process-Variables](images/Process-Variables.png)
5. The Generate Document Task. ![Generate-Document-Task](images/Generate-Document-Task.png)
6. The Generate Document Task : Custom Template. ![Generate-Document-Task-Custom-Template](images/Generate-Document-Task-Custom-Template.png)
[A custom template is available here](images/Generate-Doc-Template.docx):

7. The Generate Document Task : Global Template. ![Generate-Document-Task-Global-Template](images/Generate-Document-Task-Global-Template.png)
8. The Upload to ACS Task configuration. ![Upload-To-ACS-Task](images/Upload-To-ACS-Task.png)
![Upload-To-ACS-Task-Configuration](images/Upload-To-ACS-Task-Configuration.png)
9. Publish/Deploy the App.

### Run the DEMO

### References

1. <https://docs.alfresco.com/process-services1.7/topics/document_templates.html>
