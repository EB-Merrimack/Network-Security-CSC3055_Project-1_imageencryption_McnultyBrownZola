class Photo {
    constructor(owner, fileName, iv, keyBlock) {
      this.owner = owner;
      this.fileName = fileName;
      this.iv = iv;
      this.keyBlock = keyBlock;
    }
  }
  
  class KeyEntry {
    constructor(user, keyData) {
      this.user = user;
      this.keyData = keyData;
    }
  }
  
  class User {
    constructor(id, publicKey) {
      this.id = id;
      this.publicKey = publicKey;
    }
  }
  
  class Photos {
    constructor() {
      this.photos = [];
    }
  
    addPhoto(photo) {
      this.photos.push(photo);
    }
  }
  
  class Users {
    constructor() {
      this.users = [];
    }
  
    addUser(user) {
      this.users.push(user);
    }
  }
  
  module.exports = { Photo, KeyEntry, User, Photos, Users };
  