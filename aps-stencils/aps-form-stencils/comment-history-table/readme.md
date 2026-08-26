# Comment History Stencil - POC

An APS (Alfresco Process Services) Form Stencil for adding and viewing workflow comments. Comments are stored directly in Elasticsearch with no backend service required.

## 📋 Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [File Structure](#file-structure)
- [API Reference](#api-reference)
- [Troubleshooting](#troubleshooting)
- [Known Issues](#known-issues)
- [Future Enhancements](#future-enhancements)

---

## ✨ Features

### Core Features
- ✅ **Add Comments** - Users can submit comments within APS form tasks
- ✅ **View Comment History** - Display all comments for a process instance
- ✅ **Dynamic User Profile** - Shows current user as "FirstName LastName (username)"
- ✅ **Timestamp Formatting** - Both full date/time and relative time (e.g., "2 hours ago")
- ✅ **Direct Elasticsearch Integration** - No backend service required
- ✅ **CORS Support** - Works with Elasticsearch CORS enabled
- ✅ **Refresh Comments** - Manual refresh button with CSS spinner
- ✅ **Auto-Reload** - 5-second delay after submission before auto-reload
- ✅ **Process Instance Isolation** - Comments isolated by processInstanceId
- ✅ **Mobile Responsive** - Works on mobile and desktop

### Comment Format
Comments are stored with:
- **ProcessInstanceId** - Identifies which workflow instance
- **Comment Text** - The actual comment
- **Username** - Format: "FirstName LastName (externalId)"
- **Timestamp** - ISO 8601 format for sorting and display

---

## 🏗️ Architecture

### Technology Stack
- **Frontend**: AngularJS 1.x (compatible with APS)
- **Database**: Elasticsearch (direct browser calls, no backend)
- **HTTP Method**: POST for Elasticsearch queries
- **Data Format**: JSON

### Data Flow

```
┌──────────────────┐
│   APS Form       │
│   (HTML/JS)      │
└────────┬─────────┘
         │
         ├─────────────────────────┐
         │                         │
    (Browser)               (Browser)
         │                         │
         ▼                         ▼
┌──────────────────┐    ┌──────────────────┐
│ Load Comments    │    │ Save Comment     │
│ (HTTP POST)      │    │ (HTTP POST)      │
└────────┬─────────┘    └────────┬─────────┘
         │                       │
         └───────────┬───────────┘
                     │
                     ▼
         ┌─────────────────────────┐
         │  Elasticsearch          │
         │  (my_comments index)    │
         └─────────────────────────┘
```

### Configuration Sources

**Stencil Custom Properties** (set in APS form designer):
```javascript
{
  commentDB_URL: "http://elasticsearch:9200",
  commentIndex: "my_comments_one",
  processInstanceId: "5040"
}
```

---

## 📦 Prerequisites

### Server-Side
- **Alfresco Process Services (APS)** v26.x or compatible
- **Elasticsearch** v7.x with CORS enabled
- Java 8+ for APS

### Client-Side
- Modern browser (Chrome, Firefox, Safari, Edge)
- JavaScript enabled
- No additional dependencies

### Elasticsearch CORS Configuration

**Option 1: Environment Variables (Docker)**
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

## 📥 Installation

### Step 1: Download Files

Get the following files:
1. `commentHistoryStencil_WITH_REFRESH.js` - AngularJS controller
2. `commentHistoryStencil_WITH_SPINNER.html` - HTML template with spinner

### Step 2: Deploy to APS

```bash
cd /opt/alfresco/aps/tomcat/webapps/activiti-app/app/stencils/

# Backup existing files
mv commentHistoryStencil_POC.js commentHistoryStencil_POC.js.bak
mv commentHistoryStencil_POC.html commentHistoryStencil_POC.html.bak

# Copy new files
cp /path/to/commentHistoryStencil_WITH_REFRESH.js commentHistoryStencil_POC.js
cp /path/to/commentHistoryStencil_WITH_SPINNER.html commentHistoryStencil_POC.html
```

### Step 3: Restart APS

```bash
/opt/alfresco/aps/tomcat/bin/shutdown.sh
sleep 5
/opt/alfresco/aps/tomcat/bin/startup.sh
```

### Step 4: Verify Installation

1. Open APS form designer
2. Edit form containing the stencil
3. Open the form in a task
4. Check browser console (F12) for `[CommentHistory]` logs

---

## ⚙️ Configuration

### Stencil Custom Properties

In APS Form Designer, add these custom properties to the Comment History stencil:

**Property 1: commentDB_URL**
- **Display Name**: "Elasticsearch URL"
- **Property Name**: `commentDB_URL`
- **Type**: Text
- **Example**: `http://federal.alfdemo.com:9200`
- **Required**: Yes

**Property 2: commentIndex**
- **Display Name**: "Comment Index Name"
- **Property Name**: `commentIndex`
- **Type**: Text
- **Example**: `my_comments_one`
- **Required**: Yes

**Property 3: processInstanceId**
- **Display Name**: "Process Instance ID"
- **Property Name**: `processInstanceId`
- **Type**: Text
- **Example**: `5040` or use form variable
- **Required**: Yes

---

## 🚀 Usage

### For End Users

**Adding a Comment:**
1. Type in the "Add Comment" textarea
2. Click "SUBMIT COMMENT" button
3. See success message "✓ Comment added!"
4. Wait 5 seconds for auto-reload
5. New comment appears in table

**Viewing Comments:**
- Comments display in table with:
  - **Comment** - The text
  - **By** - User name (FirstName LastName (username))
  - **When** - Timestamp (full date/time and relative time)

**Refreshing Comments:**
- Click **Refresh** button in table header
- Icon spins while loading
- Comments reload from Elasticsearch

### For Developers

**Access Comments via Console:**
```javascript
// Get current comments
angular.element(document).injector().get('$scope').comments

// Get process ID
angular.element(document).injector().get('$scope').processId

// Get current user
angular.element(document).injector().get('$scope').currentUserDisplayName
```

---

## 📁 File Structure

```
outputs/
├── commentHistoryStencil_WITH_REFRESH.js
│   └── AngularJS controller with all functionality
│
├── commentHistoryStencil_WITH_SPINNER.html
│   └── HTML template with CSS spinner
│
├── README.md (this file)
│
└── [Other supporting files]
```

---

## 🔌 API Reference

### Elasticsearch Endpoints

#### Search Comments
```
POST /my_comments_one/_search

Query:
{
  "query": {
    "term": {
      "processInstanceId": "5040"
    }
  },
  "sort": [{ "timestamp": { "order": "desc" } }],
  "size": 100
}
```

#### Create Comment
```
POST /my_comments_one/_doc

Document:
{
  "processInstanceId": "5040",
  "comment": "This is my comment",
  "username": "John Smith (jsmith)",
  "timestamp": "2026-08-26T10:30:00.000Z"
}
```

### AngularJS Functions

- `loadComments()` - Fetch comments from Elasticsearch
- `submitComment()` - Save comment and reload
- `formatTime(isoString)` - Format timestamp
- `timeAgo(isoString)` - Relative time display

---

## 📊 Data Model

### Comment Document

```json
{
  "processInstanceId": "5040",
  "comment": "The actual comment text",
  "username": "John Smith (jsmith)",
  "timestamp": "2026-08-26T10:30:00.000Z"
}
```

**Fields:**
- **processInstanceId** (keyword) - Workflow instance ID
- **comment** (text) - Comment content (searchable)
- **username** (keyword) - User full name and ID
- **timestamp** (date) - ISO 8601 timestamp

---

## 🐛 Troubleshooting

### Issue: "Cannot reach Elasticsearch"

**Solution:**
1. Verify Elasticsearch is running:
   ```bash
   curl http://elasticsearch:9200
   ```
2. Check CORS is enabled
3. Update Elasticsearch URL in stencil properties

### Issue: "Could not determine process instance ID"

**Solution:**
1. Verify custom property `processInstanceId` is configured
2. Check the value is not empty
3. Review browser console logs

### Issue: Comments not appearing after submit

**Solution:**
```javascript
// Manually reload
angular.element(document).injector().get('$scope').loadComments()
```

### Issue: User shows "Unknown User"

**Verify:**
```javascript
// Check if task loaded
console.log(angular.element(document).injector().get('$scope').task)
```

---

## ⚠️ Known Issues

1. **CORS Required** - Elasticsearch CORS must be enabled
2. **No Backend Auth** - Uses Elasticsearch directly (POC only)
3. **No User Validation** - Any logged-in user can see all comments
4. **No Deletion** - Cannot delete comments via UI
5. **No Editing** - Cannot edit comments after submission

---

## 🔮 Future Enhancements

- [ ] Comment deletion functionality
- [ ] Comment editing functionality
- [ ] Comment @ mentions
- [ ] Comment attachments
- [ ] Permission-based access
- [ ] Backend service layer (Spring Boot)
- [ ] Pagination for large lists
- [ ] Rich text editor
- [ ] Search functionality
- [ ] Export comments

---

## 🔒 Security Considerations

### Current Implementation (POC)
⚠️ **NOT production-ready:**
- Direct Elasticsearch access
- No authentication layer
- No permission checks
- Not HTTPS required

### For Production:
✅ **Recommended:**
- Add backend API service
- Implement authentication
- Add role-based access control
- Use HTTPS only
- Sanitize inputs
- Add audit logging

---

## ✅ Testing Checklist

- [ ] Form loads without errors
- [ ] Can type comment
- [ ] Submit button disabled when empty
- [ ] Comment submits successfully
- [ ] Success message appears
- [ ] Comments reload after 5 seconds
- [ ] Refresh button shows spinner
- [ ] User name format correct
- [ ] Timestamps display correctly
- [ ] Works on mobile
- [ ] Error messages display

---

## 📝 Key Development Decisions

### Why Direct Elasticsearch?
- **POC Speed**: No backend service needed
- **Development Simplicity**: Direct HTTP calls
- **Learning**: Understand ES interaction
- **Trade-off**: Not secure for production

### Why 5-Second Delay?
- **ES Indexing**: Time for Elasticsearch to index documents
- **UX**: Show success message first, then reload
- **Network**: Handle network latency
- **Refresh Animation**: Visual feedback during wait

### Why CSS Spinner?
- **Professional**: Cleaner than emoji
- **Lightweight**: No image files
- **Accessible**: Works in all browsers
- **Customizable**: Easy to change colors/speed

### Why AngularJS 1.x?
- **Compatibility**: APS uses AngularJS 1.x
- **No Dependencies**: Uses built-in $http
- **Legacy Support**: Works with older browsers
- **Proven**: Stable for enterprise

---

## 🚀 Quick Deploy

```bash
# Single command deployment
cd /opt/alfresco/aps/tomcat/webapps/activiti-app/app/stencils/
cp commentHistoryStencil_WITH_REFRESH.js commentHistoryStencil_POC.js
cp commentHistoryStencil_WITH_SPINNER.html commentHistoryStencil_POC.html
/opt/alfresco/aps/tomcat/bin/shutdown.sh
sleep 5
/opt/alfresco/aps/tomcat/bin/startup.sh
```

---

## 📚 Resources

- [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/)
- [AngularJS Docs](https://angularjs.org/)
- [APS Documentation](https://docs.alfresco.com/process-services/latest/)
- [HTTP Standards](https://tools.ietf.org/html/rfc7231)

---

## 📞 Support

- Check browser console (F12) for `[CommentHistory]` logs
- All operations are logged with timestamps
- Review Elasticsearch connectivity
- Verify stencil custom properties

---

**Version**: 1.0.0  
**Status**: POC - Ready to Deploy  
**Last Updated**: August 26, 2026  
**Compatibility**: APS v26.x, Elasticsearch 7.x