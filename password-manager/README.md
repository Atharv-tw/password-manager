# Secure Password Manager (Java)

A professional, feature-rich Password Manager built purely in Java using Object-Oriented Principles. This application securely stores your credentials in an encrypted vault, using industry-standard cryptography, protected by a single Master Password.

## 🌟 Key Features

* **Master Password Protection**: Uses **SHA-256** hashing to ensure your master password is never stored in plain text.
* **Encrypted Vault Storage**: Uses **AES-128 bit Cryptography** to securely encrypt all saved website credentials before saving them to disk (`passwords.enc`).
* **Auto Password Generator**: Built-in tool to generate highly secure, customizable passwords directly within the application.
* **Sleek Dark Mode UI**: A completely custom, dark-themed User Interface built natively with Java Swing (no external dependencies).
* **Instant Search**: A responsive search bar that instantly filters your saved credentials.
* **Quick Copy Functionality**: One-click copying of usernames and passwords straight to your system clipboard.
* **Portable Backup**: Your entire vault is safely packed into a single encrypted file `data/passwords.enc`. Exporting or backing up requires copying just that one file.

## 🛠️ Technology Stack

* **Language**: Java
* **UI Framework**: Java Swing / AWT (Custom Nimbus Dark Theme)
* **Security & Cryptography**: `java.security.MessageDigest` (SHA-256), `javax.crypto.Cipher` (AES)
* **Storage Structure**: Encrypted Java Serialization (`ObjectOutputStream`)

## 📂 Project Structure

```
password-manager/
├── README.md               # Project documentation
├── data/                   # (Created at runtime) Stores master.hash & passwords.enc
└── src/                    
    ├── Main.java                        # Application entry point
    ├── model/Credential.java            # Data model for passwords
    ├── security/EncryptionUtil.java     # Handles AES Encryption logic
    ├── security/HashingUtil.java        # Handles SHA-256 Hashing logic
    ├── security/PasswordGenerator.java  # Auto-generates passwords
    ├── data/DataManager.java            # Manages reading/writing from local storage
    ├── ui/LoginUI.java                  # UI login screen
    ├── ui/DashboardUI.java              # Main vault interface
    └── ui/Theme.java                    # Defines the dark mode theme
```

## 🚀 How to Run the Project

### Using VS Code or IntelliJ IDEA (Recommended)
1. Open the `password-manager` directory in your IDE.
2. If using VS Code, ensure you have the **"Extension Pack for Java"** installed.
3. Open `src/Main.java`.
4. Click the **Run ▶️** button located above `public static void main(String[] args)`.

### Using the Terminal
To run via terminal, you must have the Java Development Kit (JDK 8+) installed and configured in your System `PATH` variables.
```bash
# 1. Open Terminal inside the project folder
cd path/to/password-manager

# 2. Compile the source code into an 'out' folder
mkdir out
javac -d out src\model\*.java src\security\*.java src\data\*.java src\ui\*.java src\Main.java

# 3. Run the compiled application
java -cp out Main
```

## 🔒 Security Notice 

This application is designed specifically for academic demonstration of encryption and data structure mechanics in Java. It successfully limits unauthorized access mathematically via symmetric AES and secure hashing. However, for production/real-world reliance, consider using audited third-party implementations.

---
**Created for Academic College Submission**
