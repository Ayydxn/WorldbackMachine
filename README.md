# Worldback Machine

![GitHub License](https://img.shields.io/github/license/Ayydxn/WorldbackMachine)
![GitHub Issues](https://img.shields.io/github/issues/Ayydxn/WorldbackMachine)
![GitHub Pull Requests](https://img.shields.io/github/issues-pr/Ayydxn/WorldbackMachine)

---

Worldback Machine is a Minecraft mod which adds support for cloud saving worlds using Google Drive.

---

## 🔽 Installation

As of currently, no builds of Worldback Machine are being released anywhere. Check back the mod when has been released.

---

## 🐛 Reporting Issues

You can report any bugs or issues you come across using the [issue tracker](https://github.com/Ayydxn/WorldbackMachine/issues). Before opening a new issue, please use the search tool to make sure your issue hasn't already been reported. Issues that are duplicates of one another or do not contain the necessary information needed to debug them may be closed.

---

## 🛠 Building From Sources

### 📃 Requirements

- **Java 21 JDK**
  - I recommend using the [Azul Zulu](https://www.azul.com) distribution as it is what I use to build the mod but, this isn't strictly required. You should be able to use whichever JDK distribution you want without issues.
- **A Google Cloud Project**
  - In order to be able to access and use the Google Drive, you'll need a Google Cloud project. You can follow [this guide](https://developers.google.com/workspace/drive/api/quickstart/java) to create one.

Like most Minecraft mods, Worldback Machine uses the standard Gradle project structure and can be compiled by simply running the default `build` task. After running the task, the build artifacts can be found in the `build/libs` directory.

In order to run the game from within your IDE, you'll need two more things: A Google OAuth 2.0 Client ID and a dotenv file.

Firstly, in the `resources` directory, create a directory called `auth`. Create a directory for the cloud provider you would like to use. For example, `google` or `onedrive`.
Then, simply put all of your necessary authentication files in that directory. For example, for Google Drive, your `credentials.json` will go in that folder.

Set the 'CREDENTIALS_FILE' to whatever you want. You can then follow the [Google Drive API Java Quickstart](https://developers.google.com/workspace/drive/api/quickstart/java) to a OAuth Client ID. Once you have it, put it to the location you have set in the 'CREDENTIALS_FILE' variable.

---

## 📃 License

Worldback Machine is licensed under the free and open-source license, GNU LGPLv3. For more information, please read the [license](https://choosealicense.com/licenses/lgpl-3.0/).