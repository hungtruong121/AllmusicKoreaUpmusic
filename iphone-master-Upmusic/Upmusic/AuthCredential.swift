

/*
 Some of the Auth Credentials needs to be populated for the Sample build to work.
 
 Please follow the following steps to populate the valid AuthCredentials
 and copy it to AuthCredentials.swift file
 
 You will need to replace the following values:
 $KGOOGLE_CLIENT_ID
 Get the value of the CLIENT_ID key in the GoogleService-Info.plist file.
 
 $KFACEBOOK_APP_ID
 FACEBOOK_APP_ID is the developer's Facebook app's ID, to be used to test the
 'Signing in with Facebook' feature of Firebase Auth. Follow the instructions
 on the Facebook developer site: https://developers.facebook.com/docs/apps/register
 to obtain the id
 
 
 struct AuthCredentials {
 static let FACEBOOK_APP_ID = "$KFACEBOOK_APP_ID"
 static let GOOGLE_CLIENT_ID = "$KGOOGLE_CLIENT_ID"
 }
 
 */

import Foundation

struct AuthCredentials {
    static let FACEBOOK_APP_ID = "307280393178933"
    static let GOOGLE_CLIENT_ID = "644731469776-1f5beiqd2lfotu2khmcqrv2cn78qbj4d.apps.googleusercontent.com"
}
