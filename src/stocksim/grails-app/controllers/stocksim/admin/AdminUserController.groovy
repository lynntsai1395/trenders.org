package stocksim.admin

import stocksim.*

class AdminUserController {
    def userService
    
    def index() {
        render(view: "/admin/user/index")
    }
    
    def addUser() {
        def user = userService.addUser(params.email)
        redirect(action: "become", params: [user: user.getId()])
    }
    
    def become() {
        // create a new session
        def user = User.get(params.user)
        userService.become(flash, user)
        
        new UserAlert(type: "success", title: "You're now ${user.getEmail()}!", message: "You've assumed your new identity.").add(flash)
               
        redirect(action: "index")
    }
}