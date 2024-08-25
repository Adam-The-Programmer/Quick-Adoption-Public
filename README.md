# Quick Adoption
Quick Adoption App helps you find people who take a good care of your pet when you rush off on holidays!

# <img src="https://cdn-icons-png.flaticon.com/512/3430/3430793.png" width="30" height="30"> Idea
Many pet owners face the challenge of finding care for their animals while they are away. Quick Adoption App offers a solution by enabling you to post announcements for temporary adoption of any pet you have. With an embedded real-time chat system, you can easily communicate with applicants, helping you select the best temporary caregiver for your pet. Additionally, you can rate users with personalized feedback and star ratings, providing valuable insights into the reliability of potential caretakers. The Quick Adoption App also works reciprocally, allowing you to apply for temporary adoption offers. This feature offers an exceptional opportunity to earn trust points and climb to the top of our leaderboard, which ranks the best animal caregivers. Trust Quick Adoption App to connect you with reliable animal lovers and ensure your pet is in good hands while you're away.

> [!TIP]
> Watch film

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/1uu265fjOso/0.jpg)](https://www.youtube.com/watch?v=1uu265fjOso)


# :sparkles: Features List
1. Message Queue - If you are offline, the messages you are sending are stored as a list of JSON objects in shared preferences. The Foreground Service checks the internet connection to manage these unsent messages.
2. Profile Editing - You can easily modify your profile data, e.g., location, image.
3. Creating, Modifying, and Deleting Announcements
4. Real-time Chat - Based on Firestore database changes listening.
5. Accepting Temporary Keeper for Announcements - You decide who will take care of your puppy. 🙂
6. Browsing and Adding Opinions of Users, Competing in Leaderboard
7. Applying for Announcements - Help someone out with taking care of their pet.

# :hourglass_flowing_sand: Coming Soon
1. Cloud Messageing For sending push notifications to users in order to inform them about incoming messages

# <img src="https://cdn-icons-png.flaticon.com/512/9243/9243391.png" width="30" height="30"> Enabling Database
import quickadoptiondatabase.sql file to your phpMyAdmin
![image](https://github.com/user-attachments/assets/fef19ab9-cb39-45e0-ae72-c1ac695a4eac)


# <img src="https://www.gstatic.com/devrel-devsite/prod/ve6d23e3d09b80ebb8aa912b18630ed278e1629b97aee6522ea53593a0024d951/firebase/images/touchicon-180.png" width="30" height="30"> Configure Firebase
Enable:
1. Firebase Authentication by Email/Passowrd with functionality of verification by email
2. Firestore Database. Start 2 collections: *users* and *chats*
3. Storage wit root folder *image*

# <img src="https://cdn-icons-png.flaticon.com/512/4380/4380600.png" width="30" height="30"> Run API Server
1. Create google cloud account
2. Set up new project "Quick Adoption API Server" and enter terminal
3. Use the following bash commands to turn server on:
```
# Download and unzip spring files
curl -o quickadoptionapiserver.zip http://test.lbiio.pl/quickadoptionapiserver.zip
unzip quickadoptionapiserver.zip

# Select directory
cd quickadoptionapiserver

# Add executive privileges for maven file
chmod +x mvnw

# Clean & Run Spring App
./mvnw clean
./mvnw spring-boot:run

# Deploy
gcloud app deploy

```
> [!TIP]
> if there is an error informing that 8080 port is already not available,</br>
> use the following to kill all processes associated with project:
> 
> ```kill -9 -1```

> [!WARNING]
> Remember to configure properties.xml file to access database correctly

# <img src="https://techcrunch.com/wp-content/uploads/2020/10/image9.png" width="30" height="30"> Runing Android Studio
> [!CAUTION]
> 1. Replace google-services.json with your file generated by firabse!
> 2. In ApiModule.kt change path to your api server (you will obtain it as you invoke gcloud app deploy)

