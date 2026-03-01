package api.madn.es.slide.exception

import api.madn.es.common.exception.CommonException

class UnauthorizedSlideException : CommonException("Authentication required to access a slide")
