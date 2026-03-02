package api.madn.es.slide.exception

import api.madn.es.common.exception.CommonException

class UnauthorizedSlideException : CommonException("Authentication required to access a slide")

class SlideNotFoundException : CommonException("Slide not found")

class SlideAccessDeniedException : CommonException("You do not have access to this slide")
