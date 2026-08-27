# Comment History Stencil - Complete Implementation Guide

A step-by-step guide to implementing a fully functional Comment History stencil in Alfresco Process Services (APS) with direct Elasticsearch integration.

## 📋 Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Architecture](#architecture)
- [Step-by-Step Implementation](#step-by-step-implementation)
- [Groovy Initialization Script](#groovy-initialization-script)
- [Testing the Implementation](#testing-the-implementation)
- [Troubleshooting](#troubleshooting)
- [Tips & Best Practices](#tips--best-practices)
- [Summary](#summary)

---

## 🎯 Overview

This guide documents the complete implementation of a **Comment History Stencil** in APS that allows users to add and view comments within workflow tasks. Comments are stored directly in Elasticsearch with no backend service required.

### Key Features
✅ Add comments to workflow instances  
✅ View comment history in a table  
✅ Display current user information  
✅ Refresh comments manually  
✅ Auto-reload after submission  
✅ Mobile responsive design  
✅ Direct Elasticsearch integration  
✅ CORS-enabled for browser access  

---

## 📦 Prerequisites

### Required
- **Alfresco Process Services (APS)** v26.x
- **Elasticsearch** v7.x with CORS enabled
- Modern web browser (Chrome, Firefox, Safari, Edge)
- Access to APS admin/designer interface

### Elasticsearch CORS Configuration
Before starting, ensure Elasticsearch CORS is enabled:

**Option 1: Docker Environment Variables**
```bash
docker run -d \
  --name elasticsearch \
  -e discovery.type=single-node \
  -e "http.cors.enabled=true" \
  -e "http.cors.allow-origin=*" \
  -e "http.cors.allow-headers=X-Requested-With,Content-Type,Authorization" \
  -e "http.cors.allow-methods=OPTIONS,HEAD,GET,POST,PUT,DELETE" \
  -p 9200:9200 \
  docker.elastic.co/elasticsearch/elasticsearch:7.17.0
```

**Option 2: elasticsearch.yml**
```yaml
http.cors.enabled: true
http.cors.allow-origin: "*"
http.cors.allow-headers: "X-Requested-With,Content-Type,Authorization"
http.cors.allow-methods: "OPTIONS,HEAD,GET,POST,PUT,DELETE"
```

---

## 🏗️ Architecture

```
┌─────────────────────────────┐
│   APS Form Designer         │
│   (Stencil Configuration)   │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│   Form with Comment         │
│   History Stencil           │
└──────────────┬──────────────┘
               │
        ┌──────┴──────┐
        │             │
        ▼             ▼
    (Load)        (Save)
        │             │
        └──────┬──────┘
               │
               ▼
    ┌─────────────────────────┐
    │  Elasticsearch          │
    │  (Direct HTTP Calls)    │
    │  my_comments_one Index  │
    └─────────────────────────┘
```

**Data Flow:**
1. User opens form in APS task
2. Comment History stencil initializes
3. Browser loads existing comments from Elasticsearch
4. User submits comment
5. JavaScript sends POST request directly to Elasticsearch
6. Elasticsearch indexes the comment
7. After 5-second delay, comments reload
8. New comment appears in table

---

## 📝 Step-by-Step Implementation

### **Phase 1: Create & Configure the Stencil**

#### Step 1: Access Stencils Section
1. Log into APS
2. Click on **"Stencils"** tab at the top
3. You should see "You have X stencils"

![Step 1: Stencils Section](./screenshots/01-stencils-section.png)

**What You'll See:**
- Top navigation with tabs: Processes, Forms, Decision Tables, Apps, Data Models, **Stencils**
- "You have 4 stencils" message showing available stencils
- List of stencil libraries available
- Current user shown (Demo User)

#### Step 2: Select Stencil Library
1. Click on the stencil library that will contain Comment History
   - Example: **"Federal Stencil"**

![Step 2: Select Federal Stencil](./screenshots/02-federal-stencil.png)

**What You'll See:**
- Stencil library card showing "Federal Stencil"
- Version 55 badge
- Created by: Demo User
- Last updated: Yesterday at 4:05 AM
- Click to open the stencil library

#### Step 3: Open Comment History Stencil
1. Click on **"Comment History"** at the bottom of the list
2. The stencil editor opens showing available form components

![Step 3: Stencil Editor - Form Components](./screenshots/03-stencil-components.png)

**What You'll See:**
- Stencil Version 55
- Last updated: Yesterday at 4:05 AM
- Form component library on left side including:
  - Text, Multi-line text, Number, Checkbox, Date, Date and time
  - Dropdown, Typeahead, Amount, Radio buttons
  - People, Group of people, Dynamic table, Hyperlink
  - Container, Header, Attach File, Attach Folder
  - Display value, Display text
  - **Comment History** (at bottom)

#### Step 4: Configure Form Runtime Template
1. Scroll down to **"Form runtime template"** section
2. Replace the template with the HTML code
3. This includes:

![Step 4: Form Runtime Template](./screenshots/04-form-runtime-template.png)

**What You'll See:**
- Form editor template section with code editor
- Initial comment: `<!-- COMMENT HISTORY STENCIL -->`
- Red error indicator (X with "1") - this will disappear once you add complete HTML
- Large text area for HTML template code

**Action:**
- Clear the existing comment
- Paste the complete HTML template (with CSS and Angular directives)
- This includes:
   - Add comment section (textarea + submit button)
   - Comments history table
   - Refresh button with CSS spinner
   - Error/success messages

**Template Structure:**
```html
<!-- Add Comment Section -->
<div class="comment-section">
  <h3>Add Comment</h3>
  <textarea ng-model="newComment"></textarea>
  <button ng-click="submitComment()">SUBMIT COMMENT</button>
</div>

<!-- Comments History Section -->
<div class="comments-section">
  <div class="comments-header">
    <h3>Comment History</h3>
    <button ng-click="loadComments()">Refresh</button>
  </div>
  <table>
    <!-- Comments table -->
  </table>
</div>
```

#### Step 5: Add Custom Component Controller
1. Scroll to **"Define a custom component controller"**
2. Click **"Edit"** button

![Step 5: Custom Component Controller Edit Button](./screenshots/05-controller-edit-button.png)

**What You'll See:**
- Section titled "Define a custom component controller"
- Blue **"Edit"** button on the right
- Clicking Edit opens a code editor for the controller

3. Paste the Custom Component Controller (AngularJS) code:
  <details>
    <summary>
      Click to expand!!!
    </summary>

    ``` typescript
      
      /**
      * POC: Comment History Stencil - WITH REFRESH & 5-SECOND DELAY
      * 
      * Features:
      * - Dynamically fetches current user profile from APS
      * - Formats username as: "FirstName LastName (externalId)"
      * - Extracts processInstanceId from custom properties
      * - Stores and displays comments with full user info
      * - Direct Elasticsearch integration (no backend)
      * - Refresh button to reload comments manually
      * - 5-second delay before auto-reload after submission
      */

      angular.module('activitiApp').controller('CommentHistoryController', [
        '$scope',
        '$http',
        '$q',
        '$timeout',
        '$location',
        function($scope, $http, $q, $timeout, $location) {

          console.log('[CommentHistory] Controller loaded');

          // ============================================================
          // CONFIGURATION
          // ============================================================

          $scope.config = {
            commentDB_URL: '',
            commentIndex: ''
          };


          // ============================================================
          // SCOPE VARIABLES
          // ============================================================

          $scope.comments = [];
          $scope.newComment = '';
          $scope.loading = false;
          $scope.error = '';
          $scope.success = '';
          $scope.processId = null;
          $scope.currentUser = null;
          $scope.currentUserDisplayName = 'Unknown User';




          // ============================================================
          // GET CURRENT USER PROFILE - DYNAMIC FETCH
          // ============================================================

          /**
          * Get current user profile from APS form context
          * Does NOT make API calls - uses what's already in the page
          * Avoids logout issues
          * 
          * Formats as: "FirstName LastName (externalId)"
          */
          $scope.getCurrentUserProfile = function() {
            console.log('[CommentHistory] Getting current user profile from page context...');

            // Strategy 1: Check if user info is in $scope (from form/task context)
            // This is the safest - it's already loaded and won't cause session issues
            if ($scope.task && $scope.task.assignee) {
              console.log('[CommentHistory] ✓ Found user in $scope.task.assignee');
              console.log('[CommentHistory] User:', $scope.task.assignee);
              
              $scope.currentUserDisplayName = $scope.task.assignee;
              console.log('[CommentHistory] ✓ User display name:', $scope.currentUserDisplayName);
              
              return $q.when($scope.task.assignee);
            }

            // Strategy 2: Check various form context locations
            var userInfo = null;
            
            // Try appData
            if ($scope.appData && $scope.appData.user) {
              userInfo = $scope.appData.user;
              console.log('[CommentHistory] ✓ Found user in $scope.appData.user');
            }
            // Try formData
            else if ($scope.formData && $scope.formData.user) {
              userInfo = $scope.formData.user;
              console.log('[CommentHistory] ✓ Found user in $scope.formData.user');
            }
            // Try variables
            else if ($scope.variables && $scope.variables.user) {
              userInfo = $scope.variables.user;
              console.log('[CommentHistory] ✓ Found user in $scope.variables.user');
            }

            if (userInfo) {
              console.log('[CommentHistory] User info:', userInfo);
              
              // Handle if it's a string (just username)
              if (typeof userInfo === 'string') {
                $scope.currentUserDisplayName = userInfo;
              }
              // Handle if it's an object with firstName, lastName, externalId
              else if (typeof userInfo === 'object') {
                var firstName = userInfo.firstName || '';
                var lastName = userInfo.lastName || '';
                var externalId = userInfo.externalId || userInfo.id || userInfo.username || '';
                
                $scope.currentUserDisplayName = (firstName + ' ' + lastName).trim();
                
                if (externalId) {
                  $scope.currentUserDisplayName = $scope.currentUserDisplayName + ' (' + externalId + ')';
                }
              }
              
              console.log('[CommentHistory] ✓ User display name:', $scope.currentUserDisplayName);
              return $q.when(userInfo);
            }

            // Strategy 3: Check window object (last resort, no API call)
            if (window.alfUser) {
              console.log('[CommentHistory] ✓ Found user in window.alfUser');
              
              var firstName = window.alfUser.firstName || '';
              var lastName = window.alfUser.lastName || '';
              var externalId = window.alfUser.externalId || window.alfUser.id || '';
              
              $scope.currentUserDisplayName = (firstName + ' ' + lastName).trim() + ' (' + externalId + ')';
              console.log('[CommentHistory] ✓ User display name:', $scope.currentUserDisplayName);
              
              return $q.when(window.alfUser);
            }

            // Fallback: Use default
            console.log('[CommentHistory] ⚠️ Could not find user info in page context');
            console.log('[CommentHistory] Available in $scope:', Object.keys($scope).filter(function(k) { return !k.startsWith('$'); }));
            
            $scope.currentUserDisplayName = 'Demo User';
            console.log('[CommentHistory] Using default display name:', $scope.currentUserDisplayName);
            
            return $q.when(null);
          };

          // ============================================================
          // EXTRACT PROCESS ID FROM CUSTOM PROPERTIES
          // ============================================================

          $scope.extractProcessIdFromCustomProperties = function() {
            if ($scope.field && $scope.field.params && $scope.field.params.customProperties && $scope.field.params.customProperties.processInstanceId) {
              var processId = $scope.field.params.customProperties.processInstanceId.value;
              console.log('[CommentHistory] Extracted processInstanceId from custom properties:', processId);
              return processId;
            }
            console.warn('[CommentHistory] ⚠️ Could not find processInstanceId in custom properties');
            return null;
          };

          $scope.extractCommentDB_URLFromCustomProperties = function() {
            if ($scope.field && $scope.field.params && $scope.field.params.customProperties && $scope.field.params.customProperties.commentDB_URL) {
              var commentDB_URL = $scope.field.params.customProperties.commentDB_URL.value;
              console.log('[CommentHistory] Extracted commentDB_URL from custom properties:', commentDB_URL);
              return commentDB_URL;
            }
            console.warn('[CommentHistory] ⚠️ Could not find commentDB_URL in custom properties');
            return null;
          };

          $scope.extractCommentIndexFromCustomProperties = function() {
            if ($scope.field && $scope.field.params && $scope.field.params.customProperties && $scope.field.params.customProperties.commentIndex) {
              var indexName = $scope.field.params.customProperties.commentIndex.value;
              console.log('[CommentHistory] Extracted indexName from custom properties:', indexName);
              return indexName;
            }
            console.warn('[CommentHistory] ⚠️ Could not find commentIndex in custom properties');
            return null;
          };


          

          // ============================================================
          // INITIALIZE
          // ============================================================

          /**
          * Initialize - get process ID and user profile, then load comments
          */
          $scope.init = function() {
            console.log('[CommentHistory] === INITIALIZING ===');

            $scope.config.commentDB_URL = $scope.extractCommentDB_URLFromCustomProperties();
            $scope.config.commentIndex = $scope.extractCommentIndexFromCustomProperties();

            // Step 1: Extract process ID from URL
            $scope.processId = $scope.extractProcessIdFromCustomProperties();

            if (!$scope.processId) {
              console.error('[CommentHistory] ✗ FATAL: Could not extract processInstanceId from URL');
              $scope.error = 'Error: Could not determine process instance ID from URL.';
              return;
            }

            console.log('[CommentHistory] ✓ Process ID extracted:', $scope.processId);

            // Step 2: Get current user profile
            console.log('[CommentHistory] Getting user profile...');
            $scope.getCurrentUserProfile().then(function(userProfile) {
              console.log('[CommentHistory] ✓ User profile loaded');
              console.log('[CommentHistory] Current user display name:', $scope.currentUserDisplayName);
              
              console.log('[CommentHistory] ✓ Ready to load and submit comments');
              console.log('[CommentHistory] === INITIALIZATION COMPLETE ===');
              
              // Step 3: Load existing comments
              $scope.loadComments();
            });
          };

          /**
          * Load comments from Elasticsearch
          * Can be called manually (refresh button) or automatically (on init/submit)
          */
          $scope.loadComments = function() {
            console.log('[CommentHistory] loadComments() called');
            console.log('[CommentHistory] Process ID:', $scope.processId);

            if (!$scope.processId) {
              console.error('[CommentHistory] Cannot load - processInstanceId is null/undefined');
              $scope.error = 'Error: No process instance ID';
              return;
            }

            $scope.loading = true;
            $scope.error = '';

            // Build Elasticsearch query
            var query = {
              query: {
                term: {
                  processInstanceId: $scope.processId
                }
              },
              sort: [{ timestamp: { order: 'desc' } }],
              size: 100
            };

            var url = $scope.config.commentDB_URL + '/' + $scope.config.commentIndex + '/_search';

            console.log('[CommentHistory] Query URL:', url);
            console.log('[CommentHistory] Searching for processInstanceId:', $scope.processId);
            console.log('[CommentHistory] Query:', JSON.stringify(query));

            $http.post(url, query, {
              headers: { 'Content-Type': 'application/json' }
            }).then(function(response) {
              console.log('[CommentHistory] ✓ Query succeeded');
              console.log('[CommentHistory] Total hits:', response.data.hits.total);
              console.log('[CommentHistory] Found', response.data.hits.hits.length, 'comments');

              // Transform Elasticsearch response to simple format
              $scope.comments = response.data.hits.hits.map(function(hit) {
                return {
                  id: hit._id,
                  comment: hit._source.comment,
                  username: hit._source.username || 'Unknown User',
                  timestamp: hit._source.timestamp
                };
              });

              console.log('[CommentHistory] ✓ Comments loaded:', $scope.comments.length);
              $scope.loading = false;

            }).catch(function(error) {
              console.error('[CommentHistory] ✗ Query failed');
              console.error('[CommentHistory] Status:', error.status);
              console.error('[CommentHistory] Error:', error.data || error.statusText);

              if (error.status === 404) {
                console.log('[CommentHistory] Index does not exist yet (404) - no comments for this process');
                $scope.comments = [];
                $scope.loading = false;
              } else if (error.status === 0) {
                console.error('[CommentHistory] Network error - cannot reach Elasticsearch');
                $scope.error = 'Cannot reach Elasticsearch at ' + $scope.config.commentDB_URL + '. Check CORS and that it is running.';
                $scope.loading = false;
              } else {
                var errorMsg = error.data && error.data.error ? error.data.error.reason : error.statusText;
                console.error('[CommentHistory] Error message:', errorMsg);
                $scope.error = 'Failed to load comments: ' + errorMsg;
                $scope.loading = false;
              }
            });
          };

          /**
          * Submit new comment
          * Uses dynamically fetched user info: "FirstName LastName (externalId)"
          */
          $scope.submitComment = function() {
            console.log('[CommentHistory] === SUBMIT COMMENT ===');

            // Validate comment text
            if (!$scope.newComment || $scope.newComment.trim() === '') {
              console.warn('[CommentHistory] Empty comment rejected');
              $scope.error = 'Please enter a comment';
              return;
            }

            // Validate process ID
            if (!$scope.processId) {
              console.error('[CommentHistory] ✗ Cannot submit - processInstanceId is null!');
              $scope.error = 'Error: No process ID. Cannot save comment.';
              return;
            }

            // Validate user display name
            if (!$scope.currentUserDisplayName || $scope.currentUserDisplayName === 'Unknown User') {
              console.warn('[CommentHistory] ⚠️ User display name not loaded, will use: ' + $scope.currentUserDisplayName);
            }

            $scope.loading = true;
            $scope.error = '';

            // Build document with dynamically fetched user info
            var doc = {
              processInstanceId: $scope.processId,
              comment: $scope.newComment.trim(),
              username: $scope.currentUserDisplayName,  // Format: "FirstName LastName (externalId)"
              timestamp: new Date().toISOString()
            };

            var url = $scope.config.commentDB_URL + '/' + $scope.config.commentIndex + '/_doc';

            console.log('[CommentHistory] Submitting comment...');
            console.log('[CommentHistory] URL:', url);
            console.log('[CommentHistory] Document:', JSON.stringify(doc));
            console.log('[CommentHistory]   - ProcessInstanceId:', doc.processInstanceId);
            console.log('[CommentHistory]   - Comment:', doc.comment);
            console.log('[CommentHistory]   - Username (with userid):', doc.username);
            console.log('[CommentHistory]   - Timestamp:', doc.timestamp);

            // Send to Elasticsearch
            $http.post(url, doc, {
              headers: { 'Content-Type': 'application/json' }
            }).then(function(response) {
              console.log('[CommentHistory] ✓ Comment submitted successfully!');
              console.log('[CommentHistory] Document ID:', response.data._id);
              console.log('[CommentHistory] Response:', response.data);

              $scope.newComment = ''; // Clear textarea
              $scope.success = 'Comment added!';

              // Clear success message after 3 seconds
              $timeout(function() {
                $scope.success = '';
              }, 3000);

              // Wait 5 seconds before reloading comments (gives Elasticsearch time to index)
              console.log('[CommentHistory] Waiting 5 seconds before reloading comments...');
              $timeout(function() {
                console.log('[CommentHistory] 5-second delay complete, reloading comments...');
                $scope.loadComments();
              }, 5000);

            }).catch(function(error) {
              console.error('[CommentHistory] ✗ Submit failed!');
              console.error('[CommentHistory] Status:', error.status);
              console.error('[CommentHistory] Error:', error.data || error.statusText);

              var errorMsg = error.data && error.data.error ? error.data.error.reason : error.statusText;
              $scope.error = 'Failed to submit comment: ' + errorMsg;
              $scope.loading = false;
            });
          };

          /**
          * Format timestamp for display (full date/time)
          */
          $scope.formatTime = function(isoString) {
            if (!isoString) return 'Unknown';
            try {
              var date = new Date(isoString);
              return date.toLocaleString();
            } catch (e) {
              return isoString;
            }
          };

          /**
          * Format timestamp for display (relative time)
          * Examples: "just now", "2 minutes ago", "3 hours ago"
          */
          $scope.timeAgo = function(isoString) {
            if (!isoString) return '';
            try {
              var date = new Date(isoString);
              var now = new Date();
              var seconds = Math.floor((now - date) / 1000);

              if (seconds < 60) return 'just now';
              if (seconds < 3600) return Math.floor(seconds / 60) + ' min ago';
              if (seconds < 86400) return Math.floor(seconds / 3600) + ' hours ago';
              return Math.floor(seconds / 86400) + ' days ago';
            } catch (e) {
              return '';
            }
          };

          // ============================================================
          // LIFECYCLE HOOKS
          // ============================================================

          console.log('[CommentHistory] Setting up lifecycle hooks');

          // Initialize when view content is loaded
          $scope.$on('$viewContentLoaded', function() {
            console.log('[CommentHistory] $viewContentLoaded event fired');
            $scope.init();
          });

          // Also try init after a delay (fallback in case event doesn't fire)
          $timeout(function() {
            console.log('[CommentHistory] Delayed init check - processId:', $scope.processId);
            if (!$scope.processId) {
              console.log('[CommentHistory] ProcessId not set, calling init() from timeout');
              $scope.init();
            } else {
              console.log('[CommentHistory] ProcessId already set, skipping delayed init');
            }
          }, 500);

        }
      ]);

      console.log('[CommentHistory] Controller registration complete');
    ```
  </details>
<br/><br/>

**Key Functions:**
- `loadComments()` - Fetch comments from Elasticsearch
- `submitComment()` - Save comment and reload
- `getCurrentUserProfile()` - Get user info from APS context
- `formatTime()` - Format timestamps

#### Step 6: Configure Custom Properties
1. Scroll to **"Tabs and properties"** section
2. Click **"Edit"** button

![Step 6A: Tabs and Properties Section](./screenshots/06a-tabs-and-properties.png)

**What You'll See:**
- Section titled "Tabs and properties" with blue **"Edit"** button
- Below it shows:
  - **General:** label, id, required, processInstanceId, commentDB_URL, commentIndex
  - **Visibility:** visibility

3. After clicking Edit, a dialog opens showing editable properties:

![Step 6B: Edit Stencil Properties Dialog](./screenshots/06b-edit-properties-dialog.png)

**What You'll See:**
- Title: "Edit stencil properties - Comment History"
- Two tabs: **General** and **Visibility**
- Tab name: "FORM-BUILDER.TABS.GENERAL"
- Three custom properties listed on the left:
  - **Process Instance Id** (highlighted)
  - **commentDB_URL**
  - **commentIndex**
- Right panel shows details for selected property
- Options to move (arrow buttons) and delete (X button)
- **"+ Add new tab"** and **"+ Add new property"** buttons

4. Click **"+ Add new property"** three times to add properties with **Type: Variable**:

| Property | Name | Type | Description |
|----------|------|------|-------------|
| processInstanceId | Process Instance ID | Variable | Workflow instance ID |
| commentDB_URL | Elasticsearch URL | Variable | Elasticsearch endpoint (e.g., http://elasticsearch:9200) |
| commentIndex | Index Name | Variable | Elasticsearch index name (e.g., my_comments_one) |

#### Step 7: Close & Publish
1. Click **"Close"** to save properties
2. Click **"Publish"** to publish the stencil

---

### **Phase 2: Create & Configure the Form**

#### Step 8: Go to Forms Section
1. Click on **"Forms"** tab at the top

#### Step 9: Create or Open a Form
1. Create a new form or open existing form
   - Example: "Gather" form

#### Step 10: Add Comment History Stencil
1. In the form designer, add the Comment History stencil
2. Label it as **"My Comments"**
3. The component appears with label "COMMENT HISTORY"

![Step 10: Form with Comment History Stencil](./screenshots/10-form-designer.png)

**What You'll See:**
- Form designer for "Gather" form (Version 30)
- Top navigation tabs: Design, Tabs, Outcomes, Style, Javascript, Properties
- Left side: Form components available to add
- Center: Form layout showing:
  - Section: "My Comments"
  - Component: "COMMENT HISTORY" (in blue link)
  - Edit (pencil) icon and delete (X) icon on the right

#### Step 11: Configure Stencil Properties
1. Click the **pencil icon** on the Comment History component

![Step 11A: Click Pencil Icon](./screenshots/11a-pencil-icon.png)

**What You'll See:**
- Form section "My Comments"
- Comment History component in blue
- Pencil (edit) icon on the right side
- X (delete) icon next to it

2. The **"Edit custom field 'My Comments'"** dialog opens

![Step 11B: Edit Custom Field Dialog](./screenshots/11b-edit-custom-field.png)

**What You'll See:**
- Dialog title: "Edit custom field 'My Comments'"
- Two tabs: **General** and **Visibility**
- Label: "My Comments"
- ID: "mycomments"
- Override ID checkbox
- Required checkbox
- Three custom properties sections:
  1. **Process Instance Id**
     - Value: "processInstanceId - processInstanceId - string"
     - Toggle buttons: "Form field" and "Variable"
  2. **commentDB_URL**
     - Value: "commentDB_URL - commentDB_URL - string"
     - Toggle buttons: "Form field" and "Variable"
  3. **commentIndex**
     - Value: "commentIndex - commentIndex - string"
     - Toggle buttons: "Form field" and "Variable"
- Blue **"Close"** button at bottom right

3. Configure the three custom properties:
   - **Process Instance Id**: Select "Variable" ✓
   - **commentDB_URL**: Select "Variable" ✓
   - **commentIndex**: Select "Variable" ✓

#### Step 12: Save & Publish Form
1. Click **"Close"** to save configuration
2. Click **"Save"** to save the form
3. Click **"Publish"** to publish the form

---

### **Phase 3: Create Process with Initialization Script**

#### Step 13: Create or Edit a Process
1. Go to **"Processes"** tab
2. Create a new process or edit existing one
3. Add the form you created as a user task

#### Step 14: Add Groovy Initialization Script
1. In the process definition, add a task before the form
2. Or use a script task to initialize variables
3. Add the initialization script (see next section)

---

## 🔧 Groovy Initialization Script

Add this Groovy script to your process to initialize the Comment History custom properties:

![Step 14: Groovy Initialization Script](./screenshots/14-groovy-script.png)

**What You'll See:**
- Groovy script code in a script editor
- Three `execution.setVariable()` calls:
  1. `execution.setVariable("processInstanceId", execution.getProcessInstanceId());`
  2. `execution.setVariable("commentDB_URL", "http://federal.alfdemo.com:9200");`
  3. `execution.setVariable("commentIndex", "my_comments_one");`
- Color-coded syntax highlighting

```groovy
// Initialize Comment History Stencil Properties

// Get the current process instance ID dynamically
execution.setVariable("processInstanceId", execution.getProcessInstanceId());

// Set Elasticsearch URL (change to your Elasticsearch server)
execution.setVariable("commentDB_URL", "http://federal.alfdemo.com:9200");

// Set Elasticsearch index name for comments
execution.setVariable("commentIndex", "my_comments_one");

// Optional: Log for debugging
println("Comment History initialized:");
println("  Process Instance ID: " + execution.getProcessInstanceId());
println("  Elasticsearch URL: http://federal.alfdemo.com:9200");
println("  Index Name: my_comments_one");
```

### Where to Add This Script
**Option 1: Script Task in Process**
- Add a **Script Task** in the process
- Set **Script Format**: Groovy
- Paste the script above
- Execute before the form task

**Option 2: Service Task**
- Add **Service Task** with Groovy script
- Same script content

**Option 3: Form Task**
- In the task properties, add **Form Start Script**
- Paste the script

### What This Script Does
✅ `execution.getProcessInstanceId()` - Gets unique process instance ID  
✅ Sets Elasticsearch URL for the form  
✅ Sets index name for comment storage  
✅ Logs initialization for debugging  

---

## 🧪 Testing the Implementation

### Step 15: Save & Publish Process
1. Save the process definition
2. Publish the process

### Step 16: Start a Process Instance
1. Go to **"Processes"** or **"Apps"**
2. Start a process instance
3. Complete any pre-form tasks
4. Reach the form task with Comment History

### Step 17: Test Adding Comments
1. **Open the form** in the task
2. **Type a comment** in the textarea
3. **Click "SUBMIT COMMENT"** button
4. **Verify:**
   - ✓ Success message appears: "✓ Comment added!"
   - ✓ Textarea clears
   - ✓ Wait 5 seconds
   - ✓ Comment appears in the table

### Step 18: Test Viewing Comments
1. **Refresh the form** (F5)
2. **Verify:**
   - ✓ Previously submitted comments appear in table
   - ✓ User name shows in format: "FirstName LastName (username)"
   - ✓ Timestamp shows both full date and relative time ("2 hours ago")

### Step 19: Test Manual Refresh
1. **Click "Refresh" button** in comments table header
2. **Verify:**
   - ✓ Spinner icon appears while loading
   - ✓ Button is disabled during refresh
   - ✓ Comments reload from Elasticsearch

---

## 🐛 Troubleshooting

### Issue 1: "Cannot reach Elasticsearch"

**Error Message:**
```
Cannot reach Elasticsearch at http://federal.alfdemo.com:9200. 
Check CORS and that it is running.
```

**Solutions:**
1. **Verify Elasticsearch is running:**
   ```bash
   curl http://elasticsearch:9200
   ```
   Should return cluster info

2. **Check CORS is enabled:**
   ```bash
   curl -i http://elasticsearch:9200/ \
     -H "Origin: http://your-aps-url"
   ```
   Should include `Access-Control-Allow-Origin` header

3. **Verify URL in form properties:**
   - Make sure `commentDB_URL` matches your Elasticsearch server
   - Check for typos in hostname/port

### Issue 2: "Could not determine process instance ID"

**Error Message:**
```
Error: Could not determine process instance ID.
```

**Solutions:**
1. **Verify Groovy script ran:**
   - Check process logs for any errors
   - Ensure script task executed before form task

2. **Check variable is set:**
   - In process definition, verify `processInstanceId` variable exists
   - Check variable scope (should be process-level)

3. **Verify custom property configuration:**
   - In form designer, check that property is set to "Variable" type
   - Not "Form field"

### Issue 3: Comments Not Appearing After Submit

**Problem:** Comment submitted successfully but doesn't appear in table

**Solutions:**
1. **Wait for indexing:**
   - Elasticsearch has 5-second delay built-in
   - Wait and comments should appear automatically

2. **Manually refresh:**
   - Click "Refresh" button to force reload

3. **Check Elasticsearch data:**
   ```bash
   curl http://elasticsearch:9200/my_comments_one/_search?pretty
   ```
   Should show your comment in results

### Issue 4: User Shows "Unknown User"

**Problem:** Comment shows "Unknown User" instead of actual name

**Solutions:**
1. **Check form context:**
   - Stencil tries to get user from `$scope.task.assignee`
   - If not available, defaults to "Unknown User"

2. **Verify task assignment:**
   - User should be assigned to the task
   - Check in APS task properties

3. **Check browser console:**
   - Open DevTools (F12)
   - Look for `[CommentHistory]` logs
   - Should show: "Getting current user profile from page context"

### Issue 5: CORS Error

**Error Message:**
```
Access to XMLHttpRequest at 'http://elasticsearch:9200/...' 
from origin 'http://your-aps-url' has been blocked by CORS policy
```

**Solutions:**
1. **Enable CORS on Elasticsearch:**
   - If Docker, restart with CORS environment variables
   - If standalone, update elasticsearch.yml

2. **Verify CORS headers:**
   ```bash
   curl -i http://elasticsearch:9200/ \
     -H "Origin: http://federal.alfdemo.com"
   ```

3. **Check Elasticsearch config:**
   ```bash
   curl http://elasticsearch:9200/_nodes/settings?pretty | grep cors
   ```

### Issue 6: Index Not Found (404)

**Error Message:**
```
{"error":{"type":"index_not_found_exception"...}}
```

**Solutions:**
1. **Create the index:**
   ```bash
   curl -X PUT http://elasticsearch:9200/my_comments_one
   ```

2. **Verify index exists:**
   ```bash
   curl http://elasticsearch:9200/my_comments_one
   ```

3. **Check index name:**
   - Verify `commentIndex` value matches
   - Index names are case-sensitive
   - No uppercase letters allowed

---

## 💡 Tips & Best Practices

### 1. **Test Elasticsearch Connectivity Early**
```bash
# Test basic connectivity
curl http://elasticsearch:9200

# Test CORS
curl -i http://elasticsearch:9200/ \
  -H "Origin: http://your-aps-url"

# Check cluster health
curl http://elasticsearch:9200/_cluster/health?pretty
```

### 2. **Use Browser Console for Debugging**
Press **F12** in browser and look for:
```javascript
// These are helpful:
[CommentHistory] logs - Detailed operation logs
[CommentHistory] ✓ - Success messages
[CommentHistory] ✗ - Error messages

// In console, you can also run:
angular.element(document).injector().get('$scope').comments
// Shows loaded comments

angular.element(document).injector().get('$scope').loadComments()
// Force reload comments
```

### 3. **Groovy Script Best Practices**
```groovy
// Good: Use meaningful variable names
execution.setVariable("processInstanceId", execution.getProcessInstanceId());

// Good: Add logging for debugging
println("Comment History initialized for process: " + processInstanceId);

// Avoid: Hardcoding sensitive data
// Instead, use process variables or configuration
```

### 4. **Index Naming Convention**
```bash
# Use descriptive names
my_comments_one          # Good
my_comments_process_5040 # Good
comments                 # Too generic
C0MMents                 # Invalid (uppercase)
my-comments             # Avoid (hyphens can cause issues)
```

### 5. **Monitor Comments in Elasticsearch**
```bash
# Check all comments
curl http://elasticsearch:9200/my_comments_one/_search?pretty

# Filter by process instance
curl http://elasticsearch:9200/my_comments_one/_search?pretty -d '
{
  "query": {
    "term": {
      "processInstanceId": "5040"
    }
  }
}'

# Get document count
curl http://elasticsearch:9200/my_comments_one/_count
```

### 6. **Security Considerations**
⚠️ **This POC is NOT production-ready:**
- Direct Elasticsearch access (no authentication)
- No user permission checks
- Not HTTPS secured

✅ **For Production:**
- Add backend API layer (Spring Boot)
- Implement authentication
- Use role-based access control (RBAC)
- Enable Elasticsearch X-Pack security
- Use HTTPS only
- Add input sanitization
- Log all operations

---

## 📊 Summary

### What You've Built

A fully functional **Comment History Stencil** for APS that:

✅ Allows users to comment on workflow instances  
✅ Displays comment history in a clean table  
✅ Shows user names in format: "FirstName LastName (username)"  
✅ Provides manual refresh with visual feedback  
✅ Auto-reloads after 5 seconds  
✅ Stores comments in Elasticsearch  
✅ Works on mobile and desktop  
✅ Has comprehensive error handling  

### Files Used

| File | Purpose |
|------|---------|
| `commentHistoryStencil_WITH_REFRESH.js` | AngularJS controller |
| `commentHistoryStencil_WITH_SPINNER.html` | HTML template with CSS |
| Groovy script | Initialize properties |

### Architecture Summary

```
Process Definition
  ├─ Groovy Script Task
  │  └─ Initialize: processInstanceId, commentDB_URL, commentIndex
  │
  └─ Form Task
     └─ Comment History Stencil
        ├─ Add Comment Section
        ├─ Comments History Table
        └─ Elasticsearch Integration
```

### Key Learnings

1. **Custom Properties** are configured in stencil but valued in form/process
2. **Groovy Scripts** initialize variables that the form uses
3. **CORS must be enabled** on Elasticsearch for browser access
4. **Direct HTTP calls** from browser to Elasticsearch require careful configuration
5. **5-second delay** allows time for Elasticsearch indexing
6. **User information** comes from APS task context, not an API call

---

## 📚 Next Steps

### To Improve This Solution

1. **Add Comment Deletion** - Allow users to delete their comments
2. **Add Comment Editing** - Let users edit submitted comments
3. **Add Backend Service** - Wrap Elasticsearch behind a Spring Boot API
4. **Add Permissions** - Implement role-based access control
5. **Add Notifications** - Notify users of new comments
6. **Add Attachments** - Allow file attachments to comments
7. **Add @ Mentions** - Tag other users in comments
8. **Add Search** - Search through comments

### To Make It Production-Ready

1. Remove direct Elasticsearch access
2. Add authentication layer
3. Implement authorization checks
4. Use HTTPS only
5. Add input sanitization
6. Add comprehensive audit logging
7. Add backup/recovery strategy
8. Add monitoring and alerting

---

## 🔗 Additional Resources

- [Elasticsearch Documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/)
- [AngularJS 1.x Docs](https://angularjs.org/)
- [APS Developer Guide](https://docs.alfresco.com/process-services/latest/)
- [Groovy Documentation](https://groovy-lang.org/)

---

## 📞 Support & Questions

If you encounter issues:

1. **Check browser console** (F12) for `[CommentHistory]` logs
2. **Verify Elasticsearch** is running and CORS enabled
3. **Check Groovy script** executed successfully
4. **Verify custom properties** are set correctly
5. **Test Elasticsearch** connectivity with curl

---

---

## 📸 Screenshots Directory Structure

To use this guide with screenshots, create the following directory structure in your repository:

```
your-repo/
├── IMPLEMENTATION_GUIDE.md (this file)
├── README.md
├── screenshots/
│   ├── 01-stencils-section.png
│   ├── 02-federal-stencil.png
│   ├── 03-stencil-components.png
│   ├── 04-form-runtime-template.png
│   ├── 05-controller-edit-button.png
│   ├── 06a-tabs-and-properties.png
│   ├── 06b-edit-properties-dialog.png
│   ├── 10-form-designer.png
│   ├── 11a-pencil-icon.png
│   ├── 11b-edit-custom-field.png
│   └── 14-groovy-script.png
├── code/
│   ├── commentHistoryStencil_WITH_REFRESH.js
│   ├── commentHistoryStencil_WITH_SPINNER.html
│   └── groovy-initialization-script.groovy
└── docs/
    └── DEPLOY_GUIDE.md
```

### Screenshot Reference Guide

| Screenshot | Step | Description |
|-----------|------|-------------|
| 01-stencils-section.png | 1 | APS Stencils page showing navigation |
| 02-federal-stencil.png | 2 | Federal Stencil library card |
| 03-stencil-components.png | 3 | Stencil editor with form components |
| 04-form-runtime-template.png | 4 | Form runtime template section |
| 05-controller-edit-button.png | 5 | Controller Edit button |
| 06a-tabs-and-properties.png | 6 | Tabs and properties section |
| 06b-edit-properties-dialog.png | 6 | Edit stencil properties dialog |
| 10-form-designer.png | 10 | Form designer with Comment History |
| 11a-pencil-icon.png | 11 | Pencil icon to edit component |
| 11b-edit-custom-field.png | 11 | Edit custom field configuration |
| 14-groovy-script.png | 14 | Groovy initialization script |

---

## 🎯 Visual Journey

### Complete Flow Diagram

```
START
  │
  ├─ Step 1-3: Navigate to Stencil Editor
  │   └─ Stencils → Federal Stencil → Comment History
  │
  ├─ Step 4-5: Configure Stencil Code
  │   ├─ Add HTML template (with CSS)
  │   └─ Add AngularJS controller
  │
  ├─ Step 6-7: Configure Properties & Publish
  │   ├─ Define: processInstanceId, commentDB_URL, commentIndex
  │   └─ Publish stencil
  │
  ├─ Step 8-12: Create Form & Configure Properties
  │   ├─ Forms → Create form → Add Comment History
  │   ├─ Configure properties as "Variable" type
  │   └─ Save & Publish form
  │
  ├─ Step 13-14: Create Process with Groovy Script
  │   ├─ Processes → Create process
  │   ├─ Add Groovy script to initialize variables
  │   └─ Save & Publish process
  │
  ├─ Step 15-19: Test the Implementation
  │   ├─ Start process instance
  │   ├─ Submit comment
  │   ├─ View comments
  │   ├─ Refresh comments
  │   └─ Verify all features
  │
  └─ END: Fully Functional Comment History
```

---

## ✅ Visual Checklist

### Configuration Phase
- [ ] Navigate to Stencils (Screenshot 01)
- [ ] Select Federal Stencil (Screenshot 02)
- [ ] Open Comment History stencil (Screenshot 03)
- [ ] Configure Form Runtime Template (Screenshot 04)
- [ ] Add Custom Controller (Screenshot 05)
- [ ] Edit Properties (Screenshots 06a & 06b)
- [ ] Publish stencil

### Form Phase
- [ ] Create form (Gather)
- [ ] Add Comment History component
- [ ] Click pencil icon (Screenshot 11a)
- [ ] Configure custom properties (Screenshot 11b)
- [ ] Save form
- [ ] Publish form

### Process Phase
- [ ] Create process
- [ ] Add Groovy initialization script (Screenshot 14)
- [ ] Save process
- [ ] Publish process

### Testing Phase
- [ ] Start process instance
- [ ] Open form with Comment History
- [ ] Submit a comment
- [ ] Verify it appears in table
- [ ] Test refresh button
- [ ] Verify user name format
- [ ] Check error handling

---

**Version:** 1.0.0  
**Status:** Complete Implementation Guide with Screenshots  
**Last Updated:** August 26, 2026  
**Compatibility:** APS v26.x, Elasticsearch 7.x  

---

**Happy commenting! 🎉**