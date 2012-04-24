package stocksim
import javax.servlet.http.Cookie

class UserController {
    def userService
    
    def signup() {
        render(view: "/signup")
    }
    
    def signupStudent() {
        if (params.signup) { // TODO: redirect after POST
            def email = params.email
            def name = params.name
            def classID = params.classid
            
            def classroom = Classroom.findByClassCode(classID)
            
            if (! classroom) {
                render "That wasn't a valid class ID." // TODO: make this prettier
            } else {
                def user = new User(displayName: name, email: email, classroom: classroom)
                
                if (user.validate()) {
                    user.save()
                    userService.become(response, user)

                    redirect(mapping: "signupTeacherSuccess")
                } else {
                    render "Please use valid information." // TODO: prettier
                    
                    user.errors.allErrors.each { error ->
                        println error
                    }
                }
            }
        } else {
            render(view: "/signupStudent")
        }
    }
    
    def signupTeacher() {
        if (params.signup) { // TODO: redirect after POST
            def email = params.email
            def name = params.name
            
            def user = new User(displayName: name, email: email).save()
            userService.become(response, user)
            
            def classroom = userService.createClassroom(user)
            
            user.setClassroom(classroom)
            user.save()
            
            redirect(mapping: "signupTeacherSuccess")
        } else {
            render(view: "/signupTeacher")
        }
    }
    
    def signupTeacherSuccess() {
        render(view: "/signupTeacherSuccess")
    }
    
    def signupStudentSuccess() {
        render(view: "/signupStudentSuccess")
    }
    
    // TODO: email user on account creation
    // TODO: login (less important since permanent login upon account creation)
    // TODO: recover password via email
    
    def logout() {
        if (! params.s) { // silent?
            new UserAlert(type: "success", title: "You've logged out!", message: "Visit the home page to log in again.").add(flash)
        }
        
        def cookiesToRemove = ["user", "token"]
        
        cookiesToRemove.each { name ->
            def cookie = request.cookies.find { it.getName() == name }
            cookie.setMaxAge(0)
            cookie.setHttpOnly(true)
            cookie.setPath("/")
            cookie.setValue("")
            
            response.addCookie(cookie)
        }
        
        if (params.r) {
            redirect(params.r)
        } else {
            redirect(mapping: "home")
        }
        
        /*
        if (params.r) { // TODO: fix XSS vulnerability
            render("<script>window.location = \"" + params.r + "\";</script>")
        } else {
            render("<script>window.location = \"" + createLink(mapping: "home") + "\";</script>")
        }
        */
        
        render "You've been logged out. Redirecting..."
    }
}