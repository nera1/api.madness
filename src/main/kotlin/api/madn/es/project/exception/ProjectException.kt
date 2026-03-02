package api.madn.es.project.exception

import api.madn.es.common.exception.CommonException

class UnauthorizedProjectException : CommonException("Authentication required to access a project")

class ProjectNotFoundException : CommonException("Project not found")

class ProjectAccessDeniedException : CommonException("You do not have access to this project")
