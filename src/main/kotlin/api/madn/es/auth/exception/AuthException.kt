package api.madn.es.auth.exception

import api.madn.es.common.exception.CommonException

class EmailDuplicationException : CommonException("Email in use")
class UserCredentialNotFoundException : CommonException("User credential not found")
class UserNotFoundException : CommonException("User not found")
