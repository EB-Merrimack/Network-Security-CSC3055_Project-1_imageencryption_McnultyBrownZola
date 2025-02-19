# Network-Security-CSC3055_Project-1_imageencryption_McnultyBrownZola

## Overview

This project is an image encryption system built for CSC3055. It securely encrypts and shares images using a combination of AES (for the image data) and ElGamal encryption (for the AES keys). The application is built using Java Swing for the graphical user interface and uses BouncyCastle for cryptographic operations.

## Features

- **User Management:**  
  Add and manage users whose public keys are stored in a key ring (`users.json`).

- **Photo Encryption:**  
  Photos are encrypted with AES in CBC mode using PKCS5Padding. The AES key is encrypted using the user’s ElGamal public key.

- **Photo Export:**  
  To export (decrypt) a photo, the application automatically loads the corresponding private key from the `key_data` folder based on the username. The decrypted photo is then saved as a JPG file.

- **Automatic Updates:**  
  After adding a photo or a user, simply press the "Return to Main Menu" button to refresh and update the displayed lists.

## How to Use

1. **Adding a Photo:**
   - Open the *Add Photo* panel.
   - Enter your username and select a photo file.
   - Click "Upload Photo" to encrypt and upload the photo.
   - After the photo is added, press "Return to Main Menu" to update the list of photos.

2. **Adding a User:**
   - Use the designated user management panel to add a new user.
   - After adding the user, press "Return to Main Menu" to update the user list.

3. **Exporting (Decrypting) a Photo:**
   - Open the *Export Photo* panel.
   - Enter your username.
   - Select a photo from the dropdown list.
   - The application automatically loads your private key from the `key_data` folder (a file named `<username>_private.pem`).
   - Click "Export Photo" to decrypt the photo.
   - Save the decrypted file as a JPG (the application will remove the `.enc` extension and add `.jpg`).
   - After exporting, press "Return to Main Menu" to refresh the view.

## Project Structure

- **GUI Panels:**
  - `AddPhotoPanel.java`: Handles photo upload and encryption.
  - `ExportPhotoPanel.java`: Handles decryption (export) of photos.
  - `MainMenuPanel.java`: Main navigation menu for the application.

- **Encryption Utilities:**
  - `AESUtil.java`: Contains methods to generate AES keys/IV, encrypt data using AES, and decrypt data.
  - `ElGamalUtil.java`: Contains methods for encrypting and decrypting AES keys using ElGamal encryption.

- **Key Management:**
  - `KeyManager.java`: Generates, stores (in PEM format), and loads public/private keys.

- **Data Storage:**
  - User and photo metadata are stored in JSON files (e.g., `users.json` and `photos.json`).
  - Encrypted images are stored in the `imgdir` folder.
  - Private keys are stored in the `key_data` folder with filenames following the format `<username>_private.pem`.

## Setup and Running the Application

1. **Prerequisites:**
   - Java JDK (version 21 or higher recommended)
   - BouncyCastle cryptographic libraries
   - An IDE (e.g., Eclipse, IntelliJ IDEA) or command-line tools

2. **Setup:**
   - Clone or download the repository.
   - Make sure that the `imgdir` and `key_data` directories exist (the application will attempt to create them if they do not).
   - Build the project using your IDE or from the command line.

3. **Running the Application:**
   - Run the main class (e.g., `Main.java`).
   - Follow the on-screen instructions to add users, upload photos, and export (decrypt) photos.

## Important Notes

- **Return to Main Menu:**  
  After adding a photo or a user, press the "Return to Main Menu" button to refresh the application view and update the displayed lists.

- **Private Key Files:**  
  The application automatically loads your private key from the `key_data` folder. Ensure that your private key file is named `<username>_private.pem` (for example, `em_private.pem` for user "em").

- **Debugging:**  
  Debug logs are printed to the console, such as the IV length, the length of the Base64-decoded encrypted data, and the decrypted data length. These logs can help diagnose any issues with the encryption/decryption process.

## License

[Your License Information]

---

Feel free to update this README as your project evolves!
