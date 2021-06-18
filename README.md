# Cuntr-App
 Cuntr app is a social countdown app. The user can enter the application and
create private, shared or public countdowns. 
Private countdowns are used for create reminders for users. Shared countdowns,
on the other hand, are there for users to share and follow together
various activities they have planned with their friends. Public
countdowns are open for everyone to see and are used to promote
events.

  When user tab to the discover fragment
he/she can see all user and public Cuntrs inside app. With help of search field
user can search user and Cuntrs with their names and follow them. When user follow another
user, all public Cuntrs of this user will be followed and showed on home page.
  
  After creation of a shared Cuntr, user can invite friends with their e-mail. If there is
a user with this e-mail address in the system, the invitation falls on that user's profile page.
User can accept or reject this invitation. If user accept, Cuntr will be followed and can be seen
on home page. Also in the deatils of an shared Cuntr, the user can see other users who have 
accepted the invitation.

 The application uses Firebase Firestore, Auth and Storage. Also,
Recycler Views, Fragments and Navigation Framework are used. Users can
securely register and login via Firebase Auth. They can also save
their profile pictures via Firebase Storage. Also, countdown
informations are kept on Firebase Firestore.
