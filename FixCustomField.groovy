package com.aurea.utils

import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.fields.CustomField

def customFieldManager = ComponentAccessor.getCustomFieldManager()
def p1Snap = customFieldManager.getCustomFieldObjectByName("P1 Snapshot")
ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def issueManager = ComponentAccessor.getIssueManager();
def issue = issueManager.getIssueObject("SPEC-16221")
IssueIndexingService issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService.class);
issue.setCustomFieldValue(p1Snap, "https://google.com/p1s")
issueManager.updateIssue(user, issue, EventDispatchOption.ISSUE_UPDATED, Boolean.FALSE)
issueIndexingService.reIndex(issue)
issue.key

// https://docs.google.com/a/devfactory.com/spreadsheets/d/1SnDlpznwuHgFKHxeDb8kMDzsIRj3d-_MyU1a-XF_i2I/edit
https://docs.google.com/document/d/104OBis2OB_-DU03-BIQTPxTwyIt8_uKrNktKAdFh_Xo/edit
SPEC-8846