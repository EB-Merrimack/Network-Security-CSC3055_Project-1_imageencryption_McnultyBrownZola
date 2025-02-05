const { Photo, KeyEntry, User, Photos, Users } = require('./src/json/data');
const fs = require('fs');
const path = require('path');

const photosFilePath = path.join(__dirname, './src/json/photos.json');
const usersFilePath = path.join(__dirname, './src/json/users.json');

// Load JSON data
function loadJsonFile(filePath) {
  if (fs.existsSync(filePath)) {
    const data = fs.readFileSync(filePath, 'utf8');
    return JSON.parse(data);
  }
  return { photos: [], users: [] };
}

// Save JSON data
function saveJsonFile(filePath, data) {
  const jsonData = JSON.stringify(data, null, 2);
  fs.writeFileSync(filePath, jsonData, 'utf8');
}

// Initialize data structures
const photosData = loadJsonFile(photosFilePath);
const usersData = loadJsonFile(usersFilePath);

const photos = new Photos();
const users = new Users();

// Deserialize JSON data into objects
photosData.photos.forEach(photo => {
  const keyBlock = photo.keyBlock.map(entry => new KeyEntry(entry.user, entry.keyData));
  photos.addPhoto(new Photo(photo.owner, photo.fileName, photo.iv, keyBlock));
});

usersData.users.forEach(user => {
  users.addUser(new User(user.id, user.publicKey));
});

// Perform operations (example)
const newPhoto = new Photo("Alice", "photo_123.jpg", "iv_example", [new KeyEntry("Bob", "keyData_example")]);
photos.addPhoto(newPhoto);

const newUser = new User("user_456", "public_key_example");
users.addUser(newUser);

// Serialize objects back to JSON
const updatedPhotosData = { photos: photos.photos };
const updatedUsersData = { users: users.users };

// Save updated JSON data to files
saveJsonFile(photosFilePath, updatedPhotosData);
saveJsonFile(usersFilePath, updatedUsersData);

console.log("JSON files updated successfully!");
