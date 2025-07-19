**Here we are illustrating all the updates which are required to increase the efficiency of SRUTI APPLICATION.**

# API Endpoints
_| HTTP Verbs | Endpoints | Action |_

| --- | --- | --- |
| POST | login/ | To login as new user in application |
| POST | change-password/ | To reset password after login |
| POST | forgot-password/ | For forget password while login |
| POST | forgot-password-with-otp/ | To get otp after clicking on forget password |
| POST | loginVerifyOTP/ | To verify the login with otp |
| POST | resend-otp/ | To resend the otp for login if user can't get it |

# Some of the code which are not following the "DRY Principle"



--> (isActivityRunning) Exception has been resolved in Warehouse,seller and Global for the AlertDialog is adding again and again in stack.

# In this SCAD2-21514 :- We are going to adding the code from Signature activity to SrutiSyncService so, that whenever the sync service running this Proof of pickup API also runs.