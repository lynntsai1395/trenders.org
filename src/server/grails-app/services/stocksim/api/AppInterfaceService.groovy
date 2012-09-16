package stocksim.api

import stocksim.*
import stocksim.api.category.*

class AppInterfaceService {
    def lyricService
    def userService
    
    def betaInterfaceService
    def classroomInterfaceService
    def userInterfaceService
    def portfolioInterfaceService
    def infoInterfaceService
    
    def generateResponse(def request, def category, def item, def action, def params) {
        def response = [:]
        def startTime = (new Date()).getTime()
        
        // add additional helper info to params
        params.info = [
            userAgent: request.getHeader("User-Agent"),
            ip: request.getRemoteAddr()
        ]
        
        // see if the request has an appropriate token, and if so, find the user
        // they're trying to login as
        def user = null
        def sessionToken = params.token
        
        if (sessionToken) {
            user = userService.getUserForSessionToken(sessionToken)
        }
        
        // process for a response
        def categoryService = getCategoryService(category)
        
        if (categoryService == null || item.startsWith("_") || ! (categoryService.hasProperty(item) || categoryService.hasProperty("_" + item))) {
            response.apiCode = AppInterface.codes.NOT_AVAILABLE
        } else {
            // does a closure exist for the item requested?
            def method
            
            def normalMethodName = item
            def authMethodName = "_" + normalMethodName
            def requiresAuth = false
            
            if (categoryService.hasProperty(authMethodName)) {
                method = categoryService[authMethodName]
                requiresAuth = true
            } else {
                method = categoryService[item]
            }
            
            if (method != null) {
                // does that method require authorization? (starts with semicolon)
                def authorized = requiresAuth ? (user != null) : true
                
                if (authorized) {
                    // mark the response as "ok" (API methods can override this later if
                    // they want)
                    response.apiCode = AppInterface.codes.OK
                    method(response, action, params, user)
                } else {
                    response.apiCode = AppInterface.codes.LOGIN_FIRST
                }
            } else {
                response.apiCode = AppInterface.codes.NOT_AVAILABLE
            }
        }
        
        // add standard stuff on all requests
        response.apiDate = (new Date()).toString()
        response.apiTime = (new Date()).getTime() - startTime
        response.apiLyric = lyricService.getLine()
        
        // feed back the response so it can be processed and output by some controller
        response
    }
    
    def getCategoryService(def category) {
        def categories = [
            beta: betaInterfaceService,
            classroom: classroomInterfaceService,
            user: userInterfaceService,
            portfolio: portfolioInterfaceService,
            info: infoInterfaceService
        ]
        
        categories[category]
    }
}