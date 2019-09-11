package com.aurea.utils

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.user.ApplicationUser
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

def CURRENT_ON_CALL = "currentOnCall"
def PREVIOUS_ON_CALL = "previousOnCall"
def LAST_ASSIGNEE = "lastAssignee"
def NEXT_ASSIGNEE = "nextAssignee"

def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
MutableIssue mutableIssue = (MutableIssue) issue
def jsonSlurper = new JsonSlurper()
def customFieldManager = ComponentAccessor.customFieldManager
def issueManager = ComponentAccessor.issueManager
def issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService.class)
def customField = customFieldManager.getCustomFieldObjectByName("SaaSOps On-Call List")
def configIssue = issueManager.getIssueObject("NJPP-1209")
def customFieldString = (String) configIssue.getCustomFieldValue(customField)
if(customFieldString != null && !((String) customFieldString).trim().isEmpty()) {
    def onCallConfig = (Map) jsonSlurper.parseText(customFieldString)
    def newAssignee
    if(onCallConfig.containsKey(LAST_ASSIGNEE)) {
        def nextAssignee = (String) onCallConfig.get(NEXT_ASSIGNEE)
        def currentOnCall = (List<String>) onCallConfig.get(CURRENT_ON_CALL)
        if(currentOnCall == null || currentOnCall.isEmpty()) {
            // send email
            return
        }
        if(currentOnCall.contains(nextAssignee)) {
            newAssignee = nextAssignee
        } else {
            newAssignee = currentOnCall.get(0)
        }
        onCallConfig.put(PREVIOUS_ON_CALL, currentOnCall.collect())
        onCallConfig.put(LAST_ASSIGNEE, newAssignee)
        def index = currentOnCall.findIndexOf {it.equals(newAssignee)}
        if(index >= currentOnCall.size()){
            index = 0
        }
        onCallConfig.put(NEXT_ASSIGNEE, currentOnCall.get(index))
        mutableIssue.setAssignee((ApplicationUser) ComponentAccessor.userManager.getUserByName(newAssignee))
        issueManager.updateIssue(currentUser, mutableIssue, EventDispatchOption.ISSUE_UPDATED, Boolean.FALSE)
        configIssue.setCustomFieldValue(customField, JsonOutput.toJson(onCallConfig))
        issueManager.updateIssue(currentUser, configIssue, EventDispatchOption.ISSUE_UPDATED, Boolean.FALSE)
    }
}