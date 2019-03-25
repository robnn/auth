package hu.robnn.auth.social.google

class GoogleUser(var id: String? = null,
                 var email: String? = null,
                 var verified_email: Boolean? = null,
                 var name: String? = null,
                 var given_name: String? = null,
                 var family_name: String? = null,
                 var link: String? = null,
                 var picture: String? = null,
                 var gender: String? = null,
                 var locale: String? = null)