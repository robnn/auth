package hu.robnn.auth.social.facebook

class FacebookUser(var name: String? = null,
                   var email: String? = null,
                   var picture: Picture? = null,
                   var first_name: String? = null,
                   var last_name: String? = null,
                   var id: String? = null)

class Picture(var data: Data? = null)

class Data(var height: Int? = null,
           var is_silhouette: Boolean? = null,
           var url: String? = null,
           var width: Int? = null)