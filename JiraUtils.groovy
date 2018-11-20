/**
 *  Add a group to a role in all projects
 */

import com.atlassian.jira.bc.projectroles.ProjectRoleService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.roles.ProjectRole
import com.atlassian.jira.util.SimpleErrorCollection
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.project.Project
import com.atlassian.jira.security.roles.ProjectRoleActor

def projectRoleService = ComponentAccessor.getComponent(ProjectRoleService);
def projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager);
def projectManager = ComponentAccessor.getComponent(ProjectManager);

ProjectRole projectRoleObject = projectRoleManager.getProjectRole(11400l)
List<Project> projects = projectManager.getProjectObjects();
def errorCollection = new SimpleErrorCollection();
Collection<String> actorCollection = new ArrayList<>();
actorCollection.add("RAM-Jira-Central-Validations");
log.error(projects.size())
projects.each { project ->
    projectRoleService.addActorsToProjectRole(actorCollection, projectRoleObject, project, ProjectRoleActor.GROUP_ROLE_ACTOR_TYPE, errorCollection)
}
log.error(errorCollection.getErrorMessages());