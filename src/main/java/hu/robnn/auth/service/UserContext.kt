package hu.robnn.auth.service

import hu.robnn.auth.dao.model.User

class UserContext {
    companion object {
        var currentUser: User? = null
    }
}